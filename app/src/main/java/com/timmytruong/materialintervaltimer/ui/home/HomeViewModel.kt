package com.timmytruong.materialintervaltimer.ui.home

import android.content.Context
import androidx.databinding.ObservableField
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.TimerListScreenBinding
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_I
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val timerRepository: TimerRepository,
    private val screen: HomeScreen,
    private val bottomSheet: TimerActionBottomSheetScreen
) : BaseViewModel() {

    fun fetchRecentTimers() {
        screen.recents = timerRepository.getRecentTimers().map(::mapTimerList)
    }

    fun fetchFavouriteTimers() {
        screen.favourites = timerRepository.getFavouriteTimers().map(::mapTimerList)
    }

    fun onAddClicked() = navigateWith(screen.navToCreateTimer())

    private suspend fun mapTimerList(list: List<Timer>) = withContext(ioDispatcher) {
        return@withContext when {
            list.size < 7 -> list.subList(0, list.size).map(::mapTimerToBinding)
            else -> list.map(::mapTimerToBinding)
        }
    }

    private fun mapTimerToBinding(timer: Timer) = TimerListScreenBinding(
        time = ObservableField(
            formatNormalizedTime(
                getTimeFromSeconds(timer.timer_total_time_ms / MILLI_IN_SECS_I),
                ctx.string(R.string.timerTimeFormat)
            )
        ),
        title = ObservableField(timer.timer_title),
        intervalCount = ObservableField(
            String.format(
                ctx.string(R.string.number_of_intervals_format),
                timer.timer_intervals_count
            )
        ),
        timerId = timer.id,
        clicks = ::onTimerCardClicked
    )

    private fun onTimerCardClicked(binding: TimerListScreenBinding) =
        startSuspending(ioDispatcher) {
            val timer = timerRepository.getTimerById(binding.timerId)
            bottomSheet.timerId.set(timer.id)
            bottomSheet.isFavourited.set(timer.timer_saved)
            navigateWith(screen.navToBottomSheet())
        }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class HomeViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideHomeScreen() = HomeScreen()
}