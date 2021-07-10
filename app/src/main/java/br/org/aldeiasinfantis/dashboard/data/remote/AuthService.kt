package br.org.aldeiasinfantis.dashboard.data.remote

import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.Global
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AuthService : AuthRepository {

    override suspend fun loginUser(
        email: String,
        password: String,
        callback: (result: ServiceResult) -> Unit
    ) {
        val (isValid, errorType) = Utils.isLoginInformationValid(
            email = email,
            password = password
        )

        if (!isValid) {
            callback(ServiceResult.Error(errorType!!))
            return
        }

        withTimeout(Global.AUTH_TIME_OUT_IN_MILLIS) {
            try {
                Singleton.AUTH
                    .signInWithEmailAndPassword(email, password)
                    .await()

                callback(ServiceResult.Success)
            } catch (e: Exception) {
                when (e) {
                    is TimeoutCancellationException ->
                        callback(ServiceResult.Error(ErrorType.LOGIN_TIME_OUT))

                    is FirebaseNetworkException ->
                        callback(ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))

                    is FirebaseAuthInvalidUserException ->
                        callback(ServiceResult.Error(ErrorType.AUTH_INVALID_USER))

                    else ->
                        callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }
            }
        }
    }

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
                        when ((task.exception as FirebaseAuthException).errorCode) {
                            "ERROR_EMAIL_ALREADY_IN_USE" ->
                                callback(ServiceResult.Error(ErrorType.EMAIL_ALREADY_REGISTERED))

                            "ERROR_INVALID_EMAIL" ->
                                callback(ServiceResult.Error(ErrorType.INVALID_EMAIL))

                            else ->
                                callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                        }
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
            .addOnSuccessListener { callback(ServiceResult.Success) }
            .addOnFailureListener { callback(ServiceResult.Error(ErrorType.SERVER_ERROR)) }
    }
}