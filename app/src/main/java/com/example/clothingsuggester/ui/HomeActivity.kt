package com.example.clothingsuggester.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.clothingsuggester.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    var binding: ActivityHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initOnClickListeners()
    }


    private fun initOnClickListeners() {
        val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        }

        binding!!.selectClothesButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}