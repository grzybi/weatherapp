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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import pl.wojciechgrzybek.weatherapp.model.WeatherModel
import pl.wojciechgrzybek.weatherapp.service.WeatherService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity(), SetupFragment.SetupFragmentListener {

    private val appId: String = "72b07a9589d4af1914df47d3a2bb786b"
    private val baseUrl: String = "https://api.openweathermap.org/data/"

    private lateinit var binding: ActivityMainBinding
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
                return
            }
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                locationListener
            )
        } else {
            Toast.makeText(
                this@MainActivity,
                "Error: Network provider",
                Toast.LENGTH_SHORT
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
            Toast.makeText(this@MainActivity, city.toString(), Toast.LENGTH_LONG).show()
            // TODO get weather from this point
        }
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
        if (isNetworkAvailable(this@MainActivity) && city != "") {
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service: WeatherService =
                retrofit.create<WeatherService>(WeatherService::class.java)

            val listCall: Call<WeatherModel> =
                service.getWeather(null, null, "Lodz", "metric", appId)

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
