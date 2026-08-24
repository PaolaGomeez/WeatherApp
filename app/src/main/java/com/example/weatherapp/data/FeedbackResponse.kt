package com.example.weatherapp.data

// This data class represents what APIDog sends back after we POST
// APIDog returns minimal data - we just need to know if it succeeded
data class FeedbackResponse(
    val message: String? = null, // optional success message
    val success: Boolean? = null // optional success flag
)