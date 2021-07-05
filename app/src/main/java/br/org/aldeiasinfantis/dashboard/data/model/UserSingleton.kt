package br.org.aldeiasinfantis.dashboard.data.model

object UserSingleton {

    var name: String = ""
    var email: String = ""
    var isAdmin: Boolean = false

    fun set(user: User) {
        apply {
            name = user.name
            email = user.email
            isAdmin = user.isAdmin
        }
    }

    fun clear() {
        name = ""
        email = ""
        isAdmin = false
    }
}