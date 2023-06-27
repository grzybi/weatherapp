package pl.wojciechgrzybek.weatherapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CityListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        val actionBar = supportActionBar
        actionBar?.title = "Add city"
        actionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_add -> {
                Log.d("----- menu", "add")
                Toast.makeText(this@CityListActivity, "add", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.action_manage -> {
                Log.d("----- menu", "manage")
                Toast.makeText(this@CityListActivity, "manage", Toast.LENGTH_LONG).show()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_city, menu)
        return true
    }
}