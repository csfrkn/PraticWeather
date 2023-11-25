package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Sys(
    val pod: String
):Parcelable{
    constructor():this("")
}