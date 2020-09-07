package com.example.testapplication.ui.form

import android.app.Activity
import android.content.Intent
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

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import java.io.File

class FormFragment : Fragment() {
    companion object {
        private const val CAMERA_CODE = 1001
        private const val CLASS_NAME = "FormFragment/"
    }

    private lateinit var binding: FragmentFormBinding
    private lateinit var formViewModel: FormViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        setToolbar()
        context?.let { context ->
            formViewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory(context)).get(FormViewModel::class.java)
        }
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
            startCamera()
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

    private fun changeFormImageButtonSrc(photo: File?) {
        if (photo == null) {
            binding.imagebuttonFormPhoto.setImageResource(R.drawable.ic_form_imagebutton)
        } else {
            Picasso.get()
                    .load(photo)
                    .resizeDimen(R.dimen.form_photo_size, R.dimen.form_photo_size)
                    .error(R.drawable.ic_form_imagebutton)
                    .into(binding.imagebuttonFormPhoto)
        }
        Log.i(LogTags.LOG_FORM.name, "$CLASS_NAME form image changed")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
            formViewModel.saveImageForm()
        }
    }

    private fun startCamera() {
        context?.let { context ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(context.packageManager).also {
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, formViewModel.createPhotoFileAndGetUri())
                    startActivityForResult(intent, CAMERA_CODE)

                    Log.i(LogTags.LOG_CAMERA.name, "$CLASS_NAME camera started")
                }
            }
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
