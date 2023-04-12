package com.example.comebackhome.network

import com.example.comebackhome.model.Coordinates
import com.example.comebackhome.model.Route
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


private const val BASE_URL =
    "https://nominatim.openstreetmap.org/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface RoutesApiService {
    // "search?city=guanajuato&state=guanajuato&country=mexico&postalcode=38940&format=json"
    @GET()
    suspend fun getRoute(@Url url:String): Response<List<Coordinates>>
}

// Object para hacer uso de Singleton
object RoutesApiLyL{
    val retrofitService : RoutesApiService by lazy {
        retrofit.create(RoutesApiService::class.java) }

}