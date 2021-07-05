package br.org.aldeiasinfantis.dashboard.di

import br.org.aldeiasinfantis.dashboard.data.remote.AuthService
import br.org.aldeiasinfantis.dashboard.data.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AuthServiceModule {

    @Provides
    fun provideAuthService(): AuthRepository = AuthService()
}