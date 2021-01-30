package com.timmytruong.materialintervaltimer.di

import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.ui.home.adapters.HorizontalTimerItemAdapter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Qualifier

@InstallIn(FragmentComponent::class)
@Module
class HomeAdapterModule {

    @FragmentScoped
    @Recents
    @Provides
    fun recentsAdapter(homeViewModel: HomeViewModel) = HorizontalTimerItemAdapter(homeViewModel)

    @FragmentScoped
    @Favourites
    @Provides
    fun favouritesAdapter(homeViewModel: HomeViewModel) = HorizontalTimerItemAdapter(homeViewModel)

}

@Qualifier annotation class Recents
@Qualifier annotation class Favourites