package pl.wojciechgrzybek.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.wojciechgrzybek.weatherapp.R
import pl.wojciechgrzybek.weatherapp.databinding.FirstFragmentBinding
import pl.wojciechgrzybek.weatherapp.databinding.SetupFragmentBinding

class SetupFragment : Fragment() {

    private lateinit var binding: SetupFragmentBinding

    interface SetupFragmentListener {
        fun onButtonClicked()
        fun onTextChanged(text: String)
    }

    var listener: SetupFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("jestem tu", "tak")
        if (context is SetupFragmentListener) {
            Log.d("jestem tu", "1")
            listener = context
            Log.d("jestem tu", "2")
        } else {
            Log.d("jestem tu", "0")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SetupFragmentBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
    }

    private fun setupData() {
//        binding.txtMain.text = getString(R.string.first_fragment_label)
//        binding.imgMain.setImageResource(R.mipmap.ic_launcher)
    }
}