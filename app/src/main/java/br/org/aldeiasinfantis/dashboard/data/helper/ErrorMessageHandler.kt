package br.org.aldeiasinfantis.dashboard.data.helper

import br.org.aldeiasinfantis.dashboard.R
import br.org.aldeiasinfantis.dashboard.data.model.ErrorType

object ErrorMessageHandler {
    fun getErrorMessage(resProvider: ResourceProvider, errorType: ErrorType): String {
        return when (errorType) {
            ErrorType.UNEXPECTED_ERROR -> resProvider.getString(R.string.unexpected_error_message)
            ErrorType.SERVER_ERROR -> resProvider.getString(R.string.server_error_message)
            ErrorType.EMPTY_FIELD -> resProvider.getString(R.string.empty_field_error_message)
//            ErrorType.ACCOUNT_NOT_FOUND -> resProvider.getString(R.string.account_not_found_error_message)
            ErrorType.INVALID_EMAIL -> resProvider.getString(R.string.invalid_email_message)
//            ErrorType.INVALID_PHONE -> resProvider.getString(R.string.invalid_phone_message)
            ErrorType.EMAIL_ALREADY_REGISTERED -> resProvider.getString(R.string.email_already_registered_error_message)
//            ErrorType.PASSWORD_CONFIRM_DONT_MATCH -> resProvider.getString(R.string.password_confirm_dont_match_error_message)
            else -> resProvider.getString(R.string.unexpected_error_message)
        }
    }
}