package ictlife.test.skata.views.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import ictlife.test.skata.R
import ictlife.test.skata.app.AppConstants
import ictlife.test.skata.app.AppPreference
import ictlife.test.skata.views.interfaces.BaseInstanceHelper
import kotlinx.android.synthetic.main.frag_view_main_image_viewer.view.*
import java.io.ByteArrayOutputStream


class ImageViewer : Fragment() {

    private lateinit var instanceHelper: BaseInstanceHelper
    private lateinit var appPreference: AppPreference

    private lateinit var imageView: AppCompatImageView
    private lateinit var button: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.frag_view_main_image_viewer, container, false)

        imageView = rootView.iv_image
        button = rootView.btn_action

        // get preferences' instance
        appPreference = instanceHelper.getPrefsInstance()

        setUpView()

        button.setOnClickListener {
            when (appPreference.viewType) {
                "preview_image" -> {
                    showLoader()
                }
                "skatad_image" -> {
                    if (ContextCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) run {
                        ActivityCompat.requestPermissions(
                            activity!!,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 99
                        )
                    } else {
                        val share = Intent(Intent.ACTION_SEND)
                        share.type = "image/png"

                        val fStream = activity!!.openFileInput("skatad.png")
                        val skatad = BitmapFactory.decodeStream(fStream)

                        skatad.compress(Bitmap.CompressFormat.PNG, 100, ByteArrayOutputStream())

                        val path = MediaStore.Images.Media.insertImage(
                            activity!!.contentResolver,
                            skatad, "Skatad Image", null
                        )

                        val imageUri = Uri.parse(path)

                        share.putExtra(Intent.EXTRA_STREAM, imageUri)
                        startActivity(Intent.createChooser(share, "Share via..."))
                    }
                }
                else -> Toast.makeText(
                    context, "How come??", Toast.LENGTH_LONG
                ).show() // weird... shouldn't happen
            }
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

    override fun onResume() {
        super.onResume()
        setUpView()
    }

    private fun setUpView() {
        when (appPreference.viewType) {
            "preview_image" -> {
                val fStream = activity!!.openFileInput("raw.png")

                val raw = BitmapFactory.decodeStream(fStream)

                imageView.setImageBitmap(raw)

                button.text = getString(R.string.scatter_image)
            }
            "skatad_image" -> {
                val fStream = activity!!.openFileInput("skatad.png")

                val skatad = BitmapFactory.decodeStream(fStream)
                imageView.setImageBitmap(skatad)

                button.text = getString(R.string.share)
            }
            else -> activity!!.onBackPressed() // will head back to Home() as the last resort
        }
    }

    private fun showLoader() {
        val loader = Loader.instance

        activity!!.supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.fragment_slide_bottom_enter, R.animator.fragment_slide_bottom_exit,
                R.animator.fragment_slide_top_enter, R.animator.fragment_slide_top_exit
            )
            .replace(R.id.container_main, loader, AppConstants.FRAG_LOADER)
            .addToBackStack(AppConstants.FRAG_LOADER)
            .commit()
    }

    companion object {

        private var ivInstance: ImageViewer? = null

        val instance: ImageViewer
            @Synchronized get() {
                if (ivInstance == null) ivInstance = ImageViewer()

                return ivInstance!!
            }
    }
}