package br.org.aldeiasinfantis.dashboard.data.helper

import android.app.Activity
import android.app.Dialog
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType

class Utils {

    companion object {
        private fun isValidEmail(target: CharSequence): Boolean =
            !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()

        fun isRegisterInformationValid(
            name: String? = null,
            email: String? = null,
            password: String? = null
        ): Pair<Boolean, ErrorType?>
        {
            if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password))
            {
                return Pair(false, ErrorType.EMPTY_FIELD)
            }

            if (!isValidEmail(email!!))
                return Pair(false, ErrorType.INVALID_EMAIL)

            /*if (password!!.length < Global.PASSWORD_MINIMUM_LENGTH)
                return Pair(false, ErrorType.WEAK_PASSWORD)*/

            return Pair(true, null)
        }
    }

}