package com.belkanoid.weather.data.api


import com.belkanoid.weather.model.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getCurrentWeather(@Query("q") city : String) : Response<Weather>


}