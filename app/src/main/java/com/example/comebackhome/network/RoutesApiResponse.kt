package com.example.proyectodivisacontentprovider.network

import com.example.comebackhome.model.Route
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


// TODO: Cambiar de API y guardarla en un lugar seguro
private const val BASE_URL =
    "https://api.openrouteservice.org/"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface RoutesApiService {
    @GET("v2/directions/driving-car")
    suspend fun getRoute(@Query("api_key") key:String,
                         @Query("start", encoded = true) start:String,
                         @Query("end", encoded = true) end: String
    ): Response<Route>
}

// Object para hacer uso de Singleton
object RoutesApi{
    val retrofitService : RoutesApiService by lazy {
        retrofit.create(RoutesApiService::class.java) }

}