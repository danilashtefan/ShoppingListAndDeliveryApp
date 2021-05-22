package bme.bme.cs.shoppingHW

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.parse.*
import kotlinx.android.synthetic.main.activity_customer.*
import java.lang.Exception

class Customer : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    lateinit var locationManager : LocationManager
    lateinit var locationListener: LocationListener
    var flagRequestActive = false;

    fun Order(view: Customer) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0f, locationListener
                )
                val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val order = ParseObject("Order")
                    order.put("username", ParseUser.getCurrentUser().username)
                    val parseGeoPoint = ParseGeoPoint(lastLocation.latitude, lastLocation.longitude)
                    order.put("location", parseGeoPoint)
                    try {
                        order.put("items", ParseUser.getCurrentUser().get("good"))
                    }
                    catch(e:Exception){
                      Log.i("Items to request","Error when tried to transmit items to request")
                    }
                    order.saveInBackground { e ->
                            flagRequestActive = true
                    }
            }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        val q = ParseUser.getQuery()
        q.whereEqualTo("type", "Driver")
        q.findInBackground { objects, e ->
            if (objects.size > 0) {
                Log.i("DriversPins","There are Drivers")
                for(result in objects){
                    val allDriversLocation= result["location"] as ParseGeoPoint
                    val resultLocation = LatLng(allDriversLocation.latitude,allDriversLocation.longitude)
                    map.addMarker(MarkerOptions().position(resultLocation).title("Driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

                }
            }
            else
            {
                Log.i("DriversPins","There are NO Drivers")
            }
        }
        val query = ParseQuery<ParseObject>("Order")
        query.whereEqualTo("username", ParseUser.getCurrentUser().username)
        query.findInBackground { objects, e ->
                if (objects.size > 0) {
                    flagRequestActive = true
            }
        }
        btnOrder.setOnClickListener {

            if (flagRequestActive) {

                Toast.makeText(this, "You had already made an order, driver will contact you soon ", Toast.LENGTH_LONG).show()
            }
else{
                Order(this)
                Toast.makeText(this, "Ordered successfully", Toast.LENGTH_LONG).show()
            }
            }


        btnCancel.setOnClickListener{
            if(flagRequestActive){
                 val query = ParseQuery.getQuery<ParseObject>("Order")
                query.whereEqualTo("username", ParseUser.getCurrentUser().username)
                query.findInBackground { objects, e ->
                    if (objects.size > 0) {
                        for (result in objects) {
                            result.deleteInBackground()
                        }
                        flagRequestActive = false
                        Toast.makeText(this, "Cancelled successfully  ", Toast.LENGTH_LONG).show()
                    }
                }
            }
            else{
                Toast.makeText(this, "There is no order placed by you", Toast.LENGTH_LONG).show()
            }
        }
        }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        locationManager = this. getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = object: LocationListener {
            override fun onLocationChanged(location: Location) {
                val userLocation = LatLng(location.latitude, location.longitude)
                val testLocation =LatLng(0.0, 0.0)
                map.clear()
                map.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                val q = ParseUser.getQuery()
                q.whereEqualTo("type", "Driver")
                q.findInBackground { objects, e ->
                    if (objects.size > 0) {
                        Log.i("DriversPins","There are Drivers")
                        for(result in objects){
                            val allDriversLocation= result["location"] as ParseGeoPoint
                            val resultLocation = LatLng(allDriversLocation.latitude,allDriversLocation.longitude)
                            map.addMarker(MarkerOptions().position(resultLocation).title("Driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

                        }
                    }
                    else
                    {
                        Log.i("DriversPins","There are NO Drivers")
                    }
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                TODO("Not yet implemented")
            }

            override fun onProviderEnabled(provider: String?) {
                TODO("Not yet implemented")
            }

            override fun onProviderDisabled(provider: String?) {
                TODO("Not yet implemented")
            }
        }

        if (Build.VERSION.SDK_INT < 23)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
                val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val userLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10f))
                map.clear()
                map.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                val q = ParseUser.getQuery()
                q.whereEqualTo("type", "Driver")
                q.findInBackground { objects, e ->
                    if (objects.size > 0) {
                        Log.i("DriversPins","There are Drivers")
                        for(result in objects){
                            val allDriversLocation= result["location"] as ParseGeoPoint
                            val resultLocation = LatLng(allDriversLocation.latitude,allDriversLocation.longitude)
                            map.addMarker(MarkerOptions().position(resultLocation).title("Driver").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))

                        }
                    }
                    else
                    {
                        Log.i("DriversPins","There are NO Drivers")
                    }
                }
            }
        }

    }
}

