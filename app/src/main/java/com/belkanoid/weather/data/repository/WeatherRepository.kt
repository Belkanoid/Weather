package com.belkanoid.weather.data.repository

import androidx.lifecycle.LiveData
import com.belkanoid.weather.common.Constants
import com.belkanoid.weather.data.api.WeatherApi
import com.belkanoid.weather.data.api.WeatherInterceptor
import com.belkanoid.weather.model.Weather
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class WeatherRepository {

    val service : WeatherApi by lazy{
        val client : OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(WeatherInterceptor())
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)

    }

}