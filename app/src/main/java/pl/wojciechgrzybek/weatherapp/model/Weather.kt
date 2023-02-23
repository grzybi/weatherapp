package pl.wojciechgrzybek.weatherapp.model

data class Weather(
    var id: Int,
    val main: String,
    val description: String,
    val icon: String
    )