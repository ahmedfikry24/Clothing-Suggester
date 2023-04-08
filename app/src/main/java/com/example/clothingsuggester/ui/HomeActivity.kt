package com.example.clothingsuggester.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.clothingsuggester.R
import com.example.clothingsuggester.databinding.ActivityHomeBinding
import com.example.clothingsuggester.utlis.SharedPrefManager

class HomeActivity : AppCompatActivity() {
    private var binding: ActivityHomeBinding? = null
    private val summerClothesList = mutableListOf<String>()
    private val winterClothesList = mutableListOf<String>()

    //shared pref values
    private var summerImageNumber: Int = 100
    private var winterImageNumber: Int = 0
    private var isSelectedImage = false

    //permissions
    private val requestMediaPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    // image pickers
    private var summerImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) saveClothes(it, false)

        }
    private var winterImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) saveClothes(it, true)

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
        summerImageNumber = SharedPrefManager.latSummerImageNumber!!
        winterImageNumber = SharedPrefManager.lastWinterImageNumber!!
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

    private fun checkMediaPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@HomeActivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun initOnClickListeners() {

        binding!!.selectClothesButton.setOnClickListener {
            if (checkMediaPermission()) {
                val dialog = AlertDialog.Builder(this@HomeActivity)
                dialog.setTitle("clothes category")
                    .setMessage("please select category what you want to add .")
                    .setPositiveButton("Summer", DialogInterface.OnClickListener { dialog, id ->
                        summerImagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    }).setNegativeButton("Winter", DialogInterface.OnClickListener { dialog, id ->
                        winterImagePicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    })
                dialog.create().show()
            } else {
                requestMediaPermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun saveClothes(image: Uri, isWinter: Boolean) {
        if (isWinter) {
            SharedPrefManager.getInit(this@HomeActivity).edit().putString(
                winterImageNumber.toString(),
                image.toString()
            ).apply()

            SharedPrefManager.lastWinterImageNumber = winterImageNumber + 1
            winterImageNumber = SharedPrefManager.lastWinterImageNumber!!

            Toast.makeText(
                this@HomeActivity,
                getString(R.string.image_add_done),
                Toast.LENGTH_SHORT
            ).show()

        } else {
            SharedPrefManager.getInit(this@HomeActivity).edit().putString(
                summerImageNumber.toString(),
                image.toString()
            ).apply()

            SharedPrefManager.latSummerImageNumber = summerImageNumber + 1
            summerImageNumber = SharedPrefManager.latSummerImageNumber!!

            Toast.makeText(
                this@HomeActivity,
                getString(R.string.image_add_done),
                Toast.LENGTH_SHORT
            ).show()
        }
        if (!isSelectedImage) SharedPrefManager.isSelectedImage = true
    }

    private fun loadClothes() {
        for (i in 100 until summerImageNumber) {
            val image = SharedPrefManager.getInit(this@HomeActivity).getString(i.toString(), null)

            if (!image.isNullOrBlank()) summerClothesList.add(image)

        }
        for (i in 0 until winterImageNumber) {
            val image = SharedPrefManager.getInit(this@HomeActivity).getString(i.toString(), null)

            if (!image.isNullOrBlank()) winterClothesList.add(image)
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