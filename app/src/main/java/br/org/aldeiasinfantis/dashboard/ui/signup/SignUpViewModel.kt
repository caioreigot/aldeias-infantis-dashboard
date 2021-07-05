package br.org.aldeiasinfantis.dashboard.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.org.aldeiasinfantis.dashboard.data.helper.ErrorMessageHandler
import br.org.aldeiasinfantis.dashboard.data.helper.ResourceProvider
import br.org.aldeiasinfantis.dashboard.data.helper.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val resProvider: ResourceProvider
) : ViewModel() {

    private val _errorMessage: MutableLiveData<String> = MutableLiveData()
    val errorMessage: LiveData<String>
        get() = _errorMessage

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
    }
}