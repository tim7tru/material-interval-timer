package com.timmytruong.materialintervaltimer.ui.create.timer

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.*
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateTimerViewModelTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val resources: ResourceProvider = mock()
    val date: DateProvider = mock()
    val timerStore: Store<Timer> = mock {
        on { observe }.thenReturn(emptyFlow())
        on { get() }.thenReturn(TIMER)
    }
    val timerRepository: TimerRepository = mock()
    val screen = CreateTimerScreen()

    fun create(timerScreen: CreateTimerScreen = screen) = CreateTimerViewModel(
        ioDispatcher = TestCoroutineDispatcher(),
        mainDispatcher = TestCoroutineDispatcher(),
        timerStore = timerStore,
        screen = timerScreen,
        timerLocalDataSource = timerRepository,
        resources = resources,
        date = date
    )

    Given("store is updated with timer") {
        whenever(timerStore.observe).thenReturn(flowOf(TIMER))
        create().fetchCurrentTimer(true)
        Then("timer count is set") {
            assertEquals(INTERVAL_COUNT, screen.timerIntervalCount.get())
        }
        Then("title is set") {
            assertEquals(TITLE, screen.timerTitle.get())
        }
        Then("interval sound name is set") {
            assertEquals(SOUND_NAME, screen.timerSelectedSound.get())
        }
    }

    Given("clear timer is called") {
        create().clearTimer()
        Then("store is updated") { verify(timerStore).update(any()) }
        Then("timer title is empty") {
            assertEquals("", screen.timerTitle.get())
        }
        Then("timer count is 0") {
            assertEquals(0, screen.timerIntervalCount.get())
        }
        Then("interval sound name is empty") {
            assertEquals("None", screen.timerSelectedSound.get())
        }
    }

    Given("timer title is inputted") {
        When("should clear store") {
            create().apply {
                fetchCurrentTimer(true)
                setTimerTitle(TITLE)
            }
            Then("store is cleared and updated") { verify(timerStore, times(2)).update(any())}
        }

        When("should not clear store") {
            create().apply {
                fetchCurrentTimer(false)
                setTimerTitle(TITLE)
            }
            Then("store is updated") { verify(timerStore).update(any())}
        }
    }

    Given("repeat is triggered") {
        When("repeat is true") {
            create().setRepeat(true)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertTrue(screen.timerIsRepeated.get()) }
        }
        When("repeat is false") {
            create().setRepeat(false)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertFalse(screen.timerIsRepeated.get()) }
        }
    }

    Given("favourite is triggered") {
        When("favourite is true") {
            create().setFavourite(true)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertTrue(screen.timerIsSaved.get()) }
        }
        When("favourite is false") {
            create().setRepeat(false)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertFalse(screen.timerIsSaved.get()) }
        }
    }

    Given("validate timer is called") {
        val action: NavDirections = mock()
        val navScreen: CreateTimerScreen = mock { on { navToTimer(any()) }.thenReturn(action) }
        timerRepository::saveNewTimer.stub { onBlocking { this.invoke(any()) }.thenReturn(TIMER_ID.toLong()) }
        val viewModel = create(navScreen)
        viewModel.navigateFlow.test {
            viewModel.validateTimer(TITLE)
            Then("timer is updated in store") { verify(timerStore).update(any()) }
//            Then("date is fetched") {
//                val captor = argumentCaptor<(Timer) -> Unit>()
//                verify(timerStore).update(captor.capture())
//                captor.firstValue.invoke(TIMER)
//                verify(date, times(2)).getCurrentDate()
//            }
            Then("timer is saved to repository") { verify(timerRepository).saveNewTimer(TIMER) }
            Then("navigate event is retrieved") { verify(navScreen).navToTimer(TIMER_ID) }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }

    Given("add interval is clicked") {
        val navScreen: CreateTimerScreen = mock()
        val action: NavDirections = mock()
        navScreen::navToAddInterval.stub { on { this.invoke() }.doReturn(action) }
        val viewModel = create(navScreen)
        viewModel.navigateFlow.test {
            viewModel.onGoToAddIntervalClicked()
            Then("navigate event is retrieved") { verify(navScreen).navToAddInterval() }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }

    Given("sounds is clicked") {
        val action: NavDirections = mock()
        val navScreen: CreateTimerScreen =
            mock { on { navToSoundBottomSheet(any()) }.thenReturn(action) }
        val viewModel = create(navScreen)
        viewModel.navigateFlow.test {
            viewModel.onGoToSoundsBottomSheet()
            Then("navigate event is retrieved") { verify(navScreen).navToSoundBottomSheet(SOUND_ID) }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }
})