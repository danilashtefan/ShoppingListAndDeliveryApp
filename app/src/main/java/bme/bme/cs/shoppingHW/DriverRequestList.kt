package bme.bme.cs.shoppingHW

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.parse.*
import kotlinx.android.synthetic.main.activity_driver_request_list.*
import org.json.JSONArray
import kotlin.collections.ArrayList


class DriverRequestList : AppCompatActivity() {
    lateinit var locationManager : LocationManager
    lateinit var locationListener: LocationListener
    lateinit var arrayAdapter: ArrayAdapter<String>
    var ordersLatitudes = ArrayList<Float>() //To be able to send location in Intent to DriverLocationActivity
    var ordersLongitudes = ArrayList<Float>()
    var orders = ArrayList<String>()
    var usernames = ArrayList<String>()
    var orderList = ArrayList<JSONArray>()
    var thingsToBuy = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_request_list)
        setTitle("Requests")
        btnShops.setOnClickListener{
            shopsNearby()
        }

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, orders)
        requestListView.adapter = arrayAdapter;
        orders.clear()
        requestListView.onItemClickListener =
                OnItemClickListener { adapterView, view, i, l ->
                    var query = ParseQuery.getQuery<ParseObject>("Order")
                    query.whereEqualTo("username", usernames.get(i))
                    val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    query.findInBackground(
                            object : FindCallback<ParseObject> {
                                override fun done(objects: List<ParseObject>, e: ParseException?) {
                                    if (e == null) {
                                        if (objects.size > 0) {
                                            for (result in objects) {
                                                result.put("Active", "NO")
                                                result.saveInBackground { e ->
                                                    //TODO https://stackoverflow.com/questions/2662531/launching-google-maps-directions-via-an-intent-on-android
                                                    val directionsIntent = Intent(Intent.ACTION_VIEW,
                                                            Uri.parse("http://maps.google.com/maps?saddr=" + lastLocation.latitude + "," + lastLocation.longitude + "&daddr=" + ordersLatitudes.get(i) + "," + ordersLongitudes.get(i)))
                                                        startActivity(directionsIntent)
                                                }
                                            }
                                        }
                                    }

                                    else{
                                        Log.i("Info","Error Occured")
                                    }
                                }
                            })


                }

        requestListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { adapterView, view, i, l ->
            val intent = Intent(applicationContext, DriverOrderList::class.java)
            intent.putExtra("name",usernames.get(i))
            startActivity(intent)
            true
        }

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                ParseUser.getCurrentUser().put("location",ParseGeoPoint(location.latitude, location.longitude))
                ParseUser.getCurrentUser().saveInBackground()

                loadList(location);
            }

            //JUST OBLIGATORY METHODS
            override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
                TODO("Not yet implemented")
            }
            override fun onProviderEnabled(p0: String?) {
                TODO("Not yet implemented")
            }
            override fun onProviderDisabled(p0: String?) {
                TODO("Not yet implemented")
            }
        }


        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
                return
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        } else { if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            val parseGeoPoint = ParseGeoPoint(lastKnownLocation.latitude, lastKnownLocation.longitude)
            ParseUser.getCurrentUser().put("location",parseGeoPoint)
            ParseUser.getCurrentUser().saveInBackground()
            lastKnownLocation?.let { loadList(it) }

        }
        }

    }

    fun roundDistance(distance: Double){

        val dist = Math.round(distance*10).toDouble() / 10
    }

    fun shopsNearby(){
        val shopsIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("driverLatitude", 0.0) + "," + intent.getDoubleExtra("driverLongitude", 0.0) + "&daddr=" + "Grocery"))
        startActivity(shopsIntent)
    }


//TODO https://www.back4app.com/docs/android/parse-geopoint
    fun loadList(location:Location){
    val parseGeoPoint = ParseGeoPoint(location.latitude, location.longitude)
    ParseUser.getCurrentUser().put("location",parseGeoPoint)
    ParseUser.getCurrentUser().saveInBackground()
        val query = ParseQuery.getQuery<ParseObject>("Order")
        val geoPointLocation = ParseGeoPoint(location.latitude, location.longitude)
        query.whereDoesNotExist("Active")
val geocoder = Geocoder(this)
    var address=""
        query.findInBackground { objects, e ->
            if (e == null) {
                orders.clear()
                ordersLongitudes.clear()
                ordersLatitudes.clear()
                if (objects.size > 0) {
                    for (result in objects) {

                        val orderLocation = result["location"] as ParseGeoPoint
                        val geocoderList = geocoder.getFromLocation(orderLocation.latitude,orderLocation.longitude,1)
                        geocoderList[0].getAddressLine(0)
                        var distance = geoPointLocation.distanceInKilometersTo(orderLocation)
                        distance = Math.round(distance * 10).toDouble() / 10
                            orders.add(geocoderList[0].getAddressLine(0)+"    ($distance km)")
                            ordersLatitudes.add(orderLocation.latitude.toFloat())
                            ordersLongitudes.add(orderLocation.longitude.toFloat())
                          //  Log.i("Info","The locations are" +  requestLocation.latitude.toFloat() + requestLocation.longitude.toFloat())
                            Log.i("itemList in DriverRequestList","The list of items is: "+result.getString("good")+" items")
                            usernames.add(result.getString("username"))
                            orderList.add(result.getJSONArray("good"))
                            Log.i("Items in Driver Request","Items are this "+ orderList.toString())

                    }
                }
                else {
                    orders.add("No active requests nearby")
            }
                arrayAdapter.notifyDataSetChanged()
            }
        }
    }


}


