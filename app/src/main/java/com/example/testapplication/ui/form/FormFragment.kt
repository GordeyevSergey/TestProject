package com.example.testapplication.ui.form

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentFormBinding
import com.example.testapplication.util.LogTags
import com.example.testapplication.util.ViewModelFactory

import com.example.testapplication.util.OnToast
import com.example.testapplication.util.PhotoUriPathConverter
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_toolbar.view.*

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

    private var cameraResultPhoto: Uri? = null

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
            changeFormImageButtonSrc(it.photo)
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

    private fun changeFormImageButtonSrc(uri: Uri?) {
        if (uri == null) {
            binding.imagebuttonFormPhoto.setImageResource(R.drawable.ic_form_imagebutton)
        } else {
            Picasso.get()
                    .load(uri)
                    .resizeDimen(R.dimen.form_photo_size, R.dimen.form_photo_size)
                    .error(R.drawable.ic_form_imagebutton)
                    .into(binding.imagebuttonFormPhoto)
        }
        Log.i(LogTags.LOG_FORM.name, "$CLASS_NAME form image changed")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            context?.let { context ->
                cameraResultPhoto?.let { uri ->
                    val realPath = PhotoUriPathConverter.convert(context, uri)
                    formViewModel.saveImageForm(uri, realPath)
                }
            }
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
        cameraResultPhoto = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraResultPhoto)
        startActivityForResult(cameraIntent, CAMERA_CODE)
        Log.i(LogTags.LOG_CAMERA.name, "$CLASS_NAME camera started")
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
