package com.timmytruong.materialintervaltimer.ui.list

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.di.WeakContext
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.TimerListScreenBinding
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_I
import com.timmytruong.materialintervaltimer.utils.formatNormalizedTime
import com.timmytruong.materialintervaltimer.utils.getTimeFromSeconds
import com.timmytruong.materialintervaltimer.utils.string
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
class TimerListViewModel @Inject constructor(
    @WeakContext private val ctx: WeakReference<Context>,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: TimerListScreen,
    private val bottomSheet: TimerActionBottomSheetScreen
) : BaseViewModel() {

    fun fetchTimers() = startSuspending(ioDispatcher) {
        screen.timers = when (screen.screenName) {
            FavouritesFragment::class.java.simpleName -> timerRepository.getFavouritedTimers()
                .map { it.map(::mapTimerToBinding) }
            RecentsFragment::class.java.simpleName -> timerRepository.getRecentTimers()
                .map { it.map(::mapTimerToBinding) }
            else -> error("fragment type not found")
        }
    }

    private fun mapTimerToBinding(timer: Timer) = TimerListScreenBinding(
        time = ObservableField(
            formatNormalizedTime(
                getTimeFromSeconds(timer.totalTimeMs / MILLI_IN_SECS_I),
                ctx.string(R.string.timerTimeFormat)
            )
        ),
        title = ObservableField(timer.title),
        intervalCount = ObservableField(
            String.format(
                ctx.string(R.string.number_of_intervals_format),
                timer.intervalCount
            )
        ),
        timerId = timer.id,
        clicks = {
            startSuspending(ioDispatcher) {
                bottomSheet.timerId.set(timer.id)
                bottomSheet.isFavourite.set(timer.isFavourited)
                navigateWith(screen.navToBottomSheet())
            }
        }
    )
}

data class TimerListScreen(
    val isEmpty: ObservableBoolean = ObservableBoolean(false),
    var timers: Flow<@JvmSuppressWildcards List<TimerListScreenBinding>> = emptyFlow()
) : BaseScreen() {

    fun navToBottomSheet() = when (screenName) {
        FavouritesFragment::class.java.simpleName -> FavouritesFragmentDirections.actionFavouritesFragmentToTimerActionBottomSheet()
        RecentsFragment::class.java.simpleName -> RecentsFragmentDirections.actionRecentsFragmentToTimerActionBottomSheet()
        else -> error("fragment type not found")
    }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class TimerListViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideTimerListScreen() = TimerListScreen()

}