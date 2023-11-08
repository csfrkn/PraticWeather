package com.fg.praticweather.presentation.fragments

import android.Manifest
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.fg.praticweather.R
import com.fg.praticweather.data.WeatherList
import com.fg.praticweather.data.WeatherModel
import com.fg.praticweather.databinding.FragmentTodayBinding
import com.fg.praticweather.presentation.activities.MainActivity
import com.fg.praticweather.presentation.adapter.HourlyWeatherAdapter
import com.fg.praticweather.retrofit.ApiUtils
import com.fg.praticweather.util.capitalizeWords
import com.fg.praticweather.util.dateFormatter
import com.fg.praticweather.util.kelvinToCelcius
import com.fg.praticweather.util.timepmFormatter
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class TodayFragment : Fragment() {

    private lateinit var binding: FragmentTodayBinding
    private lateinit var hourlyWeatherAdapter: HourlyWeatherAdapter
    private val apiKey = "f9b7ef54fc706efc8c27189d32e1ab44"
    var hourlyItemsLiveData = MutableLiveData<List<WeatherList>>()
    private var activity = MainActivity()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var city4: String
    var ts by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hourlyWeatherAdapter = HourlyWeatherAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodayBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        location()
        setupHourlyItemsRv()
        observeHourlyWeather()
        registerLaunch()

        binding.nextDays.setOnClickListener {

            val qq = TodayFragmentDirections.toTomorrow(city = city4)
            Navigation.findNavController(it).navigate(qq)

        }


        binding.edSearch.setOnEditorActionListener { _, actionId, _ ->
            var city3 = binding.edSearch.text.toString().trim()
            city4 = city3
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getCityWeather(city3)
                val view = activity?.currentFocus
                if (view != null) {
                    val im: InputMethodManager =
                        requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager

                    im.hideSoftInputFromWindow(view.windowToken, 0)
                    binding.edSearch.clearFocus()
                }
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }


    }


    private fun location() {

        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                getCurrentWeather(location.longitude.toString(), location.latitude.toString())
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Snackbar.make(
                    binding.root,
                    "Permission needed for location",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1800000,
            2000f,
            locationListener
        )

    }

    private fun registerLaunch() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    if (ActivityCompat.checkSelfPermission(
                            activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            1800000,
                            2000f,
                            locationListener
                        )
                    }
                } else {
                    Toast.makeText(activity, "Permission Needs", Toast.LENGTH_LONG).show()
                }
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
                            hourlyItemsLiveData.value = response.body()!!.list
                        }
                    }
                }
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "ww", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun getCurrentWeather(lon: String, lat: String) {
        ApiUtils.getWeatherDao()?.getCurrentData(lon, lat, apiKey)?.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body().let {
                            var city = response.body()!!.name
                            getCityWeather(city)
                            city4 = city
                        }
                    }
                }
                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(requireContext(), "ww", Toast.LENGTH_SHORT).show()
                }
            })
    }


    fun setData(body: WeatherModel) {
        binding.apply {

            tvDegree.text = kelvinToCelcius(body.list[0].main.temp).toString() + "°"

            tvCloudly.text = body.list[0].weather[0].description.capitalizeWords()

            tvCityName.text = body.city.name

            tvDegree2.text =
                "H:${kelvinToCelcius(body.list[0].main.temp_max)} L:${kelvinToCelcius(body.list[0].main.temp_min)}"

            tvDate.text = dateFormatter(body.list[0].dt_txt)

            if (body.list[0].rain?.h != null) {
                tvRain.text = "${body.list[0].rain?.h.toString()}%"
            } else tvRain.text = "0%"

            tvWind.text = (body.list[0].wind.speed * 3.6).toString().take(2) + "km/s"

            tvHumidity.text = body.list[0].main.humidity.toString() + "%"

            ts = timepmFormatter(body.list[0].dt_txt).take(2).toInt()
        }
        updateUI(body.list[0].weather[0].id)
    }

    fun updateUI(id: Int) {
        Log.e("tag", "$ts")
        binding.apply {

            when {

                (id in 200..232) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.thunderstormnight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 200..232) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.thunderstormlight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 200..232) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.thunderstormnight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 300..321) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.drizzlenight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 300..321) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.drizzlelight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 300..321) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.drizzlenight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 500..531) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.rainnight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 500..531) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.rainlight)
                    imgView.setAnimation(R.raw.rain)
                }

                (id in 500..531) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.rainnight)
                    imgView.setAnimation(R.raw.rain)

                }

                (id in 600..622) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.snownight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 600..622) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.snowlight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 600..622) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.snownight)
                    imgView.setAnimation(R.raw.snow)

                }

                (id in 701..781) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.fognight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 701..781) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.foglight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id in 701..781) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.fognight)
                    imgView.setAnimation(R.raw.thunderstorm)

                }

                (id == 800) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.clearnight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 800) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.clearlight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 800) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.clearnight)
                    imgView.setAnimation(R.raw.sun)

                }

                (id == 801) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.nightcloud)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 801) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.lightscatter)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 801) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.nightcloud)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.scatternight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.weather_bg)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id == 802) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.scatternight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 0..7) -> {
                    todayLayout.setBackgroundResource(R.drawable.cloudsnight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 7..19) -> {
                    todayLayout.setBackgroundResource(R.drawable.cloudslight)
                    imgView.setAnimation(R.raw.scatter)

                }

                (id in 803..804) && (ts in 19..24) -> {
                    todayLayout.setBackgroundResource(R.drawable.cloudsnight)
                    imgView.setAnimation(R.raw.scatter)

                }
                else -> {}
            }
        }
    }

    private fun setupHourlyItemsRv() {
        binding.rv.apply {
            layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyWeatherAdapter
        }
    }

    private fun observeHourlyWeather() {
        obserrveHourlyWeather().observe(
            viewLifecycleOwner
        ) { weatherList ->
            hourlyWeatherAdapter.setWeathers(weathersList = weatherList as ArrayList<WeatherList>)
        }
    }

    private fun obserrveHourlyWeather(): LiveData<List<WeatherList>> {
        return hourlyItemsLiveData
    }


}