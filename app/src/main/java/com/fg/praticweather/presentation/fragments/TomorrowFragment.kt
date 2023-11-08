package com.fg.praticweather.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.fg.praticweather.R
import com.fg.praticweather.data.WeatherList
import com.fg.praticweather.data.WeatherModel
import com.fg.praticweather.databinding.FragmentTomorrowBinding
import com.fg.praticweather.presentation.adapter.ForecastAdapter
import com.fg.praticweather.retrofit.ApiUtils
import com.fg.praticweather.util.capitalizeWords
import com.fg.praticweather.util.kelvinToCelcius
import com.fg.praticweather.util.timepmFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class TomorrowFragment : Fragment() {

    private lateinit var binding: FragmentTomorrowBinding
    private lateinit var forecastAdapter: ForecastAdapter
    private var foreItemsLiveData = MutableLiveData<List<WeatherList>>()
    val apiKey = "f9b7ef54fc706efc8c27189d32e1ab44"
    var ts by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTomorrowBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        forecastAdapter = ForecastAdapter()
        setupHourlyItemsRv()
        observeHourlyWeather()

        val bundle: TomorrowFragmentArgs by navArgs()
        var city = bundle.city
        getCityWeather(city)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getCityWeather(city: String) {
        ApiUtils.getWeatherDao()?.getForecastData(apiKey, city)?.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body().let {
                            setData(it!!)
                            foreItemsLiveData.value = arrayListOf(
                                response.body()!!.list[16],
                                response.body()!!.list[24],
                                response.body()!!.list[32],
                                response.body()!!.list[39]
                            )
                        }
                    }
                }
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "ww", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setData(body: WeatherModel) {
        binding.apply {

            tvDesc.text = body.list[8].weather[0].description.capitalizeWords()
            tvDegree.text = kelvinToCelcius(body.list[8].main.temp).toString() + "°"
            if (body.list[8].rain?.h != null) {
                tvRain.text = "${body.list[8].rain?.h.toString()}%"
            } else tvRain.text = "0%"
            tvWind.text = (body.list[8].wind.speed * 3.6).toString().take(2) + "km/s"
            tvHumidity.text = body.list[8].main.humidity.toString() + "%"
            ts = timepmFormatter(body.list[0].dt_txt).take(2).toInt()

        }
        updateUI(body.list[0].weather[0].id)
    }

    fun updateUI(id: Int) {
        Log.e("tag", "$ts")
        binding.apply {

            when {

                (id in 200..232) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.thunderstormnight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 200..232) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.thunderstormlight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 200..232) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.thunderstormnight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 300..321) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.drizzlenight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 300..321) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.drizzlelight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 300..321) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.drizzlenight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 500..531) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.rainnight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 500..531) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.rainlight)
                    imgView.setAnimation(R.raw.rain)
                }

                (id in 500..531) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.rainnight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 600..622) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.snownight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 600..622) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.snowlight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 600..622) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.snownight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 701..781) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.fognight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 701..781) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.foglight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 701..781) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.fognight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id == 800) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.clearnight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 800) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.clearlight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 800) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.clearnight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 801) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.nightcloud)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 801) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.lightscatter)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 801) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.nightcloud)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.scatternight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.weather_bg)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.scatternight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 0..7) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.cloudsnight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 7..19) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.cloudslight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 19..24) -> {
                    tomorrowLayout.setBackgroundResource(R.drawable.cloudsnight)
                    imgView.setAnimation(R.raw.scatter)

                }
                else -> {}
            }
        }
    }

    private fun setupHourlyItemsRv() {
        binding.rvFore.apply {
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            adapter = forecastAdapter
        }
    }

    private fun observeHourlyWeather() {
        obserrveHourlyWeather().observe(
            viewLifecycleOwner
        ) { weatherList ->
            forecastAdapter.setForecast(weatherList = weatherList as ArrayList<WeatherList>)
        }
    }

    private fun obserrveHourlyWeather(): LiveData<List<WeatherList>> {
        return foreItemsLiveData
    }
}