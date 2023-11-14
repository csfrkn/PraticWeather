package com.fg.praticweather.presentation.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fg.praticweather.data.WeatherModel
import com.fg.praticweather.presentation.fragments.TodayFragment
import com.fg.praticweather.retrofit.ApiUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TodayViewModel : ViewModel() {

    val weather = MutableLiveData<WeatherModel>()
    val currentWeather = MutableLiveData<String>()
    private val apiKey = "f9b7ef54fc706efc8c27189d32e1ab44"


    fun getCityWeather(city: String, context: Context) {
        ApiUtils.getWeatherDao()?.getForecastData(apiKey, city)?.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body().let {
                            weather.value = response.body()
                        }
                    }
                }
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(context, "ww", Toast.LENGTH_SHORT).show()
                }
            })
    }


    fun getCurrentWeather(lon: String, lat: String, context: Context) {
        ApiUtils.getWeatherDao()?.getCurrentData(lon, lat, apiKey)?.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body().let {
                            var city = response.body()!!.name
                            getCityWeather(city, context)
                           currentWeather.value = response.body()!!.name
                        }
                    }
                }
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(context, "ww", Toast.LENGTH_SHORT).show()
                }
            })
    }
}