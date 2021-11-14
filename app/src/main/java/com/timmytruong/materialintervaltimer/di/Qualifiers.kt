package com.timmytruong.materialintervaltimer.di

import javax.inject.Qualifier

@Qualifier annotation class TimerStore
@Qualifier annotation class IntervalStore

@Qualifier annotation class BackgroundDispatcher
@Qualifier annotation class MainDispatcher

@Qualifier annotation class HorizontalProgress
@Qualifier annotation class CircularProgress

@Qualifier annotation class Recents
@Qualifier annotation class Favorites