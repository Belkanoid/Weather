package com.belkanoid.weather.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.belkanoid.weather.R
import com.belkanoid.weather.data.repository.WeatherRepository
import com.belkanoid.weather.model.Weather
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException
import java.net.URL

class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private var _weatherLiveData: MutableLiveData<Weather> = MutableLiveData()
    val weatherLiveData: LiveData<Weather> get() = _weatherLiveData

    var city: MutableLiveData<String> = MutableLiveData()

    suspend fun currentWeather() {
        lateinit var response: Response<Weather>
        viewModelScope.launch(Dispatchers.IO) {
            response = repository.service.getCurrentWeather(city.value.toString())
        }.join()
        if (response.isSuccessful) _weatherLiveData.postValue(response.body())
        else throw IOException()
    }


    suspend fun getCurrentWeatherImage(code: String): Bitmap {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val fetchUrl = "http://openweathermap.org/img/wn/$code@4x.png"
            val fetchImage = URL(fetchUrl).openStream()
            BitmapFactory.decodeStream(fetchImage)
        }
    }


    fun getBackgroundColor(main: String): Int {
        return when (main) {
            "Clouds" -> R.color.clouds
            "Atmosphere" -> R.color.atmosphere
            "Snow" -> R.color.snow
            "Rain" -> R.color.rain
            "Drizzle" -> R.color.drizzle
            "Thunderstorm" -> R.color.thunderstorm
            else -> R.color.clear
        }
    }
}