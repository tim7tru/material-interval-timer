package com.timmytruong.materialintervaltimer.ui.home

import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.ResourceProvider
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class HomeViewModelTest : BehaviorSpec({

    val resources: ResourceProvider = mock()
    val timerRepository: TimerRepository = mock()
    val screen: HomeScreen = mock()
    val bottomSheet: TimerActionBottomSheetScreen = mock()

    val viewModel = HomeViewModel(
        timerRepository,
        screen,
        bottomSheet,
        resources
    ).apply {
        ioDispatcher = TestCoroutineDispatcher()
        mainDispatcher = TestCoroutineDispatcher()
    }

    Given("fetch recent timers is called") {
        viewModel.fetchRecentTimers()
        Then("timers is fetched from repo") {
            verify(timerRepository).getRecentTimers()
        }
    }

    Given("fetch favourite timers is called") {
        viewModel.fetchFavouriteTimers()
        Then("timers is fetched from repo") {
            verify(timerRepository).getFavouritedTimers()
        }
    }

    Given("on add clicked") {
        whenever(screen.navToCreateTimer()).thenReturn(mock())
        viewModel.onAddClicked()
        Then("navigation action is fetched from screen") {
            verify(screen).navToCreateTimer()
        }
    }
})