package pl.wojciechgrzybek.weatherapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {

    private val COUNT = 4

    private val firstFragment = FirstFragment()
    private val secondFragment = SecondFragment()
    private val thirdFragment = ThirdFragment()
    private val setupFragment = SetupFragment()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> firstFragment
            1 -> secondFragment
            2 -> thirdFragment
            3 -> setupFragment
            else -> firstFragment
        }
    }

//    override fun getItem(position: Int): Fragment {
//        return when (position) {
//            0 -> FirstFragment()
//            1 -> SecondFragment()
//            2 -> ThirdFragment()
//            3 -> SetupFragment()
//            else -> FirstFragment()
//        }
//    }

    override fun getCount(): Int {
        return COUNT
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Basic"
            1 -> "Extended"
            2 -> "Forecast"
            3 -> "Setup"
            else -> ""
        }
    }
}