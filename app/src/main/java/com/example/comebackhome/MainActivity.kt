package com.example.comebackhome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.comebackhome.model.Route
import com.example.proyectodivisacontentprovider.network.RoutesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : Activity() {
    var map: MapView? = null
    lateinit var sharedPreference: SharedPreferences
    var mLocationOverlay: MyLocationNewOverlay? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        sharedPreference =  getSharedPreferences("Preferences", Context.MODE_PRIVATE)

        setContentView(R.layout.activity_main)
        var lat = sharedPreference.getString("lat", "")
        var lon = sharedPreference.getString("lon", "")
        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)
        map!!.minZoomLevel=5.0
        if(lat!="") {
            var geoPoint = listOf(GeoPoint(lat!!.toDouble(), lon!!.toDouble()), GeoPoint(32.223419, -117.061903), GeoPoint(15.888880, -87.266982))
            map!!.zoomToBoundingBox(BoundingBox.fromGeoPoints(geoPoint), true)
        }

        var place = sharedPreference.getString("place", "")
        var address = findViewById<TextView>(R.id.txtAddress)
        if(place!=""){
            address.text = "Dirección:\n$place"
        }else address.text = "Especifique la dirección a la que quiere ir"


        showPin()

        //listener del botón
        val obtenerRuta = findViewById<Button>(R.id.btn_obtain_route)
        obtenerRuta.setOnClickListener{
            createRoute()
        }
        val editarDireccion = findViewById<Button>(R.id.btn_edit_direction)
        editarDireccion.setOnClickListener{
            editDirection()
        }
    }

    private fun showPin() {
        //TOMA TU UBICACIÓN Y LA DIBUJA COMO UNA FLECHA BLANCA EN EL MAPA
        this.mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(applicationContext), this.map)
        this.mLocationOverlay!!.enableMyLocation()
        this.map!!.overlays.add(this.mLocationOverlay)
    }

    private fun editDirection() {
        val intent = Intent(this, ChangeAddressActivity::class.java)
        startActivity(intent)
        map!!.overlays.clear()
        showPin()
    }

    public override fun onResume() {
        super.onResume()
        map!!.onResume()
    }

    public override fun onPause() {
        super.onPause()
        map!!.onPause()
    }
    //DIBUJA LA RUTA
    fun createRoute(){
        val location = this.mLocationOverlay!!.myLocation
        if(location!=null){
            var inicio = location.longitude.toString()+","+location.latitude.toString()
            CoroutineScope(Dispatchers.IO).launch{
                Log.i("llamadaInicio",inicio)
                var lat = sharedPreference.getString("lat", location.latitude.toString())
                var lon = sharedPreference.getString("lon", location.longitude.toString())
                Log.i("llamadaFin", lat+", "+lon)
                //LAS COORDENADAS SE METEN AL REVES EN ESTA API PRIMERO SE INGRESA LONGITUD Y LUEGO LATITUD (COMO LA VARIABLE INICIO)
                val result = RoutesApi.retrofitService.getRoute("5b3ce3597851110001cf62482c5c2e51b8a14c3898b3250c418b2176",inicio,"$lon,$lat")
                if(result.isSuccessful){
                    dibujarRuta(result.body())
                    Log.i("llamada","Si")
                }else{
                    Log.i("llamada","No")
                }
            }
        }
    }
    fun dibujarRuta(route : Route?){
        val line = Polyline(this.map)
        route?.features?.first()?.geometry?.coordinates?.forEach{
            line.addPoint(GeoPoint(it[1],it[0]))
        }
        runOnUiThread{
            map!!.getOverlays().add(line)
        }
    }

}