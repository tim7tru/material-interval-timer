package com.timmytruong.materialintervaltimer.di.modules

import android.content.Context
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProviderImpl
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import com.timmytruong.materialintervaltimer.utils.providers.DateProviderImpl
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ProviderModule {

    @Singleton
    @Provides
    fun provideResourcesProvider(@ApplicationContext context: Context): ResourceProvider =
        ResourceProviderImpl(context)

    @Singleton
    @Provides
    fun providePopUpProvider(): PopUpProvider =
        PopUpProviderImpl()

    @Singleton
    @Provides
    fun provideDateProvider(): DateProvider = DateProviderImpl()
}