package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateIntervalTimeViewModelTest : BehaviorSpec({

    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val resources: ResourceProvider = mock()
    val timerStore: Store<Timer> = mock()
    val intervalStore: Store<Interval> = mock()
    val createIntervalTimeScreen = CreateIntervalTimeScreen()

    fun subject(screen: CreateIntervalTimeScreen = createIntervalTimeScreen) =
        CreateIntervalTimeViewModel(
            ioDispatcher = ioDispatcher,
            mainDispatcher = mainDispatcher,
            resources = resources,
            timerStore = timerStore,
            intervalStore = intervalStore,
            screen = screen
        )

    Given("Add to time is called") {
        When("Time length is less than six") {
            val displayTime = "00h 00m 01s"
            whenever(resources.string(any(), any(), any(), any())).thenReturn(displayTime)
            val viewModel = subject()
            viewModel.addToTime("1")

            Then("1 is added to time") {
                argumentCaptor<CharSequence>().apply {
                    verify(resources).string(eq(R.string.inputTimeFormat), capture(), capture(), capture())
                    val zeros = "00"
                    assertEquals(zeros, firstValue)
                    assertEquals(zeros, secondValue)
                    assertEquals("01", thirdValue)
                }
            }

            Then("Display time is set") {
                assertEquals(displayTime, createIntervalTimeScreen.intervalDisplayTime.get())
            }

            Then("Display validity is set") {
                assertTrue(createIntervalTimeScreen.intervalTimeLengthValidity.get())
            }
        }

        When("Time length is six") {
            val displayTime = "12h 34m 56s"
            whenever(resources.string(any(), any(), any(), any())).thenReturn(displayTime)
            val viewModel = subject()
            (1..7).forEach { viewModel.addToTime("$it") }

            Then("Time is formatted only six times") {
                verify(resources, times(6)).string(any(), any(), any(), any())
            }

            Then("7 is not added to the time") {
                assertEquals(displayTime, createIntervalTimeScreen.intervalDisplayTime.get())
            }

            Then("Display validity is set") {
                assertTrue(createIntervalTimeScreen.intervalTimeLengthValidity.get())
            }
        }
    }

    Given("Remove from time is called") {
        When("Time length is more than zero") {
            val displayTime = "01h 23m 45s"
            whenever(resources.string(any(), any(), any(), any())).thenReturn(displayTime)
            val viewModel = subject()
            viewModel.fill()
            viewModel.removeFromTime()

            Then("Display time is set") {
                assertEquals(displayTime, createIntervalTimeScreen.intervalDisplayTime.get())
            }

            Then("Display validity is set") {
                assertTrue(createIntervalTimeScreen.intervalTimeLengthValidity.get())
            }
        }

        When ("Time length is zero") {
            val displayTime = "00h 00m 00s"
            whenever(resources.string(any(), any(), any(), any())).thenReturn(displayTime)
            val viewModel = subject()
            viewModel.removeFromTime()

            Then("String was not formatted") {
                verifyZeroInteractions(resources)
            }

            Then("Display time is empty") {
                assertEquals("00h 00m 00s", createIntervalTimeScreen.intervalDisplayTime.get())
            }

            Then("Display validity is invalid") {
                assertFalse(createIntervalTimeScreen.intervalTimeLengthValidity.get())
            }
        }
    }

    Given("Add interval is called") {
        val action: NavDirections = mock()
        val mockScreen: CreateIntervalTimeScreen = mock {
            on { navToCreateTimer() }.thenReturn(action)
        }
        val viewModel = subject(mockScreen)
        viewModel.navigateFlow.test {
            viewModel.addInterval()
            Then("Interval store is updated") { verify(intervalStore).update(any()) }
            Then("Timer store is updated") { verify(timerStore).update(any()) }
            Then("Navigation action is fired") { assertEquals(action, expectItem()) }
        }
    }

    Given("Back pressed is called") {
        val action: NavDirections = mock()
        val mockScreen: CreateIntervalTimeScreen = mock {
            on { navToCreateInterval() }.thenReturn(action)
        }
        val viewModel = subject(mockScreen)
        viewModel.navigateFlow.test {
            viewModel.backPressed()
            Then("Navigation action is fired") { assertEquals(action, expectItem()) }
        }
    }
})

private fun CreateIntervalTimeViewModel.fill() {
    (1..6).forEach { addToTime("$it") }
}