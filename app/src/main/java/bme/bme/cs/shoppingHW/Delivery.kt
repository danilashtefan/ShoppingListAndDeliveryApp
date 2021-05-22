package bme.bme.cs.shoppingHW

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.parse.*
import kotlinx.android.synthetic.main.activity_delivery.*


class Delivery : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery)

        supportActionBar?.hide()

        btnCustomer.setOnClickListener{
          getStartedCustomer()
        }
        btnDriver.setOnClickListener{
            getStartedDriver()
        }
    }
    fun redirectActivity(){

        if(ParseUser.getCurrentUser().get("type")=="Customer") {
            var items = ParseUser.getCurrentUser().get("items")
           // intent.putExtra("items",items)
            startActivity(Intent(this,Customer::class.java))
        }
        else{
            startActivity(Intent(this,DriverRequestList::class.java))
        }
    }

    fun getStartedDriver(){
        Log.i("Info","FUNCTION CALLED DRIVER");
        ParseUser.getCurrentUser().put("type","Driver")
        ParseUser.getCurrentUser().saveInBackground()
        var type = ParseUser.getCurrentUser().get("type")
        if(type == "Driver")
        Log.i("Info","Type of the User is: "+ type);
        redirectActivity()

    }
    fun getStartedCustomer(){
        Log.i("Info","FUNCTION CALLED CUSTOMER");
        ParseUser.getCurrentUser().put("type","Customer")
        ParseUser.getCurrentUser().saveInBackground()
        var type = ParseUser.getCurrentUser().get("type")
        Log.i("Info","Type of the User is: "+ type);
        Log.i("Items","The shopping items of the customer are: "+ ParseUser.getCurrentUser().get("good"))
        redirectActivity()

    }
}