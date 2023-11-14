package com.fg.praticweather.presentation.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fg.praticweather.R
import com.fg.praticweather.data.WeatherList
import com.fg.praticweather.data.WeatherModel
import com.fg.praticweather.databinding.FragmentTodayBinding
import com.fg.praticweather.presentation.activities.MainActivity
import com.fg.praticweather.presentation.adapter.HourlyWeatherAdapter
import com.fg.praticweather.presentation.viewmodels.TodayViewModel
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
    var hourlyItemsLiveData = MutableLiveData<List<WeatherList>>()
    private var activity = MainActivity()
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var city4: String
    var ts by Delegates.notNull<Int>()
    val todayViewModel by viewModels<TodayViewModel>()


    var permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                return@registerForActivityResult
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showAlert()
                } else {
                    showAlert()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hourlyWeatherAdapter = HourlyWeatherAdapter()
        checkAndRequestLocationPermission()
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
        setupHourlyItemsRv()
        observeHourlyWeather()
        location()
        observeLivedata()

        binding.edSearch.setOnEditorActionListener { _, actionId, _ ->
            val city3 = binding.edSearch.text.toString().trim()
            city4 = city3
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                todayViewModel.getCityWeather(city4, requireContext())
                val view = activity.currentFocus
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


        binding.nextDays.setOnClickListener {

            val qq =
                TodayFragmentDirections.toTomorrow(city = city4)
            findNavController().navigate(qq)
        }


    }

    private fun observeLivedata() {
        todayViewModel.weather.observe(viewLifecycleOwner) {
            if (it.list.isNotEmpty()) {
                Log.e("tag", it.list[0].dt_txt)
                binding.progressBar.visibility = View.GONE
                binding.todayScroll.visibility = View.VISIBLE
                hourlyItemsLiveData.value = it.list
                setData(it!!)

            }
        }
        todayViewModel.currentWeather.observe(viewLifecycleOwner) {
            city4 = it
        }
    }


    private fun location() {
        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                todayViewModel.getCurrentWeather(
                    location.longitude.toString(),
                    location.latitude.toString(),
                    requireContext()
                )
            }
            override fun onProviderDisabled(provider: String) {
                Snackbar.make(binding.root, "error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Permission must be given") {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }.show()
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
            showAlert()
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1800000,
            2000f,
            locationListener
        )
    }

    private fun showAlert() {
        AlertDialog.Builder(requireContext()).setTitle("location")
            .setMessage("we need").setPositiveButton("okkk") { _, _ ->
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    openAppSettings()
                }
            }.setNegativeButton("Exit App") { _, _ ->
                onDestroyView()
            }.show()
    }

    private fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        ).also(::startActivity)
    }

    private fun checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            location()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
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