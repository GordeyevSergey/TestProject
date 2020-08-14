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
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentFormBinding
import com.example.testapplication.util.LogTags

import com.example.testapplication.util.OnToast
import com.squareup.picasso.Picasso

class FormFragment : Fragment() {
    //ui
    private lateinit var binding: FragmentFormBinding
    private lateinit var formViewModel: FormViewModel

    //interfaces
    private lateinit var onToast: OnToast

    private val PERMISSION_CODE = 1000
    private val CAMERA_CODE = 1001
    private var camera_pic_result: Uri? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onToast = context as OnToast
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        formViewModel = ViewModelProviders.of(this).get(FormViewModel::class.java)

        binding.imagebuttonFormPhoto.setOnClickListener {
            checkPermissionsAndStartCamera()
        }

        formViewModel.imageLiveData.observe(this, Observer {
            changeFormImageButtonSrc(it)
        })

        return binding.root
    }

    private fun changeFormImageButtonSrc(uri: Uri) {
        Picasso.get()
                .load(uri)
                .into(binding.imagebuttonFormPhoto)

        Log.i(LogTags.LOG_FORM_IMAGEBUTTON_SRC_CHANGED.name, uri.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            camera_pic_result?.let {
                formViewModel.setImageToLd(it)
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
        camera_pic_result = context?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, camera_pic_result)
        startActivityForResult(cameraIntent, CAMERA_CODE)
    }
}
