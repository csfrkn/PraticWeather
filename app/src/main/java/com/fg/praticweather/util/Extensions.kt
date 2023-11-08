package com.fg.praticweather.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun kelvinToCelcius(t: Double): Int {
    val x = t - 272.15
    return x.toInt()
}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")
fun String.unCapitalizeWords(): String = split(" ").map { it.lowercase() }.joinToString(" ")


@RequiresApi(Build.VERSION_CODES.O)
fun dateFormatter(date: String): String {
    val _date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val x = LocalDate.parse(date, _date)
    return "${
        x.dayOfWeek.toString().take(3).unCapitalizeWords().capitalizeWords()
    } ${x.month.toString().take(3).unCapitalizeWords().capitalizeWords()} ${x.dayOfMonth}"
}

fun dayFormatter(date: String): String {
    val _date = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val x = LocalDate.parse(date, _date)
    return "${x.dayOfWeek.toString().take(3)}"
}

fun timeFormatter(time: String): String {
    var _time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    var x = DateTimeFormatter.ofPattern("HH:mm")
    val y = LocalDateTime.parse(time, _time)
    return x.format(y)
}

fun timepmFormatter(time: String): String {
    var _time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    var x = DateTimeFormatter.ofPattern("HH:mm")
    val y = LocalDateTime.parse(time, _time)
    if (x.format(y) == "00:00"){
        return "24"
    }else return x.format(y)
}


