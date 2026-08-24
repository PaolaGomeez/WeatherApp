package com.example.weatherapp.data

data class WeatherResponse (
    val name: String,           // city name - matches "name" key in JSON
    val main: Main,             // nested objet -- contains temp and humidity
    val weather: List<Weather>, // array of weather conditions
    val wind: Wind              // nested object - contains wind speed
)

data class Main (
    val temp: Double,
    val humidity: Int
)

data class Weather (
    val description: String
)

data class Wind (
    val speed: Double
)