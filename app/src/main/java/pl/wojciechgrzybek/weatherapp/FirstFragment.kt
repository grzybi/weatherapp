package pl.wojciechgrzybek.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.wojciechgrzybek.weatherapp.databinding.FirstFragmentBinding

class FirstFragment : Fragment(R.layout.first_fragment) {

    private lateinit var binding: FirstFragmentBinding

    lateinit var cityLabel: TextView
    lateinit var ivWeather: ImageView

    private var onImageViewClickListener: OnImageViewClickListener? = null

    fun setOnImageViewClickListener(listener: OnImageViewClickListener) {
        onImageViewClickListener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.first_fragment, container, false)
        binding = FirstFragmentBinding.inflate(layoutInflater)

//        cityLabel = view.findViewById(R.id.city_label)
        return view

//        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setupData()
       // ivWeather.setImageResource(R.drawable.ic_cloud_snow)
//        val textView = getView()?.findViewById<TextView>(R.id.headerLabel)
//        textView?.text = "test"
//        textView?.setOnClickListener{testClick()}
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).onFirstFragmentCreated()
                setupData()
    }

    private fun testClick() {
        Log.d("test", "test")
    }

    private fun setupData() {
        binding.ivWeather.setOnClickListener {
            onImageViewClickListener?.onBuildingImageClick()
        }
//
//        binding.ivBuilding.setOnClickListener {
//            onImageViewClickListener?.onBuildingImageClick()
//        }
//        binding.ivWeather.setOnClickListener { Log.d("click", "click")}

//        binding.cityLabel.text = getString(R.string.first_fragment_label)
//        binding.imgMain.setImageResource(R.mipmap.ic_launcher)
    }
}