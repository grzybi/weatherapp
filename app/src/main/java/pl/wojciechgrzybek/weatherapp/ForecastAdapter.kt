package pl.wojciechgrzybek.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pl.wojciechgrzybek.weatherapp.databinding.ForecastRowBinding
import pl.wojciechgrzybek.weatherapp.model.WeatherList
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class ForecastAdapter(private val data: List<WeatherList>) :
    RecyclerView.Adapter<ForecastAdapter.EntryHolder>() {
    inner class EntryHolder(binding: ForecastRowBinding) : ViewHolder(binding.root) {
        val tvDate = binding.tvDate
        val tvDescription = binding.tvDescription
        val ivWeatherImage = binding.ivWeatherImage
        val tvTemp = binding.tvTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowBinding = ForecastRowBinding.inflate(inflater, parent, false)
        return EntryHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: EntryHolder, position: Int) {
        val apiDate = data[position].dt_txt
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val output = dateFormat.parse(apiDate)
        val outputFormat = SimpleDateFormat("d MMM HH:mm   EEE")
        val outputDate = output?.let { outputFormat.format(it) }
        holder.tvDate.text = outputDate

        holder.tvDescription.text = data[position].weather[0].description

        val temp = data[position].main.temp.roundToInt().toString()
        holder.tvTemp.text = buildString {
            append(temp)
            append("°C")
        }

        holder.ivWeatherImage.setImageResource(getWeatherIcon(data[position].weather[0].main))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    private fun getWeatherIcon(weatherMain: String): Int {
        return when (weatherMain) {
            "Clear" -> R.drawable.ic_sun
            "Clouds" -> R.drawable.ic_clouds
            "Drizzle" -> R.drawable.ic_cloud_drizzle
            "Rain" -> R.drawable.ic_cloud_rain_heavy
            "Snow" -> R.drawable.ic_cloud_snow
            "Thunderstorm" -> R.drawable.ic_cloud_lightning
            else -> R.drawable.ic_cloud_haze2
        }
    }
}
