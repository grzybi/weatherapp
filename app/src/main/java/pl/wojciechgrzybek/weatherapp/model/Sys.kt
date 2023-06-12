package pl.wojciechgrzybek.weatherapp.model

data class Sys(
    val type: Int,
    val id: Int,
    val message: String,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
