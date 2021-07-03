package br.org.aldeiasinfantis.dashboard.data.model

enum class ErrorType {
    UNEXPECTED_ERROR,
    SERVER_ERROR,
    EMPTY_FIELD,
    ACCOUNT_NOT_FOUND,
    INVALID_EMAIL,
    INVALID_PHONE,
    EMAIL_ALREADY_REGISTERED,
    WEAK_PASSWORD,
    PASSWORD_CONFIRM_DONT_MATCH
}