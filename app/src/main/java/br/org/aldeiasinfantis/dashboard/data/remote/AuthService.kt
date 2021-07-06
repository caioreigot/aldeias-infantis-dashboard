package br.org.aldeiasinfantis.dashboard.data.remote

import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.Global
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.Singleton
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AuthService : AuthRepository {

    override fun loginUser(
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

        val runnable = Runnable {
            val task: Task<AuthResult> = Singleton.AUTH
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { callback(ServiceResult.Success) }

            try {
                Tasks.await(task, 10, TimeUnit.SECONDS)
            } catch (e: ExecutionException) {
                when (e.cause) {
                    // Errors related to Firebase Auth
                    is FirebaseAuthException -> {
                        when (e.cause) {
                            is FirebaseAuthInvalidUserException ->
                                callback(ServiceResult.Error(ErrorType.AUTH_INVALID_USER))

                            else ->
                                callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                        }
                    }

                    is TimeoutException ->
                        callback(ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))

                    is FirebaseNetworkException ->
                        callback(ServiceResult.Error(ErrorType.NETWORK_EXCEPTION))

                    else ->
                        callback(ServiceResult.Error(ErrorType.UNEXPECTED_ERROR))
                }
            }
        }

        // Thread to try sign in user
        val thread = Thread(runnable)
        thread.start()
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