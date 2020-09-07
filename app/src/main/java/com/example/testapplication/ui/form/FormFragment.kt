package com.example.testapplication.ui.form

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentFormBinding
import com.example.testapplication.util.LogTags
import com.example.testapplication.util.ViewModelFactory

import com.example.testapplication.util.OnToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import java.io.File
import java.io.IOException
import java.util.*

class FormFragment : Fragment() {
    companion object {
        private const val PERMISSION_CODE = 1000
        private const val CAMERA_CODE = 1001
        private const val CLASS_NAME = "FormFragment/"
    }

    private lateinit var binding: FragmentFormBinding
    private lateinit var formViewModel: FormViewModel

    //interfaces
    private lateinit var onToast: OnToast

    private lateinit var absolutePhotoPath: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onToast = context as OnToast
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        setToolbar()
        formViewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory()).get(FormViewModel::class.java)

        //Observers
        formViewModel.formLiveData.observe(this, Observer {
            binding.textviewFormName.setText(it.name)
            binding.textviewFormDescription.setText(it.comment)
            changeFormImageButtonSrc(it.realPhotoPath)
            Log.i(LogTags.LOG_FORM.name, "$CLASS_NAME form updated")
        })

        formViewModel.sendFormResult.observe(this, Observer { result ->
            result?.let {
                showAlertDialog(it)
            }
        })

        //Listeners
        binding.imagebuttonFormPhoto.setOnClickListener {
            checkPermissionsAndStartCamera()
        }

        return binding.root
    }

    private fun setToolbar() {
        val toolbar = binding.root.custom_actionbar
        toolbar.title.setText(R.string.title_form)
        toolbar.send_form_imagebutton.visibility = View.VISIBLE

        toolbar.send_form_imagebutton.setOnClickListener {
            //send form to API
            saveForm()
            formViewModel.sendForm()
        }
    }

    private fun changeFormImageButtonSrc(photoPath: String?) {
        if (photoPath == null) {
            binding.imagebuttonFormPhoto.setImageResource(R.drawable.ic_form_imagebutton)
        } else {
            Picasso.get()
                    .load(File(photoPath))
                    .resizeDimen(R.dimen.form_photo_size, R.dimen.form_photo_size)
                    .error(R.drawable.ic_form_imagebutton)
                    .into(binding.imagebuttonFormPhoto)
        }
        Log.i(LogTags.LOG_FORM.name, "$CLASS_NAME form image changed")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            formViewModel.saveImageForm(absolutePhotoPath)
        }
    }

    private fun checkPermissionsAndStartCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startCamera()

                Log.i(LogTags.LOG_PERMISSIONS.name, "$CLASS_NAME GRANTED")
            } else {
                val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissionList, PERMISSION_CODE)

                Log.i(LogTags.LOG_PERMISSIONS.name, "$CLASS_NAME REQUEST")
            }
        } else {
            startCamera()

            Log.i(LogTags.LOG_PERMISSIONS.name, "$CLASS_NAME GRANTED")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LogTags.LOG_PERMISSIONS.name, "$CLASS_NAME GRANTED")
                    startCamera()
                }
            }
        }
    }

    private fun startCamera() {
        context?.let { context ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(context.packageManager).also {
                    val photo: File? = try {
                        createPhotoFile()
                    } catch (ex: IOException) {
                        Log.i(LogTags.LOG_STORAGE.name, "$CLASS_NAME FILE NOT CREATED")
                        null
                    }
                    photo?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(context, "com.example.testapplication", it)

                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(intent, CAMERA_CODE)

                        Log.i(LogTags.LOG_CAMERA.name, "$CLASS_NAME camera started")
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createPhotoFile(): File {
        val dir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileSuffix = ".jpg"
        val filePrefix: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        } else {
            "DEFAULT"
        }
        return File.createTempFile(filePrefix, fileSuffix, dir).apply {
            absolutePhotoPath = absolutePath

            Log.i(LogTags.LOG_STORAGE.name, "$CLASS_NAME FILE CREATED- $absolutePhotoPath")
        }
    }

    private fun saveForm() {
        formViewModel.saveTextForm(binding.textviewFormName.text.toString(), binding.textviewFormDescription.text.toString())
    }

    private fun showAlertDialog(message: String) {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.alert_dialog_positive_button)) { dialog, _ ->
                        formViewModel.clearDialogMessage()
                        dialog.dismiss()
                    }
                    .show()
        }
        Log.i(LogTags.LOG_ALERT_DIALOG.name, "$CLASS_NAME AlertDialog created")
    }

    override fun onStop() {
        super.onStop()
        saveForm()
    }
}
