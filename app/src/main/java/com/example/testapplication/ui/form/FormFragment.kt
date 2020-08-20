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

import com.example.testapplication.util.OnToast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class FormFragment : Fragment() {
    private lateinit var binding: FragmentFormBinding
    private lateinit var formViewModel: FormViewModel
    //interfaces
    private lateinit var onToast: OnToast

    private val PERMISSION_CODE = 1000
    private val CAMERA_CODE = 1001
    private var cameraResultPhoto: Uri? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onToast = context as OnToast
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        setToolbar()
        formViewModel = ViewModelProviders.of(requireActivity()).get(FormViewModel::class.java)

        //Observers
        formViewModel.formLiveData.observe(this, Observer {
            binding.textviewFormName.setText(it.title)
            binding.textviewFormDescription.setText(it.description)
            changeFormImageButtonSrc(it.photo)
        })

        formViewModel.sendFormResult.observe(this, Observer {
            showAlertDialog(it)
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
        Log.i(LogTags.LOG_FORM_IMAGEBUTTON_SRC_CHANGED.name, uri.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            cameraResultPhoto?.let {
                formViewModel.saveImageForm(it)
                Log.i(LogTags.LOG_CAMERA_RESULT_SUCCESSFUL.name, it.toString())
            }
        }
    }

    private fun checkPermissionsAndStartCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context?.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    context?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startCamera()

                Log.i(LogTags.LOG_PERMISSIONS_GRANTED.name, LogTags.LOG_PERMISSIONS_GRANTED.logMessage)
            } else {
                val permissionList = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissionList, PERMISSION_CODE)

                Log.i(LogTags.LOG_PERMISSIONS_REQUEST.name, LogTags.LOG_PERMISSIONS_REQUEST.logMessage)
            }
        } else {
            startCamera()

            Log.i(LogTags.LOG_PERMISSIONS_GRANTED.name, LogTags.LOG_PERMISSIONS_GRANTED.logMessage)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LogTags.LOG_PERMISSIONS_GRANTED.name, LogTags.LOG_PERMISSIONS_GRANTED.logMessage)
                    startCamera()
                }
            }
        }
    }

    private fun startCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Pic")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Camera pic")
        cameraResultPhoto = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraResultPhoto)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }

    private fun saveForm() {
        formViewModel.saveTextForm(title = binding.textviewFormName.text.toString(), desctiption = binding.textviewFormDescription.text.toString())
    }

    private fun showAlertDialog(message: String) {
        context?.let {
            AlertDialog.Builder(it)
                    .setTitle(getString(R.string.alert_dialog_title))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.alert_dialog_positive_button)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    override fun onStop() {
        super.onStop()
        saveForm()
    }
}
