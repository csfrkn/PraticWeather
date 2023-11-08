package com.fg.praticweather.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.fg.praticweather.data.WeatherList
import com.fg.praticweather.databinding.RvTomorrowItemBinding
import com.fg.praticweather.util.capitalizeWords
import com.fg.praticweather.util.dayFormatter
import com.fg.praticweather.util.kelvinToCelcius


class ForecastAdapter() : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>() {

    private var weatherList = ArrayList<WeatherList>()

    fun setForecast(weatherList: ArrayList<WeatherList>) {

        this.weatherList = weatherList
        notifyDataSetChanged()
    }

    class ForecastViewHolder(var binding: RvTomorrowItemBinding) : ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(
            RvTomorrowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val currentItem = weatherList[position]
        holder.binding.apply {
            val imageIcon = currentItem.weather[0].icon
            val imageUrl = "https://openweathermap.org/img/wn/$imageIcon.png"
            Glide.with(holder.itemView).load(imageUrl).into(rvForeImg)

            tvRvForeMaxTemp.text =
                kelvinToCelcius(currentItem.main.temp_max +4).toString() + "°"
            tvRvForeMinTemp.text =
                kelvinToCelcius(weatherList[position].main.temp_min-4).toString() + "°"
            rvStatus.text = weatherList[position].weather[0].description.capitalizeWords()
            rvDay.text = dayFormatter(weatherList[position].dt_txt)

        }
    }

    override fun getItemCount(): Int {
        return weatherList.size
    }


}