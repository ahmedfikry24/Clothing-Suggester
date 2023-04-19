package com.example.clothingsuggester.api

import com.example.clothingsuggester.utlis.Constant
import okhttp3.*
import java.io.IOException

class ApiManager {
    private val client = OkHttpClient()

    fun getCurrentWeather(Latitude: Double, Longitude: Double, callback: Callback) {
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
                callback.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.onResponse(call, response)
            }
        })
    }
}