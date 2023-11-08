package com.fg.praticweather.retrofit

import com.fg.praticweather.data.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherDao {

    @GET("forecast")
    fun getForecastData(
        @Query("appid") appId: String,
        @Query("q") q: String
    ): Call<WeatherModel>

    @GET("weather")
    fun getCurrentData(
        @Query("lon") lon: String,
        @Query("lat") lat: String,
        @Query("appid") appId: String
    ): Call<WeatherModel>

}