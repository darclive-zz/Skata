package ictlife.test.skata.views.activities

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import ictlife.test.skata.R
import ictlife.test.skata.app.AppConstants
import ictlife.test.skata.app.AppPreference
import ictlife.test.skata.views.fragments.Home
import ictlife.test.skata.views.fragments.ImageViewer
import ictlife.test.skata.views.fragments.Loader
import ictlife.test.skata.views.interfaces.BaseInstanceHelper

class MainActivity : BaseActivity(), BaseInstanceHelper {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null ||
            supportFragmentManager.backStackEntryCount == 0
        ) showHome()
    }

    override fun getPrefsInstance(): AppPreference {
        return appPreference
    }

    override fun onBackPressed() {
        when {
            getVisibleFragment() is Loader -> {
                Toast.makeText(this@MainActivity, "Please be patient", Toast.LENGTH_LONG).show()
            }
            (getVisibleFragment() is ImageViewer) && appPreference.viewType == "skatad_image" -> {
                // remove the loader from backStack so that the
                // user is returned from the Skata screen to the Preview screen
                supportFragmentManager.popBackStack()
                supportFragmentManager.popBackStack()

                // reset view
                appPreference.viewType = "preview_image"
            }
            else -> {
                when {
                    supportFragmentManager.backStackEntryCount > 1 -> {
                        super.onBackPressed()
                    }
                    else -> finish()
                }
            }
        }
    }

    private fun getVisibleFragment(): Fragment? {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment != null && fragment.isVisible)
                return fragment
        }
        return null
    }

    private fun showHome() {
        val home = Home.instance

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.fragment_slide_bottom_enter, R.animator.fragment_slide_bottom_exit,
                R.animator.fragment_slide_top_enter, R.animator.fragment_slide_top_exit
            )
            .replace(R.id.container_main, home, AppConstants.FRAG_HOME)
            .addToBackStack(AppConstants.FRAG_HOME)
            .commit()
    }
}
