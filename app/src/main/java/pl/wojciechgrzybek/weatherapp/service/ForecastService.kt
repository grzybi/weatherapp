package pl.wojciechgrzybek.weatherapp.service

import pl.wojciechgrzybek.weatherapp.model.ForecastModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ForecastService {
    @GET("2.5/forecast")
    fun getForecast(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("q") q: String?,
        @Query("units") units: String?,
        @Query("appid") appid: String?,
        @Query("cnt") cnt: Int
    ): Call<ForecastModel>
}
