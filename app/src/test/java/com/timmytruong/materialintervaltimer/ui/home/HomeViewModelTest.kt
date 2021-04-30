package com.timmytruong.materialintervaltimer.ui.home

import android.content.Context
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
class HomeViewModelTest : BehaviorSpec({

    val context: WeakReference<Context> = mock()
    val mainDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    val backgroundDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    val timerRepository: TimerRepository = mock()
    val screen: HomeScreen = mock()
    val bottomSheet: TimerActionBottomSheetScreen = mock()

    val viewModel = HomeViewModel(
        context,
        mainDispatcher,
        backgroundDispatcher,
        timerRepository,
        screen,
        bottomSheet
    )

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
        stub { on { screen.navToCreateTimer() }.doReturn(mock()) }
        viewModel.onAddClicked()
        Then("navigation action is fetched from screen") {
            verify(screen).navToCreateTimer()
        }
    }
})