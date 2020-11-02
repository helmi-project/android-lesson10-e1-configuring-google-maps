package com.androidatc.lesson10_e1_configuringgooglemaps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.myMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private lateinit var GMap: GoogleMap

    var locationManager: LocationManager?= null
    var locationListener: LocationListener?= null

    override fun onMapReady(googleMap: GoogleMap) {
        GMap = googleMap
        /*
        val cnTower = LatLng(43.6425662,-79.3870568)

        //GMap.moveCamera(CameraUpdateFactory.newLatLng(cnTower))
        GMap.moveCamera((CameraUpdateFactory.newLatLngZoom(cnTower,16f)))
        GMap.addMarker(MarkerOptions().position(cnTower).title("Toronto CN Tower"))
         */
        GMap.setOnMapClickListener(myListener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    val userLocation = LatLng(location.latitude, location.longitude)

                    GMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,16f))
                    GMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            override fun onProviderDisabled(provider: String?) {
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        } else {
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,1f,locationListener)
        }
    }

    // Click Listener
    val myListener = object : GoogleMap.OnMapClickListener {
        override fun onMapClick(location: LatLng?) {
            GMap.clear()

            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            var address = ""

            try {
                val addressList = geocoder.getFromLocation(location!!.latitude, location!!.longitude,1)

                if (addressList != null && addressList.size > 0) {
                    if (addressList[0].thoroughfare != null) {
                        address += addressList[0].thoroughfare
                        if (addressList[0].subThoroughfare != null) {
                            address += addressList[0].subThoroughfare
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (address.equals("")) {
                address = "No address is available"
            }
            GMap.addMarker(MarkerOptions().position(location!!).title(address))
        }
    }
}
