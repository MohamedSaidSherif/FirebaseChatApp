package com.android.firebasechatapp.presentation.account_settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.databinding.DialogChangePhotoBinding
import com.android.firebasechatapp.permission.Permission
import com.android.firebasechatapp.permission.PermissionManager


class ChangePhotoDialog constructor(
    private val onImagePickedFromGallery: (Uri?) -> Unit = {}
) : DialogFragment() {

    private lateinit var binding: DialogChangePhotoBinding
    private val permissionManager = PermissionManager.from(this)

        private lateinit var imagePickLauncher: ActivityResultLauncher<String>
//    private lateinit var imagePickLauncher: ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        binding = DialogChangePhotoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListenerForViews()
    }

    private fun setUpListenerForViews() {
        imagePickLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            dismiss()
            onImagePickedFromGallery(it)
        }
        //Another way
//        imagePickLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                it.data?.let { intent ->
//                    Log.d(TAG, "setUpListenerForViews: URI: ${intent.data}")
//                    dismiss()
//                    onImagePickedFromGallery(intent.data)
//                } ?:kotlin.run {
//                    Log.e(TAG, "setUpListenerForViews: Data is Null")
//                }
//            }


        with(binding) {
            dialogChoosePhoto.setOnClickListener {
                permissionManager.request(Permission.Storage)
                    .checkPermission { granted ->
                        if (granted) {
                            imagePickLauncher.launch("image/*")
//                            val i = Intent()
//                            i.type = "image/*"
////                            i.action = Intent.ACTION_PICK
//                            i.action = Intent.ACTION_GET_CONTENT
//                            imagePickLauncher.launch(i)
                        }
                    }
            }
            dialogOpenCamera.setOnClickListener {
                permissionManager.request(Permission.Camera)
                    .checkPermission { granted ->
                        dismiss()
                        if (granted) {
                            findNavController().navigate(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToCameraFragment())
                        }
                    }
            }
        }
    }

    companion object {
        private val TAG = ChangePhotoDialog::class.simpleName.toString()
    }
}