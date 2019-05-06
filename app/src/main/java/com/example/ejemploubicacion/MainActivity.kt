package com.example.ejemploubicacion

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.tasks.OnSuccessListener

class MainActivity : AppCompatActivity() {

    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    private val CODIGO_SOLICITUD_PERMISO = 100

    var fusedLocationClient: FusedLocationProviderClient? = null

    var locationRequest:LocationRequest? = null

    var callback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = FusedLocationProviderClient(this)
        inicializarLocationRequest()

    }

    private fun inicializarLocationRequest(){
        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validarPermisosUbicacion():Boolean{
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(this,permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(this,permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbicacionOrdinaria

    }

    @SuppressLint("MissingPermision")
    private fun obtenerUbicacion(){
        /*val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(this,permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria = ActivityCompat.checkSelfPermission(this,permisoCoarseLocation)

        fusedLocationClient?.lastLocation?.addOnSuccessListener(this, object : OnSuccessListener<Location>{
            override fun onSuccess(location: Location?) {
                if(location != null){
                    Toast.makeText(applicationContext,location?.latitude.toString()+"-"+location?.longitude.toString(), Toast.LENGTH_LONG).show()
                }
            }

        })*/
        callback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                for(ubicacion in locationResult?.locations!!){
                    Toast.makeText(applicationContext,ubicacion?.latitude.toString()+"-"+ubicacion?.longitude.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(locationRequest, callback, null)
    }

    private fun pedirPermisos(){
        val deboProveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(this,permisoFineLocation)

        if(deboProveerContexto){
            //explicar
            solicitudPermiso()
        }else{
            solicitudPermiso()
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun solicitudPermiso(){
        requestPermissions(arrayOf(permisoFineLocation, permisoCoarseLocation),CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CODIGO_SOLICITUD_PERMISO ->{
                if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //obtener ubicacion
                }else{
                    Toast.makeText(this,"No diste permiso para acceder a la ubicacion", Toast.LENGTH_LONG)
                }
            }

        }
    }

    override fun onStart(){
        super.onStart()

        if (validarPermisosUbicacion()){
            obtenerUbicacion()
        }else{
            pedirPermisos()
        }

    }

    private fun detenerActualizacionUbicacion(){

        fusedLocationClient?.removeLocationUpdates(callback)
    }

    override fun onPause() {
        super.onPause()

        detenerActualizacionUbicacion()
    }

}
