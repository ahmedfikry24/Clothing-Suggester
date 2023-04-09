package com.example.clothingsuggester.utlis

import com.example.clothingsuggester.models.Temperature
import com.example.clothingsuggester.models.Weather
import com.example.clothingsuggester.models.WeatherStatus
import org.json.JSONObject

fun JSONObject.weatherParser(): Weather {
    val weatherStatusList = mutableListOf<WeatherStatus>()
    val weatherStatus = this.getJSONArray("weather")
    for (i in 0 until weatherStatus.length()) {
        val weather = weatherStatus[i] as JSONObject
        val status = weather.getString("main")
        val description = weather.getString("description")
        weatherStatusList.add(WeatherStatus(status, description))
    }
    val temperature = this.getJSONObject("main")
    val temp_min = temperature.getDouble("temp_min")
    val temp_max = temperature.getDouble("temp_max")

    return Weather(weatherStatusList, Temperature(temp_min, temp_max))
}