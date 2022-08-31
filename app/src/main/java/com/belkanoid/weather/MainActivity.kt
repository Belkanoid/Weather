package com.belkanoid.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.belkanoid.weather.screens.WeatherFragment
import com.belkanoid.weather.viewModel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val weatherFragment = WeatherFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .add(R.id.container, weatherFragment)
            .commit()

    }
}