package pl.wojciechgrzybek.weatherapp.model

data class WeatherList(
    val dt: Int,
    val main: MainForecast,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val rain: Rain,
    val snow: Snow,
    val sys: SysForecast,
    val dt_txt: String
)