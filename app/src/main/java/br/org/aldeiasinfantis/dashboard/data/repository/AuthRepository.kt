package br.org.aldeiasinfantis.dashboard.data.repository

import br.org.aldeiasinfantis.dashboard.data.model.ServiceResult

interface AuthRepository {
    fun loginUser(
        email: String,
        password: String,
        callback: (result: ServiceResult) -> Unit
    )

    fun registerUser(
        name: String,
        email: String,
        password: String,
        callback: (result: ServiceResult) -> Unit
    )

    fun sendPasswordResetEmail(
        email: String,
        callback: (result: ServiceResult) -> Unit
    )
}