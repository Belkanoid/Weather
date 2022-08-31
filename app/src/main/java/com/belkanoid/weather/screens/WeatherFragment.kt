package com.belkanoid.weather.screens

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.belkanoid.weather.R
import com.belkanoid.weather.common.SharedPreferences
import com.belkanoid.weather.viewModel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt


class WeatherFragment() : Fragment() {

    private val cancellationTokenSource = CancellationTokenSource()


    private lateinit var city: EditText
    private lateinit var main: TextView
    private lateinit var icon: ImageView
    private lateinit var temp: TextView
    private lateinit var wind: TextView
    private lateinit var currentDay: TextView
    private lateinit var feelsLike: TextView
    private lateinit var pressure: TextView
    private lateinit var dragLayout: ConstraintLayout
    private lateinit var gpsIcon: ImageView

    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_weather, container, false)
        findView(view)

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val city = SharedPreferences.getCity(requireContext()) ?: "Moscow"
            weatherViewModel.city.value = city
            updateUI()
        }

        val fusedLocationManager = LocationServices.getFusedLocationProviderClient(requireContext())

        gpsIcon.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if(!isGPSEnabled(requireContext())) {
                    turnGPSOn()
                    return@setOnClickListener
                }

                fusedLocationManager.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                )
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result: Location = task.result
                            weatherViewModel.city.value = Geocoder(requireContext(), Locale.getDefault()).getFromLocation(result.latitude, result.longitude, 1)[0].locality
                            updateUI()
                        } else {
                            Toast
                                .makeText(
                                    requireContext(),
                                    "Не удалось определить местоположение",
                                    Toast.LENGTH_LONG
                                )
                                .show()

                        }

                    }


            } else
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 44
                )


        }






        city.doOnTextChanged { text, start, before, count ->
            val city = text.toString().trim()
            weatherViewModel.city.value = city
        }
        city.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if ((event!!.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    updateUI()
                    return true
                }
                return false
            }
        })

        return view
    }

    private fun updateUI() {
        try {
            runBlocking {
                weatherViewModel.currentWeather()
            }
            weatherViewModel.weatherLiveData.observe(viewLifecycleOwner) { response ->
                val weather = response.weather[0]
                runBlocking {
                    val image = weatherViewModel.getCurrentWeatherImage(weather.icon)
                    icon.setImageBitmap(image)
                }
                val backgroundColor = weatherViewModel.getBackgroundColor(weather.main)
                val gradientDrawable = (dragLayout.background.mutate() as GradientDrawable)
                    .setColor(ContextCompat.getColor(requireContext(), backgroundColor))


                main.text = response.weather[0].description
                temp.text = response.main.temp.toFloat().roundToInt().toString().plus("°")
                wind.text = getString(R.string.wind, response.wind.speed.toString())
                currentDay.text = DateFormat.format("EEEE, MMM dd", Date().time).toString()
                feelsLike.text = getString(
                    R.string.fellsLike,
                    response.main.feels_like.toFloat().roundToInt().toString()
                )
                pressure.text = getString(R.string.pressure, response.main.pressure.toString())
                city.setText(response.name)
                SharedPreferences.setCity(requireContext(), response.name)
            }
        } catch (e: IOException) {
            AlertDialog.Builder(context)
                .setTitle(getString(R.string.alertTitle))
                .setMessage(getString(R.string.alertMessage))
                .setPositiveButton("Хорошо") { _, _ -> }
                .show()
            SharedPreferences.discardCity(requireContext())
        }


    }

    fun findView(view: View) {
        city = view.findViewById(R.id.city_weather)
        main = view.findViewById(R.id.main_weather)
        icon = view.findViewById(R.id.icon_weather)
        gpsIcon = view.findViewById(R.id.gpsIcon_weather)
        temp = view.findViewById(R.id.average_weather)
        currentDay = view.findViewById(R.id.currentDay_weather)
        wind = view.findViewById(R.id.wind_weather)
        feelsLike = view.findViewById(R.id.feelsLike_weather)
        pressure = view.findViewById(R.id.pressure_weather)
        dragLayout = view.findViewById(R.id.drag_layout)

    }


    private fun turnGPSOn() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource.cancel()
    }

    companion object {
        fun newInstance() = WeatherFragment()
    }

    fun isGPSEnabled(mContext: Context): Boolean {
        val locationManager: LocationManager =
            mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}