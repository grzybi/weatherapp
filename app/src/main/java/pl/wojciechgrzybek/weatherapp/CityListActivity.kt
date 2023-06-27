package pl.wojciechgrzybek.weatherapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CityListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        val rvCities = findViewById<RecyclerView>(R.id.rvCityList)
        rvCities.layoutManager = LinearLayoutManager(baseContext)
        val cities: List<String> = listOf("123", "456")
        rvCities.adapter = cities?.let { CityListAdapter(it.toList()) }
    }

    override fun onStart() {
        super.onStart()
        sharedPreferences = getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        Log.d("shared", sharedPreferences.all.toString())
        sharedPreferences.edit().putString("Klucz2", "Wartość2").apply()
        Log.d("shared", sharedPreferences.all.toString())
    }
}