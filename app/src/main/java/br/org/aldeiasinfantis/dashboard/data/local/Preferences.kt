package br.org.aldeiasinfantis.dashboard.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class Preferences @Inject constructor(@ApplicationContext private val context: Context) {

    private val APP_SETTINGS = "aldeias_infantis_dashboard_settings"
    private val PREF_EMAIL = "email"
    private val PREF_PASSWORD = "password"

    private fun getSharedPreferences(): SharedPreferences =
        context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE)

    fun getEmailAndPasswordValue(): Pair<String?, String?> =
        Pair(
            getSharedPreferences().getString(PREF_EMAIL, null),
            getSharedPreferences().getString(PREF_PASSWORD, null)
        )

    fun setEmailAndPasswordValue(email: String, password: String) {
        getSharedPreferences()
            .edit()
            .putString(PREF_EMAIL, email)
            .putString(PREF_PASSWORD, password)
            .apply()
    }

    fun clearPreferences() {
        getSharedPreferences()
            .edit()
            .clear()
            .apply()
    }

}