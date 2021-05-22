package bme.bme.cs.shoppingHW.touch

import android.app.Application
import android.util.Log
import com.parse.Parse
import com.parse.ParseACL
import com.parse.ParseObject
import com.parse.ParseUser

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this)

        // Add your initialization code here
        Parse.initialize(
            Parse.Configuration.Builder(applicationContext)
                .applicationId("myappID")
                .clientKey("CLNIm4ahCTNX")
                .server("http://13.58.103.15/parse/")
                .build()
        )

        //ParseUser.enableAutomaticUser()
        val defaultACL = ParseACL()
        defaultACL.publicReadAccess = true
        defaultACL.publicWriteAccess = true
        ParseACL.setDefaultACL(defaultACL, true)
    }

}