package com.mawumbo.storyapp.ui.addstory

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mawumbo.storyapp.util.getFileFromUri
import com.mawumbo.storyapp.util.reduceFileImage
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mawumbo.storyapp.databinding.FragmentAddStoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!

    private var imageFile: File? = null

    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraXButton.setOnClickListener {
            findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToCameraFragment())
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.uploadButton.setOnClickListener {
            uploadImage()
            val uiState = viewModel.uiState
            lifecycleScope.launch {
                uiState.collect {
                    if (it.navigateToHome == true) {
                        findNavController().navigate(AddStoryFragmentDirections.actionAddStoryFragmentToHomeFragment())
                        Toast.makeText(
                            requireContext(),
                            "Story Upload Success",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.navigatedToHome()
                    }
                    it.error?.let { error ->
                        Snackbar.make(
                            requireView(),
                            error,
                            Snackbar.LENGTH_SHORT
                        ).addCallback(
                            object : Snackbar.Callback() {
                                override fun onDismissed(
                                    transientBottomBar: Snackbar?,
                                    event: Int
                                ) {
                                    super.onDismissed(transientBottomBar, event)
                                    viewModel.errorShown()
                                }
                            }
                        ).show()
                    }
                }

            }
        }

        setFragmentResultListener("requestImage") { requestKey, bundle ->
            bundle.getString("responseImage")?.let { imageFile = File(it) }
            if (imageFile != null) {
                Glide.with(requireContext())
                    .load(imageFile)
                    .into(binding.previewImageView)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    activity,
                    "Doesn't have permission!",
                    Toast.LENGTH_SHORT
                ).show()
                activity?.finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireActivity(),
            it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = getFileFromUri(uri, requireContext())
                imageFile = myFile
                if (imageFile != null) {
                    Glide.with(requireContext())
                        .load(imageFile)
                        .into(binding.previewImageView)
                }
            }
        }
    }

    private fun uploadImage() {
        val description = binding.edtDescription.text.toString()

        if (imageFile != null && description.isNotEmpty()) {
            val file = reduceFileImage(imageFile as File)
            viewModel.uploadImage(file, description)

        } else {
            when {
                (description.isEmpty()) -> {
                    Toast.makeText(
                        requireContext(),
                        "Fill your description!",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                (imageFile == null) -> {
                    Toast.makeText(
                        requireContext(),
                        "Fill your image!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    companion object {

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}