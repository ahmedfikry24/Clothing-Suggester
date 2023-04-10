package com.example.clothingsuggester.ui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import com.example.clothingsuggester.R
import com.example.clothingsuggester.databinding.ActivityHomeBinding
import com.example.clothingsuggester.models.Weather
import com.example.clothingsuggester.utlis.Constant
import com.example.clothingsuggester.utlis.SharedPrefManager
import com.example.clothingsuggester.utlis.weatherParser
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDate

class HomeActivity : AppCompatActivity() {
    private var binding: ActivityHomeBinding? = null
    private val summerClothesList = mutableListOf<String>()
    private val winterClothesList = mutableListOf<String>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val client = OkHttpClient()

    //shared pref values
    private var summerImageNumber: Int = 100
    private var winterImageNumber: Int = 0
    private var isSelectedImage = false
    private var lastSummerImage = ""
    private var lastWinterImage = ""

    //permissions
    private val requestMediaPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    var locationManager : LocationManager? = null


    // image pickers
    private var summerImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                saveClothes(it, false)
                isSelectedImageBefore()
                getCurrentLocation()
            }

        }
    private var winterImagePicker =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
            if (it != null) {
                saveClothes(it, true)
                isSelectedImageBefore()
                getCurrentLocation()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        initValues()
        checkGpsEnabled()
        isSelectedImageBefore()
        initOnClickListeners()
    }

    private fun initValues() {
        SharedPrefManager.getInit(this@HomeActivity)
        summerImageNumber = SharedPrefManager.latSummerImageNumber!!
        winterImageNumber = SharedPrefManager.lastWinterImageNumber!!
        isSelectedImage = SharedPrefManager.isSelectedImage!!
        lastSummerImage = SharedPrefManager.lastSummerImage ?: ""
        lastWinterImage = SharedPrefManager.lastWinterImage ?: ""
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@HomeActivity)
       locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    private fun isSelectedImageBefore(): Boolean {
        isSelectedImage = SharedPrefManager.isSelectedImage!!
        if (isSelectedImage == true) {
            binding!!.clothesImage.isVisible = true
            binding!!.selectTextHint.isVisible = false
            loadClothes()
        } else {
            binding!!.clothesImage.isVisible = false
            binding!!.selectTextHint.isVisible = true
        }
        return isSelectedImage
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
                dialog.show()
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
            showToast()
            loadClothes()
        } else {
            SharedPrefManager.getInit(this@HomeActivity).edit().putString(
                summerImageNumber.toString(),
                image.toString()
            ).apply()
            SharedPrefManager.latSummerImageNumber = summerImageNumber + 1
            summerImageNumber = SharedPrefManager.latSummerImageNumber!!
            showToast()
            loadClothes()

        }
        if (!isSelectedImage) SharedPrefManager.isSelectedImage = true
    }

    private fun loadClothes() {
        for (i in 100 until summerImageNumber) {
            val image =
                SharedPrefManager.getInit(this@HomeActivity).getString(i.toString(), null)
            if (!image.isNullOrBlank() && image != lastSummerImage) summerClothesList.add(image)
        }
        for (i in 0 until winterImageNumber) {
            val image =
                SharedPrefManager.getInit(this@HomeActivity).getString(i.toString(), null)
            if (!image.isNullOrBlank() && image != lastWinterImage) winterClothesList.add(image)
            println(image)
        }
    }

    private fun showToast() {
        Toast.makeText(
            this@HomeActivity,
            getString(R.string.image_add_done),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@HomeActivity, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkGpsEnabled() {
        if (checkLocationPermission()) {
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableGPS()
            } else {
                getCurrentLocation()
            }
        } else {
            requestLocationPermission.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
            if (!locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                enableGPS()
            }

        }
    }

    private fun enableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onResume() {
        getCurrentLocation()
        super.onResume()
    }

    private fun getCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        getCurrentWeather(location.latitude, location.longitude)
                    }
                }
        }
    }

    private fun getCurrentWeather(Latitude: Double, Longitude: Double) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host("api.openweathermap.org")
            .addPathSegment("/data/2.5/weather")
            .addQueryParameter("lat", "$Latitude")
            .addQueryParameter("lon", "$Longitude")
            .addQueryParameter("appid", Constant.API_OPEN_WEATHER_KEY)
            .build()

        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(this@HomeActivity, "${e.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string().let {
                    val result = JSONObject(it!!).weatherParser()
                    runOnUiThread {
                        bindingResponseData(result)
                    }
                }
            }
        })
    }

    private fun convertKelvinToCelsius(kelvin: Double): Double {
        return kelvin - 273.15
    }

    private fun getAverageTemperature(temp_min: Double, temp_max: Double): Int {

        return convertKelvinToCelsius(temp_min).plus(convertKelvinToCelsius(temp_max)).div(2)
            .toInt()
    }

    private fun bindingResponseData(weather: Weather) {
        val averageTemp = getAverageTemperature(
            weather.temperature.temp_min,
            weather.temperature.temp_max
        )
        binding?.apply {
            weatherStatus.text = weather.weatherState[0].status
            weatherDay.text = LocalDate.now().dayOfWeek.name.lowercase()
            weatherTemp.text = averageTemp.toString()
        }
        getTheSuitableClothes(averageTemp)
    }

    private fun getTheSuitableClothes(temperature: Int) {
        when (temperature) {
            in 0..20 -> {
                binding?.apply {
                    mainConstraint.setBackgroundResource(R.drawable.shape_winter_background)
                    weatherIcon.setBackgroundResource(R.drawable.raining)
                    if (winterClothesList.isNotEmpty()) {
                        val randomImage = winterClothesList.shuffled()[0]
                        clothesImage.setImageURI(Uri.parse(randomImage))
                        SharedPrefManager.lastWinterImage = randomImage
                    }
                }
            }

            else -> {
                binding?.apply {
                    mainConstraint.setBackgroundResource(R.drawable.shape_summer_background)
                    weatherIcon.setBackgroundResource(R.drawable.sunny)
                    if (summerClothesList.isNotEmpty()) {
                        val randomImage = summerClothesList.shuffled()[0]
                        clothesImage.setImageURI(Uri.parse(randomImage))
                        SharedPrefManager.lastSummerImage = randomImage
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}