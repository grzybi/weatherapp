package pl.wojciechgrzybek.weatherapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm, lifecycle) {

    private val COUNT = 3

    private val firstFragment = FirstFragment()
    private val secondFragment = SecondFragment()
    private val thirdFragment = ThirdFragment()

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> firstFragment
            1 -> secondFragment
            2 -> thirdFragment
            else -> firstFragment
        }
    }

    override fun getItemCount(): Int {
        return COUNT
    }
}
