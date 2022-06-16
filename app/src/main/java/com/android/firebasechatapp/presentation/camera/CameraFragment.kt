package com.android.firebasechatapp.presentation.camera

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.firebasechatapp.databinding.FragmentCameraBinding
import com.android.firebasechatapp.presentation.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setUpListenerForViews(view)
    }

    private fun initViews() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        outputDirectory = MainActivity.getOutputDirectory(requireContext())
    }

    private fun setUpListenerForViews(view: View) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(view, cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))

        binding.cameraCaptureButton.setOnClickListener {
            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCapture.takePicture(outputFileOptions, Executors.newSingleThreadExecutor(),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException) {
                        // insert your code here.
                        error.printStackTrace()
                    }

                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        println("outputFileResults: ${outputFileResults.savedUri.toString()}")
                        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                            findNavController().navigate(
                                CameraFragmentDirections.actionCameraFragmentToAccountSettingsFragment(
                                    outputFileResults.savedUri.toString()
                                )
                            )
                        }
                    }
                })
        }
    }

    private fun bindPreview(view: View, cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        imageCapture = ImageCapture.Builder()
            .setTargetRotation(view.display.rotation)
            .build()

        preview.setSurfaceProvider(binding.previewView.surfaceProvider)

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()

        var camera = cameraProvider.bindToLifecycle(
            this as LifecycleOwner,
            cameraSelector,
            imageCapture,
            preview
        )
    }

    companion object {

        private const val TAG = "CameraFragment"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0

        /** Helper function used to create a timestamped file */
        private fun createFile(baseFolder: File, format: String, extension: String) =
            File(
                baseFolder, SimpleDateFormat(format, Locale.US)
                    .format(System.currentTimeMillis()) + extension
            )
    }
}