package pl.wojciechgrzybek.weatherapp

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), OnImageViewClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firstFragment: FirstFragment
    private lateinit var viewPager: ViewPager2
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var locationManager: LocationManager
    private lateinit var sharedPreferences: SharedPreferences
    private var currentLocation: Location? = null
    private lateinit var weatherApiConnector: WeatherApiConnector

    private lateinit var ivBuilding: TextView

    private lateinit var units: String

    override fun onBuildingImageClick() {
        Log.d("t", "t")
        Toast.makeText(this@MainActivity, "Building clicked", Toast.LENGTH_LONG).show()
    }

    override fun onSetupImageClick() {
        Log.d("t2", "t2")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("hello", "world")
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("WeatherApp", Context.MODE_PRIVATE)
        units = sharedPreferences.getString("setup.units", null).toString()
        if (units == "null") {
            units = "metric"
            sharedPreferences.edit().putString("setup.units", units).apply()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
//        firstFragment = supportFragmentManager.findFragmentById(R.id.FirstFragment) as FirstFragment

        setContentView(view)
        val fragmentWithBuilding =
            supportFragmentManager.findFragmentById(R.id.FirstFragment) as? FirstFragment
        if (fragmentWithBuilding == null)
            Log.d("fragment", "null")
        else
            Log.d("fragment", "ok")
        fragmentWithBuilding?.setOnImageViewClickListener(this)
        
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()
        setupViewPager()
        setupUI()

        weatherApiConnector = WeatherApiConnector(this@MainActivity, this@MainActivity)


        if (isNetworkAvailable(baseContext)) {
            initializePermissions()
        } else {
            Log.d("Network", "NOO")
            Toast.makeText(
                this@MainActivity,
                "No internet connection. Data can be invalid or outdated.",
                Toast.LENGTH_LONG
            ).show()
            try {
                val shared = sharedPreferences.getString("weather", null)
                if (shared != null) {
                    val json = JSONObject(shared)
                    Log.d("JSON", json.toString())
                    val storedCity = json.getString("city")
                    findViewById<TextView>(R.id.tvCity).text = storedCity
                    findViewById<TextView>(R.id.tvCity2).text = storedCity

                }
            } catch (exp: Exception) {
                Log.d("Error", "WeatherApp: ", exp)
            }
        }
    }

    private fun testClick() {
        Log.d("click test", "click test")
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


            weatherApiConnector.getWeather(city)
        }
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager = findViewById<ViewPager2>(R.id.viewPager)
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
        viewPager.registerOnPageChangeCallback(onPageChangeListener)
    }

    val onPageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Log.d("Przełączona strona", position.toString())
            when (position) {
                0 -> updateCityBanner(R.color.white, R.color.black, true)
                1 -> updateCityBanner(R.color.black, R.color.white, true)
                2 -> updateCityBanner(R.color.teal_200, R.color.black, true)


            }
        }
    }

    private fun updateCityBanner(backgroundColor: Int, textColor: Int, isVisible: Boolean) {
//        binding.root.setBackgroundColor(backgroundColor)
//        binding.tvCity.setTextColor(textColor)
//        binding.ivBuilding.imageTintList = ColorStateList.valueOf(Color.rgb(255,0,0))
//        //binding.tvCity.visibility = if (isVisible) View.VISIBLE else View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.unregisterOnPageChangeCallback(onPageChangeListener)
    }

    private fun setupUI() {
//        val buttonCitySearch = binding.buttonCitySearch
//
//        buttonCitySearch.setOnClickListener {
//            val textInputCity = binding.city
//            Log.d("City to search ", textInputCity.text.toString())
//            getWeather(textInputCity.text.toString())
//        }

//        val city: TextView = findViewById<TextView>(R.id.city)
//        val one: TextView = findViewById<TextView>(R.id.tvCity)
//        val building: ImageView = findViewById<ImageView>(R.id.ivBuilding)
//        building?.setOnClickListener{testClick()}

        val ivBuilding = binding.ivBuilding
        ivBuilding.setOnClickListener {
            Log.d("test", "test")
            val intent = Intent(this, CityListActivity::class.java)
            startActivity(intent)
        }

        val ivSettings = binding.ivSettings
        ivSettings.setOnClickListener {
            Log.d("test", sharedPreferences.all.toString())
        }
    }

//    override fun onButtonClicked() {
//        Log.d("onbuttonlistener", "click")
//    }
//
//
//
//    override fun onTextChanged(text: String) {
//        TODO("Not yet implemented")
//    }

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

