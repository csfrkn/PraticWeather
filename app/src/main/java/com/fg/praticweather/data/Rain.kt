package com.fg.praticweather.data

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val h : Float
)