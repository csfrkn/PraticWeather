package com.fg.praticweather.data

data class WeatherModel(
    val city: City,
    val cnt: Int,
    val cod: String,
    val list: List<WeatherList>,
    val message: Int,
    val name: String,
    val base: String,
    val clouds: Clouds,
    val images: List<String> = emptyList(),
    val coord: Coord,
    val dt: Int,
    val id: Int,
    val main: Main,
    val sys: Sys,
    val timezone: Int,
    val weather: List<Weather>,
    val wind: Wind
)