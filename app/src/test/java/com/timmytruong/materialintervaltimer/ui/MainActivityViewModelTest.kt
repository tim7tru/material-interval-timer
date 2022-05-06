package com.timmytruong.materialintervaltimer.ui

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.ui.home.HomeDirections
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class MainActivityViewModelTest : BehaviorSpec({

    val navAction: NavDirections = mock()
    val directions: HomeDirections = mock {
        on { toCreateTimer() } doReturn navAction
        on { toTimerList(any()) } doReturn navAction
        on { toTimerList(any())} doReturn navAction
    }
    val subject = MainActivityViewModel(
        mainDispatcher = TestCoroutineDispatcher(),
        directions = directions
    )

    Given("nav to create timer is called") {
        subject.eventFlow.test {
            subject.navToCreateTimer()
            Then("navAction is fetched from directions") {
                verify(directions).toCreateTimer()
            }
            Then("navAction event is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("nav to recents is called") {
        subject.eventFlow.test {
            subject.navToRecents()
            Then("navAction is fetched from directions") {
                verify(directions).toTimerList(TimerType.RECENTS)
            }
            Then("navAction event is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("nav to favorites is called") {
        subject.eventFlow.test {
            subject.navToFavorites()
            Then("navAction is fetched from directions") {
                verify(directions).toTimerList(TimerType.FAVORITES)
            }
            Then("navAction event is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }
})