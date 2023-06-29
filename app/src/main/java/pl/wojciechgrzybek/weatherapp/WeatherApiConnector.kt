package pl.wojciechgrzybek.weatherapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import pl.wojciechgrzybek.weatherapp.model.ForecastModel
import pl.wojciechgrzybek.weatherapp.model.WeatherModel
import pl.wojciechgrzybek.weatherapp.service.ForecastService
import pl.wojciechgrzybek.weatherapp.service.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherApiConnector(private val activity: Activity, private val context: Context) {
    private val appId: String = "72b07a9589d4af1914df47d3a2bb786b"
    private val baseUrl: String = "https://api.openweathermap.org/data/"

    var units: String = "metric"

    private lateinit var sharedPreferences: SharedPreferences

    fun getWeather(city: String?) {
        if (city != null) {
            getCurrentWeather(city)
            getForecastWeather(city)
        }
    }

    fun getCurrentWeather(city: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService =
            retrofit.create(WeatherService::class.java)

        val listCall: Call<WeatherModel> =
            service.getWeather(null, null, city, units, appId)

        listCall.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful) {
                        Log.d("__RESPONSE__WEATHER___", response.body().toString())
                        val responseBody = response.body()

                        sharedPreferences = context.getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
                        Log.d("shared w apiconn", sharedPreferences.all.toString())

                        val weatherResponseJsonString = Gson().toJson(responseBody)
                        sharedPreferences.edit().putString(city, weatherResponseJsonString).apply()
                        Log.d("shared w apiconn 2", sharedPreferences.all.toString())


                        val weatherMain = responseBody!!.weather[0].main.toString()

                        activity.findViewById<TextView>(R.id.tvCity).text = responseBody.name

                        activity.findViewById<ImageView>(R.id.ivWeather).setImageResource(
                            getWeatherIcon(weatherMain)
                        )
                        activity.findViewById<TextView>(R.id.tvDescription).text =
                            responseBody.weather[0].description
                        activity.findViewById<TextView>(R.id.tvTemp).text =
                            responseBody.main.temp.roundToInt().toString()
                        activity.findViewById<TextView>(R.id.tvPressure).text =
                            responseBody.main.pressure.toString()
                        activity.findViewById<TextView>(R.id.tvRefreshTime).text =
                            SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)

                        activity.findViewById<TextView>(R.id.tvCity2).text = responseBody.name

                        val dir = ((responseBody.wind.deg + 22.5) % 360 / 45).toInt()
                        Log.d("kierunek", dir.toString())
                        val dirName: Array<String> =
                            arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")

                        activity.findViewById<TextView>(R.id.tvWindDetails).text = buildString {
                            append(responseBody.wind.speed.roundToInt())
                            append("m/s ")
                            append(dirName[dir])
                        }
                        activity.findViewById<TextView>(R.id.tvHumidityDetails).text = buildString {
                            append(responseBody.main.humidity)
                            append("%")
                        }
                        activity.findViewById<TextView>(R.id.tvVisibilityDetails).text =
                            buildString {
                                append(responseBody.visibility)
                                append("m")
                            }
                    } else {

                    }
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Log.e("Errorrrrr", t.message.toString())
                }
            }
        )
    }

    fun getForecastWeather(city: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ForecastService =
            retrofit.create<ForecastService>(ForecastService::class.java)

        val listCall: Call<ForecastModel> =
            service.getForecast(null, null, city, units, appId, cnt = 40)

        listCall.enqueue(
            object : Callback<ForecastModel> {
                override fun onResponse(
                    call: Call<ForecastModel>,
                    response: Response<ForecastModel>
                ) {
                    if (response.isSuccessful) {
                        Log.d("__RESPONSE__FORECAST__", response.body().toString())
                        val responseBody = response.body()

                        val forecastView = activity.findViewById<RecyclerView>(R.id.recyclerView)
                        forecastView.layoutManager = LinearLayoutManager(context)
                        forecastView.adapter = responseBody?.let { ForecastAdapter(it.list, units) }
//                        firstFragment.cityLabel.text = response.body().toString()
//                        findViewById<TextView>(R.id.lat_label).text = response.body().toString()
//                        findViewById<TextView>(R.id.city_label).text = response.body()!!.name.toString(
                    } else {


                    }
                }

                override fun onFailure(call: Call<ForecastModel>, t: Throwable) {
                    Log.e("Errorrrrr", t.message.toString())
                }
            }
        )
    }

    private fun getWeatherIcon(weatherMain: String): Int {
        return when (weatherMain) {
            "Clear" -> R.drawable.ic_sun
            "Clouds" -> R.drawable.ic_clouds
            "Drizzle" -> R.drawable.ic_cloud_drizzle
            "Rain" -> R.drawable.ic_cloud_rain_heavy
            "Snow" -> R.drawable.ic_cloud_snow
            "Thunderstorm" -> R.drawable.ic_cloud_lightning
            else -> R.drawable.ic_cloud_haze2
        }
    }


}