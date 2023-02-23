package pl.wojciechgrzybek.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding
import pl.wojciechgrzybek.weatherapp.model.WeatherModel
import pl.wojciechgrzybek.weatherapp.service.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    private val appId: String = "72b07a9589d4af1914df47d3a2bb786b"
    private val baseUrl: String = "https://api.openweathermap.org/data/"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViewPager()
        var isNetworkAvailableMessage = ""
        isNetworkAvailableMessage = if (isNetworkAvailable(this@MainActivity))
            "Network available"
        else
            "Network not available"

        Toast.makeText(
            this@MainActivity,
            isNetworkAvailableMessage,
            Toast.LENGTH_SHORT
        ).show()

        getWeather("Lodz")
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnectedOrConnecting
        }
    }

    private fun getWeather(city: String?) {
        if (isNetworkAvailable(this@MainActivity) && city != "") {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService = retrofit.create<WeatherService>(WeatherService::class.java)

            val listCall: Call<WeatherModel> = service.getWeather(null,  null, "Lodz", "metric", appId)

            Log.d("response", listCall.toString())

            listCall.enqueue(object : Callback<WeatherModel> {
                @SuppressLint("SetTextI18n", "CommitPrefEdits")
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful) {
//                        hideProgressDialog()
                        val weatherList: WeatherModel? = response.body()

                        val weatherResponseJsonString = Gson().toJson(weatherList)
//                        val editor = mSharedPreferences.edit()
//                        editor.putString(weatherData, weatherResponseJsonString)
//                        editor.apply()
//                        setupUI()

                        Log.i("Response Result", "$weatherList")

                        for (i in weatherList?.weather?.indices!!) {
                            Log.d("base", weatherList?.base.toString())
                        }
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "There was an error with your request",
                            Toast.LENGTH_SHORT
                        ).show()
                        when (response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
//                    hideProgressDialog()
                    Log.e("Errorrrrr", t.message.toString())
                }
            })
        }
    }
}
