package com.non.abztest.ui.SignUpFragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import com.non.abztest.R
import com.non.abztest.adapter.PositionAdapter
import com.non.abztest.databinding.FragmentSignUpBinding
import com.non.abztest.model.UserPost
import com.non.abztest.network.ApiService
import com.non.abztest.repository.UserRepository
import com.non.abztest.utils.FileHelper
import com.non.abztest.utils.FullScreenDialogFragment
import com.non.abztest.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream


@AndroidEntryPoint
class SignUpFragment : Fragment(), PositionAdapter.OnPositionClickListener {
    private lateinit var binding: FragmentSignUpBinding

    private lateinit var pickPhotoLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private lateinit var positionsAdapter: PositionAdapter
    private var position: Int = 0
    private var selectedImageUri: Uri? = null

    private val mainViewModel: MainViewModel by viewModels({requireActivity()})

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    binding.textInputLayoutUpload.hint = FileHelper.getShortenedFileNameFromUri(requireContext(), selectedImageUri!!, 20, binding.uploadInput)
                } ?: Toast.makeText(requireContext(), "No photo selected", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Photo selection cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openPhotoPicker()
            } else {
                Toast.makeText(requireContext(), "Permission denied to access storage", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadButton.setOnClickListener {
            checkPermissionAndOpenPicker()
        }

        positionsAdapter = PositionAdapter(mainViewModel, this)
        binding.positionRecycleView.adapter = positionsAdapter

        mainViewModel.loadPositions()

        mainViewModel.positions.observe(viewLifecycleOwner) { positions ->
            positions?.let {
                positionsAdapter.submitList(it)
            }
        }

        mainViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                println(it)
            }
        }

        binding.submitBtn.setOnClickListener {
            val nameInput = binding.nameInput.text.toString()
            val emailInput = binding.emailInput.text.toString()
            val phoneInput = binding.phoneInput.text.toString()

            val positionId = position

            val textInputLayoutName = binding.textInputLayoutName
            val textInputLayoutEmail = binding.textInputLayoutEmail
            val textInputLayoutPhone = binding.textInputLayoutPhone
            val textInputLayoutUpload = binding.textInputLayoutUpload

            val selectedImageUri = this.selectedImageUri
            val photoFile = selectedImageUri?.let { FileHelper.getFileFromUri(it, requireContext()) }
            val user = UserPost(nameInput, emailInput, phoneInput, positionId, photoFile ?: File(""))

            if (!user.isValid()) {
                if (!user.isNameValid()) {
                    textInputLayoutName.error = "Required field"
                } else {
                    textInputLayoutName.error = null
                }

                if (!user.isEmailValid()) {
                    textInputLayoutEmail.error = "Email is required or invalid"
                } else {
                    textInputLayoutEmail.error = null
                }

                if (!user.isPhoneValid()) {
                    textInputLayoutPhone.error = "Required field or invalid phone format"
                } else {
                    textInputLayoutPhone.error = null
                }

                if (!user.isPhotoValid()) {
                    textInputLayoutUpload.error = "Photo is required and must be a valid JPEG file"
                } else {
                    textInputLayoutUpload.error = null
                }

                if (!user.isPositionIdValid()) {
                    Toast.makeText(requireContext(), "Please select a valid position", Toast.LENGTH_SHORT).show()
                }

            } else {
                textInputLayoutName.error = null
                textInputLayoutEmail.error = null
                textInputLayoutPhone.error = null
                textInputLayoutUpload.error = null

                if (photoFile != null) {
                    lifecycleScope.launch {
                        mainViewModel.registerUser(user, photoFile) { response ->
                            response?.let {
                                if (it.isSuccessful) {
                                    mainViewModel.clearAndReloadUsers()
                                    FullScreenDialogFragment(R.layout.dialog_signup_success)
                                        .show(parentFragmentManager, "FullScreenDialogSuccess")
                                    findNavController().navigate(R.id.nav_users)
                                } else {
                                    when (it.code()) {
                                        409 -> {
                                            FullScreenDialogFragment(R.layout.dialog_signup_failed)
                                                .show(parentFragmentManager, "FullScreenDialogFailed")
                                        }
                                        201 -> {
                                            FullScreenDialogFragment(R.layout.dialog_signup_success)
                                                .show(parentFragmentManager, "FullScreenDialogServerError")
                                        }
                                        else -> {

                                        }
                                    }
                                }
                            } ?: run {
                                FullScreenDialogFragment(R.layout.dialog_signup_failed)
                                    .show(parentFragmentManager, "FullScreenDialogFailed")
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Please select a photo to upload", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return binding.root
    }

    private fun checkPermissionAndOpenPicker() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openPhotoPicker()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(requireContext(), "Storage access is needed to pick a photo", Toast.LENGTH_SHORT).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        } else {
            openPhotoPicker()
        }
    }

    private fun openPhotoPicker() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).takeIf {
            it.resolveActivity(requireActivity().packageManager) != null
        }

        val galleryIntent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }

        val chooserIntent = Intent(Intent.ACTION_CHOOSER).apply {
            putExtra(Intent.EXTRA_INTENT, galleryIntent)
            cameraIntent?.let {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(it))
            }
            putExtra(Intent.EXTRA_TITLE, "Select Image")
        }
        pickPhotoLauncher.launch(chooserIntent)
    }


    override fun onPositionClicked(position: Int) {
        val clickedPosition = mainViewModel.positions.value?.get(position)
        clickedPosition?.let {
            this.position = it.id
        }
    }
}