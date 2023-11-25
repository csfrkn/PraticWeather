package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Wind(
    val deg: Int,
    val gust: Double,
    val speed: Double
) : Parcelable{
    constructor():this(0,0.0,0.0)
}