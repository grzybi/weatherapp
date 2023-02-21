package pl.wojciechgrzybek.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pl.wojciechgrzybek.weatherapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViewPager()
    }

    private fun setupViewPager() {
        val viewPager = binding.viewPager
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
    }

}