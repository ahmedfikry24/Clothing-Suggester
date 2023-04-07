package com.example.clothingsuggester.api

import okhttp3.OkHttpClient
import okhttp3.Request

class ApiManager {

    private var client: OkHttpClient? = null
    private val API_KEY = "9dd042cd5c654b4f93a05513230704"


    fun getInit(country: String): OkHttpClient {

        if (client == null) {
            client = OkHttpClient()
            val request = Request.Builder()
                .url("http://api.weatherapi.com/v1/current.json?key=$API_KEY&q=$country")
                .build()
            client!!.newCall(request)
        }
        return client!!
    }

}