package ictlife.test.skata.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ictlife.test.skata.app.AppPreference


abstract class BaseActivity : AppCompatActivity() {

    lateinit var appPreference: AppPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize preferences
        AppPreference.initializeInstance(this)
        appPreference = AppPreference.instance
    }
}