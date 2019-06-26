package ictlife.test.skata.views.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ictlife.test.skata.R
import ictlife.test.skata.app.AppConstants
import ictlife.test.skata.app.AppPreference
import ictlife.test.skata.views.interfaces.BaseInstanceHelper
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

class Loader : Fragment() {

    private lateinit var instanceHelper: BaseInstanceHelper
    private lateinit var appPreference: AppPreference
    private var appIsPaused = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appPreference = instanceHelper.getPrefsInstance()

        // run task in a background thread as it might take long - depends on the image's size
        doAsync {

            // get image to scatter - takes approx. 4% of the time
            val fStream = activity!!.openFileInput("raw.png")
            val raw = BitmapFactory.decodeStream(fStream)

            fStream.close()

            // get the raw image's dimensions and pixels - takes approx. 1% of the time
            val width = raw.width
            val height = raw.height

            val pixels = IntArray(width * height)
            raw.getPixels(pixels, 0, width, 0, 0, width, height)

            // scatter image - takes approx. 47% of the time
            shufflePixels(pixels)

            // rebuild new scattered image and save the output - takes approx. 48% of the time
            val cooked = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            cooked.setPixels(pixels, 0, width, 0, 0, width, height)

            val foStream = activity!!.openFileOutput("skatad.png", Context.MODE_PRIVATE)

            cooked.compress(Bitmap.CompressFormat.PNG, 100, foStream)
            foStream.close()

            uiThread {
                // navigate to the share screen if fragment isn't paused
                if (!appIsPaused) showImageViewer()
            }
        }

        return inflater.inflate(R.layout.frag_view_loader, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            instanceHelper = activity as BaseInstanceHelper
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + "must implement BaseInstanceHelper")
        }
    }

    override fun onResume() {
        super.onResume()
        appIsPaused = false
    }

    override fun onPause() {
        super.onPause()
        appIsPaused = true
    }

    // Implementing the Fisher–Yates shuffle method
    private fun shufflePixels(pixels: IntArray) {
        for (i in pixels.size - 1 downTo 1) {
            // get current pixel
            val pixel = pixels[i]

            // get random pixel
            val index = Random().nextInt(i + 1)
            val randPixel = pixels[index]

            // swap the two pixels
            pixels[index] = pixel
            pixels[i] = randPixel
        }
    }

    private fun showImageViewer() {
        appPreference.viewType = "skatad_image" // change the view type

        val fr = activity!!.supportFragmentManager

        val viewer = ImageViewer.instance

        fr.beginTransaction()
            .setCustomAnimations(
                R.animator.fragment_slide_bottom_enter, R.animator.fragment_slide_bottom_exit,
                R.animator.fragment_slide_top_enter, R.animator.fragment_slide_top_exit
            )
            .replace(R.id.container_main, viewer, AppConstants.FRAG_IMAGE_VIEWER)
            .addToBackStack(AppConstants.FRAG_IMAGE_VIEWER)
            .commit()
    }

    companion object {

        private var ivInstance: Loader? = null

        val instance: Loader
            @Synchronized get() {
                if (ivInstance == null) ivInstance = Loader()

                return ivInstance!!
            }
    }
}