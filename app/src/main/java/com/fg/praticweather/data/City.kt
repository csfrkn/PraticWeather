package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val coord: Coord,
    val country: String,
    val id: Int,
    val name: String,
    val population: Int,
    val sunrise: Int,
    val sunset: Int,
    val timezone: Int
):Parcelable{
    constructor():this(Coord(),"",0,"",0,0,0,0)
}