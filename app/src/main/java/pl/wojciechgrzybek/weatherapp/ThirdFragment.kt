package pl.wojciechgrzybek.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.wojciechgrzybek.weatherapp.R
import pl.wojciechgrzybek.weatherapp.databinding.ThirdFragmentBinding


class ThirdFragment : Fragment() {

    private lateinit var binding: ThirdFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ThirdFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
    }

    private fun setupData() {
        binding.txtMain.text = getString(R.string.third_fragment_label)
        binding.imgMain.setImageResource(R.mipmap.ic_launcher)
    }
}