package ictlife.test.skata.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import ictlife.test.skata.R

class Splash : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // some simple animation
        val bundle = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right, R.anim.slide_out_left
        ).toBundle()

        // delay for at least 1.5 seconds .. just a mere show off of a screen:)
        Thread.sleep(1500)

        // start the main activity
        startActivity(Intent(this, MainActivity::class.java), bundle)
        finish()
    }
}