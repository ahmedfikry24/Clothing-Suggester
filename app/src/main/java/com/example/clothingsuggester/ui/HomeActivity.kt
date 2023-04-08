package com.example.clothingsuggester.ui

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.clothingsuggester.databinding.ActivityHomeBinding
import com.example.clothingsuggester.utlis.SharedPrefManager

class HomeActivity : AppCompatActivity() {
    private var binding: ActivityHomeBinding? = null
    private var imageNumber: Int = 0
    private val clothesList = mutableListOf<String>()
    private var isSelectedImage = false

    //permissions
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        loadImageNumber()
        initOnClickListeners()
        loadClothes()
        isSelectedImageBefore()
    }


    private fun initOnClickListeners() {
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                if (it != null)
                    saveClothes(it)
            }

        binding!!.selectClothesButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this@HomeActivity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun loadImageNumber(): Int {
        imageNumber = SharedPrefManager.imageNumber ?: 0
        return imageNumber
    }

    private fun saveClothes(image: Uri) {
        SharedPrefManager.grtInit(this@HomeActivity).edit()
            .putString(
                if (loadImageNumber() == 0) imageNumber.toString() else (imageNumber + 1).toString(),
                image.toString()
            ).apply()
        SharedPrefManager.isSelectedImage = true
    }

    private fun isSelectedImageBefore() {
        isSelectedImage = SharedPrefManager.isSelectedImage!!
        if (isSelectedImage == true) {

            binding!!.clothesImage.isVisible = true
            binding!!.selectTextHint.isVisible = false

        } else {
            binding!!.clothesImage.isVisible = false
            binding!!.selectTextHint.isVisible = true
        }
    }

    private fun loadClothes() {
        for (i in 0..loadImageNumber()) {
            val image = SharedPrefManager.grtInit(this@HomeActivity).getString(i.toString(), null)
            if (!image.isNullOrBlank())
                clothesList.add(image)
        }
    }

    private fun getLocationPermission() {

    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}