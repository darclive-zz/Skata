package ictlife.test.skata.app

import android.content.Context

class AppPreference private constructor(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // read books
    var viewType: String?
        get() = sharedPreferences.getString(PREF_VIEW_TYPE, "preview_image")
        set(view) = sharedPreferences.edit().putString(PREF_VIEW_TYPE, view).apply()


    companion object {

        // some variables
        const val PREFS_NAME = "skata_preferences"
        const val PREF_VIEW_TYPE = "view"

        private var prefsInstance: AppPreference? = null

        @Synchronized
        fun initializeInstance(mContext: Context) {
            if (prefsInstance == null) prefsInstance = AppPreference(mContext)
        }

        val instance: AppPreference
            @Synchronized get() {
                if (prefsInstance == null) {
                    throw IllegalStateException(
                        AppPreference::class.java.simpleName +
                                " is not initialized, call initializeInstance(..) method first."
                    )
                }
                return prefsInstance!!
            }
    }
}
