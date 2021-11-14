package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.assertThrows
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.INTERVAL
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.TIMER
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.base.adapter.EmptyClicks
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
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

    Given("Create numbers is called") {
        subject.numbers.test {
            subject.createNumbers()
            val list = expectItem()

            Then("12 items are emitted") {
                list.size shouldBe 12
            }
            Then("numbers 1 to 9 are set") {
                for (num in 0 until 9) {
                    list[num].number shouldBe num + 1
                }
            }
            Then("10th item empty") {
                list[9].number shouldBe null
                list[9].clicks shouldBe EmptyClicks
            }
            Then("11th item is 0") {
                list[10].number shouldBe 0
            }
            Then("12th item is empty") {
                list[11].number shouldBe null
                list[11].clicks shouldBe EmptyClicks
            }
        }
    }

    Given("number item is clicked") {
        subject.numbers.test {
            subject.createNumbers()
            val number = expectItem().first()

            subject.time.test {

                When("number is clicked once") {
                    number.clicks.invoke(-1)
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
                            number.click()
                            time += "1"
                        }
                        expectItem() shouldBe time
                    }
                }
            }
        }
    }

    Given("Remove from time is called") {
        subject.numbers.test {
            subject.createNumbers()
            val number = expectItem().first()

            subject.time.test {
                var time = ""
                expectItem() shouldBe time
                repeat(6) {
                    number.click()
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
    }

    Given("add interval is called") {
        subject.navigateFlow.test {
            subject.addInterval()
            Then("interval store is updated") { verify(intervalStore).update(any()) }
            Then("timer store is updated") { verify(timerStore).update(any()) }
            Then("navigation action is retrieved") { verify(directions).navToCreateTimer() }
            Then("navigation action is fired") { expectItem() shouldBe navAction }
        }
    }

    Given("back pressed is called") {
        subject.navigateFlow.test {
            subject.backPressed()
            Then("navigation action is retrieved") { verify(directions).navToCreateInterval() }
            Then("Navigation action is fired") { expectItem() shouldBe navAction }
        }
    }
})