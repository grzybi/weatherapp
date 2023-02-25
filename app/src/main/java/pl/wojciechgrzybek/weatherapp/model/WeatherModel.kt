package pl.wojciechgrzybek.weatherapp.model

data class WeatherModel (
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
//    rain, snow
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
//    val id: String,
    val name: String
// cod
):java.io.Serializable
