package com.timmytruong.data.di

import javax.inject.Qualifier

@Qualifier annotation class TimerStore
@Qualifier annotation class IntervalStore

@Qualifier annotation class BackgroundDispatcher
@Qualifier annotation class MainDispatcher
