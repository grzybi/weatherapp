package pl.wojciechgrzybek.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pl.wojciechgrzybek.weatherapp.databinding.CityRowBinding
import pl.wojciechgrzybek.weatherapp.databinding.ForecastRowBinding
import pl.wojciechgrzybek.weatherapp.model.WeatherList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CityListAdapter(private val data: List<String>) :
    RecyclerView.Adapter<CityListAdapter.EntryHolder>() {
    inner class EntryHolder(binding: CityRowBinding) : ViewHolder(binding.root) {
        val tvDate = binding.tvCity
//        val tvDescription = binding.tvDescription
//        val ivWeatherImage = binding.ivWeatherImage
//        val tvTemp = binding.tvTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowBinding = CityRowBinding.inflate(inflater, parent, false)
        return EntryHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: EntryHolder, position: Int) {
//        val city = data[position]
        holder.tvDate.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
