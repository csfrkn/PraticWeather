package com.fg.praticweather.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Clouds(
    val all: Int
): Parcelable
{
    constructor() : this(0)
}