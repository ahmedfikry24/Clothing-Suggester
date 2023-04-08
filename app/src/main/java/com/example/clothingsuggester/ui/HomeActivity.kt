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
    private val clothesList = mutableListOf<String>()

    //shared pref values
    private var imageNumber: Int = 0
    private var isSelectedImage = false

    //permissions
    private val requestMediaPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) saveClothes(it)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        checkLocationPermission()
        initValues()
        isSelectedImageBefore()
        initOnClickListeners()
        loadClothes()
    }

    private fun initValues() {
        SharedPrefManager.getInit(this@HomeActivity)
        imageNumber = SharedPrefManager.imageNumber ?: 0
        isSelectedImage = SharedPrefManager.isSelectedImage!!
    }

    private fun isSelectedImageBefore() {
        if (isSelectedImage == true) {

            binding!!.clothesImage.isVisible = true
            binding!!.selectTextHint.isVisible = false

        } else {
            binding!!.clothesImage.isVisible = false
            binding!!.selectTextHint.isVisible = true
        }
    }

    private fun initOnClickListeners() {

        binding!!.selectClothesButton.setOnClickListener {
            if (checkMediaPermission()) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestMediaPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkMediaPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@HomeActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun saveClothes(image: Uri) {
        SharedPrefManager.getInit(this@HomeActivity).edit().putString(
            if (imageNumber == 0) imageNumber.toString() else (imageNumber + 1).toString(),
            image.toString()
        ).apply()
        if (!isSelectedImage) SharedPrefManager.isSelectedImage = true
    }

    private fun loadClothes() {
        for (i in 0..imageNumber) {
            val image = SharedPrefManager.getInit(this@HomeActivity).getString(i.toString(), null)
            if (!image.isNullOrBlank()) clothesList.add(image)
        }
    }

    private fun checkLocationPermission() {
        if (!(ActivityCompat.checkSelfPermission(
                this@HomeActivity, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@HomeActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        ) {
            requestLocationPermission.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}