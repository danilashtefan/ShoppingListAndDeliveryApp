package bme.bme.cs.shoppingHW

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val animation = AnimationUtils.loadAnimation(this,R.anim.pulse)

       tvMyName.startAnimation(animation);
       // tvNeptun.startAnimation(animation)
        Handler().postDelayed({
            val intent = Intent(this, ScrollingActivity::class.java )
            startActivity(intent)
        }, 10)
    }
}