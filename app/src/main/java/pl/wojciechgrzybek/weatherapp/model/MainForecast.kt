package pl.wojciechgrzybek.weatherapp.model

data class MainForecast(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Double,
    val sea_level: Int,
    val grnd_level: Int,
    val humidity: Double,
    val temp_kf: Double
)
