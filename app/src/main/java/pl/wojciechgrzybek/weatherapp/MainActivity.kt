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
import com.google.android.material.bottomnavigation.BottomNavigationView
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), SetupFragment.SetupFragmentListener {

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

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.refresh -> {
                    executorFirst("Lodz")
                    true
                }
                else -> {
                    executorFirst("Warsaw")
                    true
                }
            }


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
//        val viewPager = findViewById<ViewPager>(R.id.viewPager)
//        val adapter = viewPager.adapter as ViewPagerAdapter
//        val adapter = ViewPagerAdapter(supportFragmentManager)
//        viewPager.adapter = adapter
//        viewPager.adapter = adapter
        val viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        val adapter = viewPager.adapter as ViewPagerAdapter
        val fragment = adapter.getItem(3) as? SetupFragment
        Log.d("fragm", fragment?.view.toString())
        val elem = fragment?.view?.findViewById<Button>(R.id.button2)
        Log.d("elem", elem.toString())

        //        fragment?.listener = this
    }

    override fun onButtonClicked() {
        Log.d("onbuttonlistener", "click")
    }

    override fun onTextChanged(text: String) {
        TODO("Not yet implemented")
    }

}