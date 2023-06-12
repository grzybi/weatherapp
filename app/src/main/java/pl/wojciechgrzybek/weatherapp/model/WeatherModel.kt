package pl.wojciechgrzybek.weatherapp.model

data class WeatherModel(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val rain: Rain,
    val snow: Snow,
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
    val id: String,
    val name: String,
    val cod: String
) : java.io.Serializable
