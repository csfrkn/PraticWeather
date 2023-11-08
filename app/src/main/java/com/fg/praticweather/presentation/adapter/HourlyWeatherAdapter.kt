package com.fg.praticweather.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.fg.praticweather.data.WeatherList
import com.fg.praticweather.databinding.RvHourlyItemBinding
import com.fg.praticweather.util.kelvinToCelcius
import com.fg.praticweather.util.timeFormatter

class HourlyWeatherAdapter() : RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder>() {

    private var weathersList = ArrayList<WeatherList>()

    fun setWeathers(weathersList: ArrayList<WeatherList>) {
        this.weathersList = weathersList
        notifyDataSetChanged()
    }

    class HourlyViewHolder(var binding: RvHourlyItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        return HourlyViewHolder(
            RvHourlyItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val currentItem = weathersList[position]
        holder.binding.apply {
            val imageIcon = currentItem.weather[0].icon
            val imageUrl = "https://openweathermap.org/img/wn/$imageIcon.png"
            Glide.with(holder.itemView).load(imageUrl).into(rvItemImg)

            rvTvTemp.text = kelvinToCelcius(currentItem.main.temp).toString() + "°"
            rvTvHour.text = timeFormatter(currentItem.dt_txt)

        }


    }

    override fun getItemCount(): Int {
        return weathersList.size
    }
}