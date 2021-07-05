package br.org.aldeiasinfantis.dashboard.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.SingleLiveEvent
import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val resProvider: ResourceProvider,
    private val authService: AuthRepository
) : ViewModel() {

    companion object {
        const val VIEW_FLIPPER_REGISTER_BUTTON = 0
        const val VIEW_FLIPPER_PROGRESS_BAR = 1
    }

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _registrationMade: SingleLiveEvent<Unit> = SingleLiveEvent()
    val registrationMade: LiveData<Unit>
        get() = _registrationMade

    private val _signUpViewFlipper: MutableLiveData<Int> = MutableLiveData()
    val signUpViewFlipper: LiveData<Int>
        get() = _signUpViewFlipper

    fun registerUser(
        name: String,
        email: String,
        password: String
    ) {
        val (isValid, errorType) = Utils.isRegisterInformationValid(
            name = name,
            email = email,
            password = password
        )

        if (!isValid) {
            _errorMessage.value = ErrorMessageHandler.getErrorMessage(
                resProvider,
                errorType!!
            )

            return
        }

        _signUpViewFlipper.value = VIEW_FLIPPER_PROGRESS_BAR

        authService.registerUser(
            name = name,
            email = email,
            password = password
        ) { result ->

            _signUpViewFlipper.value = VIEW_FLIPPER_REGISTER_BUTTON

            when (result) {
                is ServiceResult.Success -> _registrationMade.call()

                is ServiceResult.Error -> {
                    _errorMessage.value =
                        ErrorMessageHandler.getErrorMessage(resProvider, result.errorType)
                }
            }
        }
    }
}