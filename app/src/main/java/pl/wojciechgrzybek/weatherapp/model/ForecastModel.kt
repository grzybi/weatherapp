package pl.wojciechgrzybek.weatherapp.model

data class ForecastModel(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherList>,
    val city: City
) : java.io.Serializable
