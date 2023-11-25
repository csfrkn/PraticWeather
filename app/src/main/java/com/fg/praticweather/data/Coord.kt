package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Coord(
    val lat: Double,
    val lon: Double
):Parcelable{
    constructor():this(0.0,0.0)
}