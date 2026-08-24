package com.example.weatherapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import com.example.weatherapp.network.AppConstants
import com.example.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// CLASS 3 IMPORTS
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Slider
import com.example.weatherapp.data.FeedbackRequest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    var city by remember { mutableStateOf("") }

    var cityResult by remember { mutableStateOf("City: --") }
    var tempResult by remember { mutableStateOf("Temperature: --") }
    var descResult by remember { mutableStateOf("Description: --") }
    var windResult by remember { mutableStateOf("Wind Speed: --") }
    var humidityResult by remember { mutableStateOf("Humidity: --") }
    var isLoading by remember { mutableStateOf(false) }

    // State variables for Feedback
    var currentCity by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var comment by remember { mutableStateOf("") }
    var feedbackResult by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            enabled = !isLoading,
            onClick = {
                val trimmedCity = city.trim()
                if (trimmedCity.isEmpty()) {
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        isLoading = true
                        try {
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.weatherApiService.getWeather(
                                    trimmedCity,
                                    AppConstants.API_KEY,
                                    AppConstants.UNITS
                                )
                            }

                            if (response.isSuccessful) {
                                val weather = response.body()
                                if (weather != null) {
                                    cityResult = "City: ${weather.name}"
                                    tempResult = "Temperature: ${weather.main.temp}"
                                    descResult = "Description: ${weather.weather[0].description}"
                                    windResult = "Wind Speed: ${weather.wind.speed} m/s"
                                    humidityResult = "Humidity: ${weather.main.humidity}%"

                                    currentCity = trimmedCity
                                }
                            } else {
                                when (response.code()) {
                                    404 -> Toast.makeText(context, "City not found. Check name and try again.", Toast.LENGTH_SHORT).show()
                                    401 -> Toast.makeText(context, "Invalid API key. Check AppConstants.kt", Toast.LENGTH_SHORT).show()
                                    else -> Toast.makeText(context, "Something went wrong (code ${response.code()})", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error. Check your connection.", Toast.LENGTH_SHORT).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (isLoading) "Loading..." else "Get Weather")
        }

        Text(cityResult, fontSize = 20.sp, modifier = Modifier.padding(top = 24.dp))
        Text(tempResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(descResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(windResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text(humidityResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))

        HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp))

        Text("How do you feel about today's weather?", fontSize = 16.sp)

        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toInt() },
            valueRange = 1f..5f,
            steps = 3
        )

        Text("Rating: $rating/5")

        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Leave comment about the weather...") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (currentCity.isEmpty()) {
                    Toast.makeText(context, "Please fetch weather for a city first", Toast.LENGTH_SHORT).show()
                } else if (comment.isBlank()) {
                    Toast.makeText(context, "Please leave a comment", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            val request = FeedbackRequest(city = currentCity, rating = rating, comment = comment)
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.feedbackApiService.submitFeedback(request)
                            }

                            // ASSIGNMENT 3
                            if (response.isSuccessful) {
                                feedbackResult = "Feedback submitted successfully!"
                                comment = ""
                                rating = 3
                            } else {
                                feedbackResult = "Failed to submit feedback."
                            }

                        } catch (e: Exception) {
                            feedbackResult = "Error submitting feedback. Check your connection."
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Submit Feedback")
        }

        Text(feedbackResult, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
    }
}