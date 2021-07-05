package br.org.aldeiasinfantis.dashboard.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.SingleLiveEvent
import br.org.aldeiasinfantis.dashboard.data.local.Preferences
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.User
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferences: Preferences,
    private val authService: AuthRepository,
    private val databaseService: DatabaseRepository
) : ViewModel() {

    private val _hasConnectionProblem: SingleLiveEvent<Unit> = SingleLiveEvent()
    val hasConnectionProblem: LiveData<Unit>
        get() = _hasConnectionProblem

    private val _loggedUserInformation: MutableLiveData<Pair<User, String>> = MutableLiveData()
    val loggedUserInformation: LiveData<Pair<User, String>>
        get() = _loggedUserInformation

    fun loginUser(email: String, password: String) {

        authService.loginUser(email, password) { result ->

            when (result) {
                // If the user logs in, send the user information to view change activity
                is ServiceResult.Success -> {
                    databaseService.getLoggedUserInformation { user, databaseResult ->
                        when (databaseResult) {
                            is ServiceResult.Success -> {
                                user?.let { itUser ->
                                    _loggedUserInformation.value = Pair(itUser, password)
                                }
                                    ?: _hasConnectionProblem.postValue(Unit)
                            }

                            is ServiceResult.Error ->
                                _loggedUserInformation.value = null
                        }
                    }
                }

                is ServiceResult.Error ->
                    if (result.errorType == ErrorType.NETWORK_EXCEPTION)
                        _hasConnectionProblem.postValue(Unit)
            }
        }
    }

    fun fetchRememberedAccount(): Pair<String, String>? {
        val (email, password) = preferences.getEmailAndPasswordValue()

        return if (email != null && password != null)
            Pair(email, password)

        else null
    }
}