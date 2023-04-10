package com.example.comebackhome

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.comebackhome.model.Route
import com.example.proyectodivisacontentprovider.network.RoutesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainActivity : Activity() {
    var map: MapView? = null
    var mLocationOverlay: MyLocationNewOverlay? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_main)
        map = findViewById<View>(R.id.map) as MapView
        map!!.setTileSource(TileSourceFactory.MAPNIK)

        //TOMA TU UBICACIÓN Y LA DIBUJA COMO UNA FLECHA BLANCA EN EL MAPA
        this.mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this.map)
        this.mLocationOverlay!!.enableMyLocation()
        this.map!!.overlays.add(this.mLocationOverlay)

        //listener del botón
        val obtenerRuta = findViewById<Button>(R.id.btn_obtain_route)
        obtenerRuta.setOnClickListener{
            createRoute()
        }
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
                Log.i("llamada",inicio)
                //EL ULTIMO PARAMETRO ES "END" AHI DEBE IR LA UBICACIÓND DE NUESTRA CASA QUE DEBE SER MODIFICABLE
                //LAS COORDENADAS SE METEN AL REVES EN ESTA API PRIMERO SE INGRESA LONGITUD Y LUEGO LATITUD (COMO LA VARIABLE INICIO)
                val result = RoutesApi.retrofitService.getRoute("5b3ce3597851110001cf62482c5c2e51b8a14c3898b3250c418b2176",inicio,"-101.18790725757029,20.11712142980282")
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