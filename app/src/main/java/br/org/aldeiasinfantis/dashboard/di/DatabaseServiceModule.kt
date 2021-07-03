package br.org.aldeiasinfantis.dashboard.di

import br.org.aldeiasinfantis.dashboard.data.remote.DatabaseService
import br.org.aldeiasinfantis.dashboard.data.repository.DatabaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseServiceModule {

    @Provides
    fun provideDatabaseService(): DatabaseRepository = DatabaseService()
}