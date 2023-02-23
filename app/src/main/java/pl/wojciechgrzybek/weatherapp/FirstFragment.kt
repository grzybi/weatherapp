package pl.wojciechgrzybek.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.wojciechgrzybek.weatherapp.databinding.FirstFragmentBinding

class FirstFragment : Fragment() {

    private lateinit var binding: FirstFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FirstFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()
        val textView = getView()?.findViewById<TextView>(R.id.headerLabel)
        textView?.text = "test"
        textView?.setOnClickListener{testClick()}
    }

    private fun testClick() {
        Log.d("test", "test")
    }

    private fun setupData() {
        binding.txtMain.text = getString(R.string.first_fragment_label)
        binding.imgMain.setImageResource(R.mipmap.ic_launcher)
    }
}