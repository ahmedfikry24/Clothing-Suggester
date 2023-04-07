package com.example.clothingsuggester.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.clothingsuggester.databinding.ActivityHomeBinding
import com.example.clothingsuggester.utlis.SharedPrefManager

class HomeActivity : AppCompatActivity() {
    var binding: ActivityHomeBinding? = null
    var imageNumber: Int = 0
    val clothesList = mutableListOf<String>()
    var isSelectedImage = false
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
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                saveClothes(it)
        }

        binding!!.selectClothesButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
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
    }

    private fun isSelectedImageBefore() {
        isSelectedImage = SharedPrefManager.isSelectedImage!!
        if (isSelectedImage == true) {

            binding?.clothesImage?.isVisible = true
            binding?.selectTextHint?.isVisible = false

        } else {
            binding?.clothesImage?.isVisible = false
            binding?.selectTextHint?.isVisible = true
        }
    }

    private fun loadClothes() {
        for (i in 0..loadImageNumber()) {
            val image = SharedPrefManager.grtInit(this@HomeActivity).getString(i.toString(), null)
            println(image)
            if (!image.isNullOrBlank())
                clothesList.add(image)
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}