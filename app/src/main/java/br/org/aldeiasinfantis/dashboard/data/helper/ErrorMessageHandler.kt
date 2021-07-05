package br.org.aldeiasinfantis.dashboard.data.helper

import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType
import br.org.aldeiasinfantis.dashboard.data.model.Global

object ErrorMessageHandler {
    fun getErrorMessage(resProvider: ResourceProvider, errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.UNEXPECTED_ERROR -> resProvider.getString(R.string.unexpected_error_message)
            ErrorType.SERVER_ERROR -> resProvider.getString(R.string.server_error_message)
            ErrorType.NETWORK_EXCEPTION -> resProvider.getString(R.string.network_exception_message)
            ErrorType.LOGIN_TIME_OUT -> resProvider.getString(R.string.login_time_out_message)
            ErrorType.AUTH_EXCEPTION -> resProvider.getString(R.string.auth_exception_message)
            ErrorType.EMPTY_FIELD -> resProvider.getString(R.string.empty_field_error_message)
            ErrorType.WEAK_PASSWORD -> resProvider.getString(
                R.string.weak_password_error_message,
                Global.PASSWORD_MINIMUM_LENGTH)
            ErrorType.ACCOUNT_NOT_FOUND -> resProvider.getString(R.string.account_not_found_message)
            ErrorType.INVALID_EMAIL -> resProvider.getString(R.string.invalid_email_message)
//            ErrorType.INVALID_PHONE -> resProvider.getString(R.string.invalid_phone_message)
            ErrorType.EMAIL_ALREADY_REGISTERED -> resProvider.getString(R.string.email_already_registered_error_message)
//            ErrorType.PASSWORD_CONFIRM_DONT_MATCH -> resProvider.getString(R.string.password_confirm_dont_match_error_message)
            else -> resProvider.getString(R.string.unexpected_error_message)
        }
    }
}