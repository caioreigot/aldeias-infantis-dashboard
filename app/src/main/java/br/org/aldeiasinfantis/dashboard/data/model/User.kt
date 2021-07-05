package br.org.aldeiasinfantis.dashboard.data.model

import com.google.firebase.database.PropertyName

data class User(
    @get:PropertyName(Global.DatabaseNames.USER_NAME)
    @set:PropertyName(Global.DatabaseNames.USER_NAME)
    var name: String = "",

    @get:PropertyName(Global.DatabaseNames.USER_EMAIL)
    @set:PropertyName(Global.DatabaseNames.USER_EMAIL)
    var email: String = "",

    var isAdmin: Boolean = false
)
