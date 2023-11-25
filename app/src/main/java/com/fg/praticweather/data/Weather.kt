package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
):Parcelable{
    constructor():this("","",0,"")
}