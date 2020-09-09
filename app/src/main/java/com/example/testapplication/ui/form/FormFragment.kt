package com.example.testapplication.ui.form

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.testapplication.R
import com.example.testapplication.databinding.FragmentFormBinding
import com.example.testapplication.util.ViewModelFactory

import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.custom_toolbar.view.*
import timber.log.Timber
import java.io.File

class FormFragment : Fragment() {
    private lateinit var binding: FragmentFormBinding
    private lateinit var formViewModel: FormViewModel

    private lateinit var activityResultLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_form, container, false)
        setToolbar()
        activity?.application?.let {
            formViewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory(it)).get(FormViewModel::class.java)
        }

        //Observers
        formViewModel.formLiveData.observe(viewLifecycleOwner, Observer {
            binding.textviewFormName.setText(it.name)
            binding.textviewFormDescription.setText(it.comment)
            changeFormImageButtonSrc(it.photo)
            Timber.d("form updated")
        })
        formViewModel.sendFormResult.observe(viewLifecycleOwner, Observer { result ->
            result?.let {
                showAlertDialog(it)
            }
            Timber.d("result of sending catched: $result")
        })

        //Listeners
        binding.imagebuttonFormPhoto.setOnClickListener {
            activityResultLauncher.launch(formViewModel.createPhotoFileAndGetUri())
            Timber.d("image button clicked")
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            formViewModel.saveImageForm()
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
            Timber.d("image button src default")
        } else {
            Picasso.get()
                    .load(photo)
                    .resizeDimen(R.dimen.form_photo_size, R.dimen.form_photo_size)
                    .error(R.drawable.ic_form_imagebutton)
                    .into(binding.imagebuttonFormPhoto)
            Timber.d("image button src changed")
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
        Timber.d("alert dialog message: $message")
    }

    override fun onStop() {
        super.onStop()
        saveForm()
    }
}
