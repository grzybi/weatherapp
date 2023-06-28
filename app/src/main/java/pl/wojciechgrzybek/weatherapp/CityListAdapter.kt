package pl.wojciechgrzybek.weatherapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import pl.wojciechgrzybek.weatherapp.databinding.CityRowBinding

class CityListAdapter(private val data: List<List<String>>) :
    RecyclerView.Adapter<CityListAdapter.EntryHolder>() {
    inner class EntryHolder(binding: CityRowBinding) : ViewHolder(binding.root) {
        val tvCity = binding.tvCity
        val tvDescription = binding.tvDescription
//        val ivWeatherImage = binding.ivWeatherImage
        val tvTemp = binding.tvTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryHolder {
        val inflater = LayoutInflater.from(parent.context)
        val rowBinding = CityRowBinding.inflate(inflater, parent, false)
        return EntryHolder(rowBinding)
    }

    override fun onBindViewHolder(holder: EntryHolder, position: Int) {
//        val city = data[position]
        holder.tvCity.text = data[position][0]
        holder.tvDescription.text = data[position][1]
        holder.tvTemp.text = data[position][2]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
