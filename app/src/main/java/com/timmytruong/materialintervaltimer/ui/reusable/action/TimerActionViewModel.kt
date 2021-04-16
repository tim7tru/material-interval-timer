package com.timmytruong.materialintervaltimer.ui.reusable.action

import android.content.Context
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.utils.string
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class TimerActionViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerActionBottomSheetScreen
) : BaseViewModel() {

    fun onFavouritedClicked(id: Int, isFavourited: Boolean) = startSuspending(ioDispatcher) {
        timerRepository.setShouldSave(id = id, saved = isFavourited)
        fireEvent(
            FAVOURITE,
            if (isFavourited) {
                ctx.string(R.string.favourited)
            } else {
                ctx.string(R.string.unfavourited)
            }
        )
    }

    fun onDeleteClicked(id: Int) = startSuspending(ioDispatcher) {
        timerRepository.deleteTimer(id = id)
        fireEvent(DELETE, ctx.string(R.string.deleted))
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