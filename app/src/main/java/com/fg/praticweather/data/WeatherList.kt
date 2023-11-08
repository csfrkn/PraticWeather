package com.fg.praticweather.data


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
)
