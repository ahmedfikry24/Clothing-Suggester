package com.example.clothingsuggester.models

data class Weather(
    val weatherState: List<WeatherStatus>,
    val temperature: Temperature,
)

data class WeatherStatus(
    val status: String,
    val description: String
)

data class Temperature(
    val temp_min: Double,
    val temp_max: Double
)