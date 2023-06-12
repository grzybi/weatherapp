package pl.wojciechgrzybek.weatherapp.model

data class Main(
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val temp_min: Double,
    val temp_max: Double,
    val sea_level: Double,
    val grnd_level: Double
)
