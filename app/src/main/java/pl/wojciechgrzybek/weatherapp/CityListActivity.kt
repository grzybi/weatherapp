package pl.wojciechgrzybek.weatherapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.roundToInt

class CityListActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var enteredCity: String = ""
    private lateinit var wheatherApiConnector: WeatherApiConnector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_list)

        val actionBar = supportActionBar
        actionBar?.title = "Add city"
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val rvCities = findViewById<RecyclerView>(R.id.rvCityList)
        rvCities.layoutManager = LinearLayoutManager(baseContext)

        sharedPreferences = getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        Log.d("shared", sharedPreferences.all.toString())

        val cityKeys: MutableSet<String> = sharedPreferences.all.keys
        cityKeys.remove("setup.units")
        cityKeys.remove("current.city")

        val gson = Gson()
        var cityDataForAdapter = mutableListOf<List<String>>()
        for (c in cityKeys) {
            var cityData = sharedPreferences.getString(c, null).toString()
            Log.d(buildString {
                append("leci klucz ")
                append(c) }
                , cityData)
            try {
                val jsonObject = JSONObject(cityData)
                Log.d(c, jsonObject.toString())
                val weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
                Log.d(c, weather)
                val temperature = jsonObject.getJSONObject("main").getDouble("temp").roundToInt().toString()
                Log.d(c, temperature)
                val valuesList = listOf(
                    c,
                    weather,
                    temperature
                )
                cityDataForAdapter.add(valuesList)
            } catch (e: JSONException) {
                Log.d("klucz jest niepoprawny", cityData)
            }
        }


        rvCities.adapter = CityListAdapter(cityDataForAdapter.toList())
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_add -> {
                showSearchCityDialog(this)

                // TODO implement
                return true
            }
            R.id.action_manage -> {
                // TODO implement
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_city, menu)
        return true
    }

    private fun showSearchCityDialog(context: Context) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val etCity = EditText(context)

        alertDialogBuilder.setView(etCity)
        alertDialogBuilder.setTitle("Search city")
        alertDialogBuilder.setMessage("Enter city name")
        alertDialogBuilder.setPositiveButton("OK") { dialog, which ->
            enteredCity = etCity.text.toString()
//            sharedPreferences.edit().putString(enteredCity, "1234").apply()
            Log.d("shared", sharedPreferences.all.toString())
        }
        alertDialogBuilder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}