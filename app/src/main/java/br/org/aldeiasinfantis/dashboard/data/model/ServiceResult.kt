package br.org.aldeiasinfantis.dashboard.data.model

sealed class ServiceResult {
    object Success : ServiceResult()
    class Error(val errorType: ErrorType) : ServiceResult()
}
