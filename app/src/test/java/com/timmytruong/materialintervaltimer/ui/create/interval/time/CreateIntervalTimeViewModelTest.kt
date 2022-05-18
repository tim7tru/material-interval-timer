package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.assertThrows
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.data.model.INTERVAL
import com.timmytruong.materialintervaltimer.data.model.Interval
import com.timmytruong.materialintervaltimer.data.model.TIMER
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.adapter.EmptyClicks
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateIntervalTimeViewModelTest : BehaviorSpec({

    val navAction: NavDirections = mock()
    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val timerStore: Store<Timer> = mock {
        on { observe } doReturn emptyFlow()
        on { get() } doReturn TIMER
    }
    val intervalStore: Store<Interval> = mock {
        on { observe } doReturn emptyFlow()
        on { get() } doReturn INTERVAL
    }
    val directions: CreateIntervalTimeDirections = mock {
        on { navToCreateInterval() } doReturn navAction
        on { navToCreateTimer() } doReturn navAction
    }

    val subject = CreateIntervalTimeViewModel(
        ioDispatcher = ioDispatcher,
        mainDispatcher = mainDispatcher,
        timerStore = timerStore,
        intervalStore = intervalStore,
        directions = directions
    )

    Given("number item is clicked") {
        subject.time.test {
            When("number is added one") {
                subject.addToTime(1)
                Then("initial item is empty") { expectItem() shouldBe "" }
                Then("time is 1") {
                    expectItem() shouldBe ""
                    expectItem() shouldBe "1"
                }
            }

            When("number is clicked more than 6 times") {
                var time = ""
                Then("time does not exceed 6") {
                    repeat(7) {
                        expectItem() shouldBe time
                        subject.addToTime(1)
                        time += "1"
                    }
                    expectItem() shouldBe time
                }
            }
        }
    }

    Given("Remove from time is called") {
        subject.time.test {
            var time = ""
            expectItem() shouldBe time
            repeat(6) {
                subject.addToTime(1)
                time += "1"
                expectItem() shouldBe time
            }
            When("remove is called once") {
                subject.removeFromTime()
                Then("one digit is removed from time") { expectItem() shouldBe "11111" }
            }
            When("remove is more than 6 times") {
                Then("time goes to empty") {
                    repeat(6) {
                        subject.removeFromTime()
                        time = time.dropLast(1)
                        expectItem() shouldBe time
                    }
                    subject.removeFromTime()
                    assertThrows<TimeoutCancellationException> { expectItem() }
                }
            }
        }
    }

    Given("add interval is called") {
        subject.eventFlow.test {
            subject.addInterval()
            Then("interval store is updated") { verify(intervalStore).update(any()) }
            Then("timer store is updated") { verify(timerStore).update(any()) }
            Then("navigation action is retrieved") { verify(directions).navToCreateTimer() }
            Then("navigation action is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("back pressed is called") {
        subject.eventFlow.test {
            subject.backPressed()
            Then("navigation action is retrieved") { verify(directions).navToCreateInterval() }
            Then("Navigation action is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }
})