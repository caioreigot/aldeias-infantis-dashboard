package br.org.aldeiasinfantis.dashboard.data.model

enum class ErrorType {
    UNEXPECTED_ERROR,
    SERVER_ERROR,
    AUTH_EXCEPTION,
    NETWORK_EXCEPTION,
    LOGIN_TIME_OUT,
    EMPTY_FIELD,
    ACCOUNT_NOT_FOUND,
    INVALID_EMAIL,
    EMAIL_ALREADY_REGISTERED,
    WEAK_PASSWORD,
}