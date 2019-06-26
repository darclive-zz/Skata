package ictlife.test.skata.views.fragments

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ictlife.test.skata.R
import ictlife.test.skata.app.AppConstants
import ictlife.test.skata.app.AppPreference
import ictlife.test.skata.views.interfaces.BaseInstanceHelper
import kotlinx.android.synthetic.main.frag_view_main_home.view.*

class Home : Fragment() {

    private lateinit var instanceHelper: BaseInstanceHelper
    private lateinit var appPreference: AppPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.frag_view_main_home, container, false)

        val btnGallery = rootView.btn_gallery
        val btnTakePhoto = rootView.btn_take_photo

        // get preferences' instance
        appPreference = instanceHelper.getPrefsInstance()

        btnGallery.setOnClickListener {
            startActivityForResult(
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                AppConstants.ACTION_FROM_GALLERY
            )
        }
        btnTakePhoto.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                AppConstants.ACTION_FROM_CAMERA
            )
        }

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            instanceHelper = activity as BaseInstanceHelper
        } catch (e: ClassCastException) {
            throw ClassCastException(activity!!.toString() + "must implement BaseInstanceHelper")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (data != null) {
                val bitmap = when (requestCode) {
                    AppConstants.ACTION_FROM_GALLERY -> MediaStore.Images.Media.getBitmap(
                        activity!!.contentResolver,
                        data.data
                    )
                    AppConstants.ACTION_FROM_CAMERA -> data.extras!!.get("data") as Bitmap
                    else -> null
                }

                if (bitmap != null) {
                    val foStream = activity!!.openFileOutput("raw.png", Context.MODE_PRIVATE)

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, foStream)

                    foStream.close()

                    appPreference.viewType = "preview_image"

                    showImageViewer()
                }
            }
        }
    }

    private fun showImageViewer() {
        val viewer = ImageViewer.instance

        activity!!.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.fragment_slide_bottom_enter, R.animator.fragment_slide_bottom_exit,
                R.animator.fragment_slide_top_enter, R.animator.fragment_slide_top_exit
            )
            .replace(R.id.container_main, viewer, AppConstants.FRAG_IMAGE_VIEWER)
            .addToBackStack(AppConstants.FRAG_IMAGE_VIEWER)
            .commit()
    }

    companion object {

        private var ivInstance: Home? = null

        val instance: Home
            @Synchronized get() {
                if (ivInstance == null) ivInstance = Home()

                return ivInstance!!
            }
    }
}