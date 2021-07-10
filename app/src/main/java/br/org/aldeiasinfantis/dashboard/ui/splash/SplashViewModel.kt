package br.org.aldeiasinfantis.dashboard.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
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

        viewModelScope.launch {

            authService.loginUser(email, password) { result ->
                when (result) {
                    /* If the user logs in, send the user information to view change activity */
                    is ServiceResult.Success -> {
                        getLoggedUserInformation(password)
                    }

                    is ServiceResult.Error -> {
                        val resultError = result.errorType

                        if (resultError == ErrorType.NETWORK_EXCEPTION ||
                            resultError == ErrorType.LOGIN_TIME_OUT
                        ) {
                            _hasConnectionProblem.call()
                        }
                    }
                }
            }
        }
    }

    private fun getLoggedUserInformation(password: String) {
        viewModelScope.launch {
            databaseService.getLoggedUserInformation { user, result ->
                when (result) {
                    is ServiceResult.Success -> {
                        user?.let { itUser ->
                            _loggedUserInformation.value = Pair(itUser, password)
                        }
                            ?: _hasConnectionProblem.call()
                    }

                    is ServiceResult.Error -> {
                        val resultError = result.errorType

                        if (resultError == ErrorType.NETWORK_EXCEPTION ||
                            resultError == ErrorType.LOGIN_TIME_OUT
                        ) {
                            _hasConnectionProblem.call()
                        }
                    }
                }
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