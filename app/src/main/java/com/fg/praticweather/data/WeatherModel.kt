package com.fg.praticweather.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherModel(
    val city: City = City(),
    val cnt: Int = 0,
    val cod: String = "",
    val list: List<WeatherList> = emptyList(),
    val message: Int = 0,
    val name: String = "",
    val base: String = "",
    val clouds: Clouds = Clouds(),
    val images: List<String> = emptyList(),
    val coord: Coord = Coord(),
    val dt: Int = 0,
    val id: Int= 0,
    val main: Main= Main(),
    val sys: Sys = Sys(),
    val timezone: Int = 0,
    val weather: List<Weather> = emptyList(),
    val wind: Wind = Wind()
) : Parcelable {
}