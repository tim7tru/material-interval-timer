package com.timmytruong.materialintervaltimer.di.modules

import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import com.timmytruong.materialintervaltimer.utils.providers.DateProviderImpl
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ProviderModule {

    @Singleton
    @Provides
    fun providePopUpProvider(): PopUpProvider =
        PopUpProviderImpl()

    @Singleton
    @Provides
    fun provideDateProvider(): DateProvider = DateProviderImpl()
}