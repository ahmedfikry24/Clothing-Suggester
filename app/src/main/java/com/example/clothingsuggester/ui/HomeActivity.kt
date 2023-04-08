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

    //permission
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initValues()
        isSelectedImageBefore()
        initOnClickListeners()
        loadClothes()
    }

    private fun initValues() {
        SharedPrefManager.grtInit(this@HomeActivity)
        isSelectedImage = SharedPrefManager.isSelectedImage!!
        imageNumber = SharedPrefManager.imageNumber ?: 0
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
        val pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
                if (it != null)
                    saveClothes(it)
            }

        binding!!.selectClothesButton.setOnClickListener {
            if (checkMediaPermission()) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                requestPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun checkMediaPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@HomeActivity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun saveClothes(image: Uri) {
        SharedPrefManager.grtInit(this@HomeActivity).edit()
            .putString(
                if (imageNumber == 0) imageNumber.toString() else (imageNumber + 1).toString(),
                image.toString()
            ).apply()
        if (!isSelectedImage)
            SharedPrefManager.isSelectedImage = true
    }

    private fun loadClothes() {
        for (i in 0..imageNumber) {
            val image = SharedPrefManager.grtInit(this@HomeActivity).getString(i.toString(), null)
            if (!image.isNullOrBlank())
                clothesList.add(image)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}