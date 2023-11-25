package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherList(
    val clouds: Clouds,
    val dt: Int,
    val dt_txt: String,
    val main: Main,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    val visibility: Int,
    val weather: ArrayList<Weather>,
    val wind: Wind
):Parcelable {
    constructor() : this (Clouds(),0,"", Main(),0.0, Rain(),Sys(),0,ArrayList<Weather>(), Wind())
}