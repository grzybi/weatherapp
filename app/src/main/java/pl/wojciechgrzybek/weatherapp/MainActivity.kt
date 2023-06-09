package pl.wojciechgrzybek.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import pl.wojciechgrzybek.weatherapp.model.WeatherModel
import pl.wojciechgrzybek.weatherapp.service.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), SetupFragment.SetupFragmentListener {

    private val appId: String = "72b07a9589d4af1914df47d3a2bb786b"
    private val baseUrl: String = "https://api.openweathermap.org/data/"

    private lateinit var binding: ActivityMainBinding

    private val handler = Handler(Looper.getMainLooper())
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val api: String = "72b07a9589d4af1914df47d3a2bb786b"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("hello", "world")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupViewPager()


//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
//        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.refresh -> {
//                    executorFirst("Lodz")
//                    true
//                }
//                else -> {
//                    executorFirst("Warsaw")
//                    true
//                }
//            }
//
//
//        }

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

    @SuppressLint("")
    fun executorFirst(city: String) {
        executor.execute {
            try {
//                val response = null
                val response: String? = try {
                    Log.d("api", api.toString())
                    Log.d("city", city.toString())
                    URL("https://api.openweathermap.org/data/2.5/weather?q=${city}&appid=${api}").readText(
                        Charsets.UTF_8
                    )


                } catch (e: java.lang.Exception) {
                    null
                }

                handler.post {
                    if (response == null) {
                        Log.d("response", "null")

                    } else {
                        Log.d("response", "received")
                    }
                }
            } catch (e: java.lang.Exception) {

            }


//            Log.d("test executor", "true")



        }
        //api call


    }



    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
//        val tabLayout = findViewById<TabLayout>(R.id.tabl)

        viewPager.adapter = adapter
//        val fragment = adapter.getItem(3) as? SetupFragment
//        Log.d("fragm", fragment?.view.toString())
//        val elem = fragment?.view?.findViewById<Button>(R.id.button2)
//        Log.d("elem", elem.toString())

        //        fragment?.listener = this
    }

    override fun onButtonClicked() {
        Log.d("onbuttonlistener", "click")
    }

    override fun onTextChanged(text: String) {
        TODO("Not yet implemented")
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
