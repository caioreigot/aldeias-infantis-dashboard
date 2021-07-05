package br.org.aldeiasinfantis.dashboard.data.remote

import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.Global
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository

class AuthService : AuthRepository {

    override fun registerUser(
        name: String,
        email: String,
        password: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isRegisterInformationValid(
            name = name,
            email = email,
            password = password
        )

        if (!isValid) {
            callback(ServiceResult.Error(errorType!!))
            return
        }

        Singleton.AUTH
            .createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                when (task.isSuccessful) {
                    true -> {
                        val userID = Singleton.AUTH.currentUser?.uid

                        userID?.let { uid ->
                            val currentUserDB = Singleton.DB_USERS_REF.child(uid)

                            with (currentUserDB) {
                                child(Global.DatabaseNames.USER_NAME).setValue(name)
                                child(Global.DatabaseNames.USER_EMAIL).setValue(email)
                            }
                        }

                        callback(ServiceResult.Success)
                    }

                    false -> {
                        if (task.exception?.message == "The email address is already in use by another account.")
                            callback(ServiceResult.Error(ErrorType.EMAIL_ALREADY_REGISTERED))
                        else
                            callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                    }
                }
            }
    }

    override fun sendPasswordResetEmail(
        email: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        if (!Utils.isValidEmail(email)) {
            callback(ServiceResult.Error(ErrorType.INVALID_EMAIL))
            return
        }

        Singleton.AUTH.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                    callback(ServiceResult.Success)
                else
                    callback(ServiceResult.Error(ErrorType.SERVER_ERROR))
            }
    }
}