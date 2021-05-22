package bme.bme.cs.shoppingHW

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.parse.*
import kotlinx.android.synthetic.main.activity_driver_order_list.*

class DriverOrderList : AppCompatActivity() {

    var itemList = ArrayList<String>()
    lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_order_list)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        driverOrderList.adapter = arrayAdapter;

        val query = ParseQuery.getQuery<ParseObject>("Order")
        query.whereEqualTo("username", intent.getStringExtra("name"))
        query.findInBackground { objects, e ->
            if (e == null) {
                if (objects.size > 0) {
                    for (result in objects) {
                        itemList.add(result.getJSONArray("items").toString())
                    }
                }
            } else {
                Log.i("Info", "Error Occured")
            }
            arrayAdapter.notifyDataSetChanged()


        }
    }
}