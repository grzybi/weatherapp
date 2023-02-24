package pl.wojciechgrzybek.weatherapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val locations: String = "locations"

    private lateinit var mSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViewPager()


        mSharedPreferences = getSharedPreferences(locations, Context.MODE_PRIVATE)

        val stored = mSharedPreferences.getString(locations, "empty")
        Log.d("stored", stored.toString())

        var edit = mSharedPreferences.edit()
        edit.putString(locations, "Lodzz")
        edit.apply()

    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

}