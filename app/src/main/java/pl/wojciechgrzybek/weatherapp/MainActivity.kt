package pl.wojciechgrzybek.weatherapp

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding
import pl.wojciechgrzybek.weatherapp.databinding.FirstFragmentBinding
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import pl.wojciechgrzybek.weatherapp.model.ForecastModel
import pl.wojciechgrzybek.weatherapp.model.WeatherModel
import pl.wojciechgrzybek.weatherapp.service.ForecastService
import pl.wojciechgrzybek.weatherapp.service.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SetupFragment.SetupFragmentListener {

    private val appId: String = "72b07a9589d4af1914df47d3a2bb786b"
    private val baseUrl: String = "https://api.openweathermap.org/data/"

    private lateinit var binding: ActivityMainBinding
    private lateinit var firstFragment: FirstFragment
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null

    private val handler = Handler(Looper.getMainLooper())
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private val api: String = "72b07a9589d4af1914df47d3a2bb786b"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("hello", "world")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//        firstFragment = supportFragmentManager.findFragmentById(R.id.FirstFragment) as FirstFragment

        setContentView(view)


    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        setupViewPager()
        setupUI()
        if (isNetworkAvailable(baseContext)) {
            initializePermissions()
        } else {
            Log.d("Network", "NOO")
            Toast.makeText(
                this@MainActivity,
                "No internet connection. Data can be invalid or outdated.",
                Toast.LENGTH_LONG
            ).show()
            // TODO get stored data, if available
        }
    }

    private fun initializePermissions() {
        Log.d("Network", "YES")
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                Log.d("PERMISSION", "Checking permission for location")
                if (isGranted) {
                    Log.d("PERMISSION", "Location permission granted")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        receiveLocation()
                    }
                } else {
//                    // TODO read from file
                    Log.d("PERMISSION", "not granted, read from file")
                }
            }

        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PERMISSION", "Location permission granted")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                receiveLocation()
            }
        } else {
            Log.d("PERMISSION", "Asking for Location permission")
            requestPermissionLauncher.launch(ACCESS_COARSE_LOCATION)
        }
        Log.d("------------------", "network avail end")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun receiveLocation() {
        locationManager =
            getSystemService(LOCATION_SERVICE) as LocationManager
        val isNetworkAvailable =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val locationListener =
            LocationListener { location -> currentLocation = location }
        if (isNetworkAvailable) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this@MainActivity,
                    "no access",
                    Toast.LENGTH_LONG
                ).show()
                return
            } else {
//                Toast.makeText(
//                    this@MainActivity,
//                    "Granted",
//                    Toast.LENGTH_SHORT
//                ).show()
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    locationListener
                )
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "No acccess to localization. Please check your system settings.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val lastKnownLocationFromNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationFromNetwork?.let { currentLocation = lastKnownLocationFromNetwork }

        Log.d("LOCATION", lastKnownLocationFromNetwork.toString())

        val geocoder = Geocoder(this, Locale.getDefault())
        val address =
            geocoder.getFromLocation(currentLocation!!.latitude, currentLocation!!.longitude, 1)
        if (address != null && address.size > 0) {
            val city = address[0].locality
            Log.d("LOCATION", city)
            //Toast.makeText(this@MainActivity, city.toString(), Toast.LENGTH_LONG).show()
            getWeather(city)
        }
    }

    @SuppressLint("")
    fun executorFirst(city: String) {
        executor.execute {
            try {
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
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Basic"
                1 -> tab.text = "Extended"
                2 -> tab.text = "Forecast"
            }
        }.attach()

        viewPager.offscreenPageLimit = 2
    }

    private fun setupUI() {
        val buttonCitySearch = binding.buttonCitySearch

        buttonCitySearch.setOnClickListener {
            val textInputCity = binding.city
            Log.d("City to search ", textInputCity.text.toString())
            getWeather(textInputCity.text.toString())
        }
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
        if (city != null) {
            getCurrentWeather(city)
            getForecastWeather(city)
        }
    }

    private fun getCurrentWeather(city: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: WeatherService =
            retrofit.create<WeatherService>(WeatherService::class.java)

        val listCall: Call<WeatherModel> =
            service.getWeather(null, null, city, "metric", appId)

        listCall.enqueue(
            object : Callback<WeatherModel> {
                override fun onResponse(
                    call: Call<WeatherModel>,
                    response: Response<WeatherModel>
                ) {
                    if (response.isSuccessful) {
                        Log.d("__RESPONSE__WEATHER___", response.body().toString())
//                        firstFragment.cityLabel.text = response.body().toString()
                        val responseBody = response.body()
                        val weatherMain = responseBody!!.weather[0].main.toString()
                        findViewById<TextView>(R.id.tvCity).text = responseBody.name

                        findViewById<ImageView>(R.id.ivWeather).setImageResource(
                            getWeatherIcon(weatherMain)
                        )
                        findViewById<TextView>(R.id.tvDescription).text =
                            responseBody.weather[0].description
                        findViewById<TextView>(R.id.tvTemp).text =
                            responseBody.main.temp.roundToInt().toString()
                        findViewById<TextView>(R.id.tvPressure).text =
                            responseBody.main.pressure.toString()
                        findViewById<TextView>(R.id.tvRefreshTime).text =
                            SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)

                        findViewById<TextView>(R.id.tvCity2).text = responseBody.name

                        val dir = ((responseBody.wind.deg + 22.5) % 360 / 45).toInt()
                        Log.d("kierunek", dir.toString())
                        val dirName: Array<String> =
                            arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")

                        findViewById<TextView>(R.id.tvWindDetails).text = buildString {
                            append(responseBody.wind.speed.roundToInt())
                            append("m/s ")
                            append(dirName[dir])
                        }
                        findViewById<TextView>(R.id.tvHumidityDetails).text = buildString {
                            append(responseBody.main.humidity)
                            append("%")
                        }
                        findViewById<TextView>(R.id.tvVisibilityDetails).text = buildString {
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

    private fun getForecastWeather(city: String) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service: ForecastService =
            retrofit.create<ForecastService>(ForecastService::class.java)

        val listCall: Call<ForecastModel> =
            service.getForecast(null, null, city, "metric", appId, cnt = 40)

        listCall.enqueue(
            object : Callback<ForecastModel> {
                override fun onResponse(
                    call: Call<ForecastModel>,
                    response: Response<ForecastModel>
                ) {
                    if (response.isSuccessful) {
                        Log.d("__RESPONSE__FORECAST__", response.body().toString())
                        val responseBody = response.body()

                        val forecastView = findViewById<RecyclerView>(R.id.recyclerView)
                        forecastView.layoutManager = LinearLayoutManager(baseContext)
                        forecastView.adapter = responseBody?.let { ForecastAdapter(it.list) }
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

    fun onFirstFragmentCreated() {
        Log.d("test", "first fragmentCreated")
    }
}

//                @SuppressLint("SetTextI18n", "CommitPrefEdits")
//                    if (response.isSuccessful) {
//                        val weatherList: WeatherModel? = response.body()
//
//                        val weatherResponseJsonString = Gson().toJson(weatherList)
////                        val editor = mSharedPreferences.edit()
////                        editor.putString(weatherData, weatherResponseJsonString)
////                        editor.apply()
////                        setupUI()
//
//                        Log.i("Response Result", "$weatherList")
//
//                        for (i in weatherList?.weather?.indices!!) {
//                            Log.d("base", weatherList?.base.toString())
//                        }
//                    } else {
//                        Toast.makeText(
//                            this@MainActivity,
//                            "There was an error with your request",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        when (response.code()) {
//                            400 -> {
//                                Log.e("Error 400", "Bad Request")
//                            }
//                            404 -> {
//                                Log.e("Error 404", "Not Found")
//                            }
//                            else -> {
//                                Log.e("Error", "Generic Error")
//                            }
//                        }
//                    }
//                }


//            })
//        }

