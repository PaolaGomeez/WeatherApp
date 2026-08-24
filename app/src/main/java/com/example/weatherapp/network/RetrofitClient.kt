package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // "by lazy" = crea esto solo cuando se accede por primera vez
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }

    val feedbackApiService: FeedbackApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.FEEDBACK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeedbackApiService::class.java)
    }
}