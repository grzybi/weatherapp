package pl.wojciechgrzybek.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CityListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        val rvCities = findViewById<RecyclerView>(R.id.rvCityList)
        rvCities.layoutManager = LinearLayoutManager(baseContext)
        val cities: List<String> = listOf("123", "456")
        rvCities.adapter = cities?.let { CityListAdapter(it.toList()) }
    }
}