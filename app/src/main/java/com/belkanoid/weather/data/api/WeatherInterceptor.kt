package com.belkanoid.weather.data.api


import com.belkanoid.weather.common.Constants.APP_KEY
import com.belkanoid.weather.common.Constants.RU
import com.belkanoid.weather.common.Constants.UNITS
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class WeatherInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest : Request = chain.request()
        val newUrl : HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("appid", APP_KEY)
            .addQueryParameter("lang", RU)
            .addQueryParameter("units", UNITS)
            .build()

        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}