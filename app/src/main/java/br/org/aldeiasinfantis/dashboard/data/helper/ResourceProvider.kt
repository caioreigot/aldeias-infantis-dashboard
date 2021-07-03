package br.org.aldeiasinfantis.dashboard.data.helper

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(@ApplicationContext private val context: Context) {

    fun getString(@StringRes stringID: Int): String =
        context.getString(stringID)

    fun getString(@StringRes stringID: Int, p2: Any): String =
        context.getString(stringID, p2)

    fun getString(@StringRes stringID: Int, p2: Any, p3: Any): String =
        context.getString(stringID, p2, p3)
}