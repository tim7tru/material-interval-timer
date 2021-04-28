package com.timmytruong.materialintervaltimer.ui.reusable.action

import android.content.Context
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.DISMISS_EVENT
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.WeakContext
import com.timmytruong.materialintervaltimer.utils.string
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class TimerActionViewModel @Inject constructor(
    @WeakContext private val ctx: WeakReference<Context>,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerActionBottomSheetScreen
) : BaseViewModel() {

    fun onFavouritedClicked(id: Int, favourite: Boolean) = startSuspending(ioDispatcher) {
        timerRepository.setFavourite(id = id, favourite = favourite)
        fireEvents(
            FAVOURITE to when (favourite) {
                true -> ctx.string(R.string.favourited)
                false -> ctx.string(R.string.unfavourited)
            }
        )
    }

    fun onDeleteClicked(id: Int) = startSuspending(ioDispatcher) {
        timerRepository.deleteTimer(id = id)
        fireEvents(DELETE to ctx.string(R.string.deleted))
    }

    fun onStartClicked(id: Int) = navigateWith(screen.navToTimer(id))
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerActionViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerActionBottomSheetScreen() = TimerActionBottomSheetScreen()
}