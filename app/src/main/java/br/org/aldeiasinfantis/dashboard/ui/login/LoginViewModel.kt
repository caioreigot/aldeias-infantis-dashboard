package br.org.aldeiasinfantis.dashboard.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.local.Preferences
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.model.User
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val resProvider: ResourceProvider,
    private val preferences: Preferences,
    private val authService: AuthRepository,
    private val databaseService: DatabaseRepository
) : ViewModel() {

    companion object {
        const val VIEW_FLIPPER_BUTTON = 0
        const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _loginViewFlipper: MutableLiveData<Int> = MutableLiveData()
    val loginViewFlipper: LiveData<Int>
        get() = _loginViewFlipper

    private val _loggedUserInformation: MutableLiveData<Pair<User, String>> = MutableLiveData()
    val loggedUserInformation: LiveData<Pair<User, String>>
        get() = _loggedUserInformation

    fun loginUser(email: String, password: String) {

        _loginViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        authService.loginUser(email, password) { result ->

            when (result) {

                // If the user logs in, send the user information to view change activity
                is ServiceResult.Success -> {
                    databaseService.getLoggedUserInformation { user, databaseResult ->
                        when (databaseResult) {
                            is ServiceResult.Success -> {
                                user?.let { itUser ->
                                    _loggedUserInformation.value = Pair(itUser, password)
                                } ?: run {
                                    _errorMessage.value =
                                        ErrorMessageHandler
                                            .getErrorMessage(resProvider, ErrorType.UNEXPECTED_ERROR)
                                }
                            }

                            is ServiceResult.Error -> {
                                _loggedUserInformation.value = null

                                _errorMessage.value =
                                    ErrorMessageHandler
                                        .getErrorMessage(resProvider, databaseResult.errorType)
                            }
                        }

                        _loginViewFlipper.value = VIEW_FLIPPER_BUTTON
                    }
                }

                is ServiceResult.Error -> {
                    _errorMessage.postValue(ErrorMessageHandler
                        .getErrorMessage(resProvider, result.errorType)
                    )

                    _loginViewFlipper.postValue(VIEW_FLIPPER_BUTTON)
                }
            }
        }
    }

    fun rememberAccount(email: String, password: String) {
        preferences.setEmailAndPasswordValue(email, password)
    }
}