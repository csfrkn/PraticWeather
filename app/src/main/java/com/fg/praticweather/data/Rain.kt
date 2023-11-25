package com.fg.praticweather.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rain(
    @SerializedName("3h")
    val h : Float
):Parcelable{
    constructor():this(0f)
}