package com.example.comebackhome

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.comebackhome.network.RoutesApiLyL
import com.example.proyectodivisacontentprovider.network.RoutesApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangeAddressActivity : AppCompatActivity() {
    lateinit var sharedPreference:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_address)
        val saveAdress = findViewById<Button>(R.id.btnSave)
        saveAdress.setOnClickListener{
            save()
        }
        sharedPreference =  getSharedPreferences("Preferences", Context.MODE_PRIVATE)
    }

    private fun save() {
        val street = findViewById<EditText>(R.id.txtStreet).text.toString()
        val city = findViewById<EditText>(R.id.txtCity).text.toString()
        val state = findViewById<EditText>(R.id.txtState).text.toString()
        val country = findViewById<EditText>(R.id.txtPais).text.toString()
        val cp = findViewById<EditText>(R.id.txtCP).text.toString()
        var query=""
        if(street!=""){
            query="street="+street
        }
        if(city!=""){
            if(query.length>0)query+="&"
            query+="city="+city
        }
        if(state!=""){
            if(query.length>0)query+="&"
            query+="state="+state
        }
        if(country!=""){
            if(query.length>0)query+="&"
            query+="country="+country
        }
        if(cp!=""){
            if(query.length>0)query+="&"
            query+="cp="+cp
        }
        CoroutineScope(Dispatchers.IO).launch{
            val result = RoutesApiLyL.retrofitService.getRoute("search?$query&format=json")
            if(result.isSuccessful){
                Log.i("llamada",result.body().toString())

                var editor = sharedPreference.edit()
                if(!result.body().isNullOrEmpty() && result.body()!!.size>0){
                    editor.putString("lat",result.body()!![0].lat)
                    editor.putString("lon",result.body()!![0].lon)
                    editor.commit()
                }
            }else{
                Log.i("llamada","No")
            }
        }
    }
}