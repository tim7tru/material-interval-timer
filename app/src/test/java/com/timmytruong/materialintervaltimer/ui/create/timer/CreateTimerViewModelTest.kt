package com.timmytruong.materialintervaltimer.ui.create.timer

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.data.TimerRepository
import com.timmytruong.data.local.Store
import com.timmytruong.data.model.Timer
import com.timmytruong.materialintervaltimer.data.model.*
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateTimerViewModelTest : BehaviorSpec({

    val navAction: NavDirections = mock()
    val date: DateProvider = mock()
    val timerStore: Store<Timer> = mock {
        on { observe }.thenReturn(emptyFlow())
        on { get() }.thenReturn(TIMER)
    }
    val timerRepository: TimerRepository = mock()
    val directions: CreateTimerDirections = mock {
        on { toCreateInterval() } doReturn navAction
        on { toSounds(any()) } doReturn navAction
        on { toTimer(any()) } doReturn navAction
    }

    val subject = CreateTimerViewModel(
        ioDispatcher = TestCoroutineDispatcher(),
        mainDispatcher = TestCoroutineDispatcher(),
        timerStore = timerStore,
        timerLocalDataSource = timerRepository,
        date = date,
        directions = directions
    )

    Given("fetch current timer is called") {
        When("No timers are emitted from store") {
            And("clear store is true") {
                Then("empty intervals are emitted") {
                    subject.intervals.test {
                        subject.fetchCurrentTimer(true)
                        val initial = expectItem()
                        initial.size shouldBe 0
                    }
                }
                Then("initial timer title is emitted") {
                    subject.title.test {
                        subject.fetchCurrentTimer(true)
                        expectItem() shouldBe ""
                    }
                }
                Then("none is emitted") {
                    subject.sound.test {
                        subject.fetchCurrentTimer(true)
                        expectItem() shouldBe "None"
                    }
                }
                Then("timer store is updated") {
                    subject.fetchCurrentTimer(clearTimer = true)
                    verify(timerStore).update(any())
                }
            }

            And("clear store is false") {
                Then("timer store is refreshed") {
                    subject.fetchCurrentTimer(clearTimer = false)
                    verify(timerStore).refresh()
                }
            }
        }

        When("Timer is emitted from store") {
            whenever(timerStore.observe).thenReturn(flowOf(TIMER))
            Then("initial and new intervals are emitted") {
                subject.intervals.test {
                    subject.fetchCurrentTimer(true)
                    val initial = expectItem()
                    initial.size shouldBe 0
                    val item = expectItem()
                    item.size shouldBe 1

                    with(item.first()) {
                        title shouldBe TITLE
                        icon shouldBe 1
                        time shouldBe TOTAL_TIME
                        hasHeaders shouldBe false
                    }
                }
            }
            Then("initial and new timer title is emitted") {
                subject.title.test {
                    subject.fetchCurrentTimer(true)
                    expectItem() shouldBe ""
                    expectItem() shouldBe TITLE
                }
            }
            Then("initial and new sound is emitted") {
                subject.sound.test {
                    subject.fetchCurrentTimer(true)
                    expectItem() shouldBe "None"
                    expectItem() shouldBe SOUND_NAME
                }
            }
        }
    }

    Given("timer title is inputted") {
        subject.setTimerTitle(TITLE)
        Then("timer store is updated") {
            verify(timerStore).update(any())
        }
    }

    Given("repeat is triggered") {
        When("repeat is true") {
            subject.shouldRepeat.test {
                subject.setRepeat(true)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("initial value is confirmed and new value is emitted") {
                    expectItem() shouldBe false
                    expectItem() shouldBe true
                }
            }
        }
        When("repeat is false") {
            subject.shouldRepeat.test {
                subject.setRepeat(false)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("value is emitted") { expectItem() shouldBe false }
            }
        }
    }

    Given("favorite is triggered") {
        When("favorite is true") {
            subject.isSaved.test {
                subject.setFavorite(true)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("initial value is confirmed and new value is emitted") {
                    expectItem() shouldBe false
                    expectItem() shouldBe true
                }
            }
        }
        When("favourite is false") {
            subject.isSaved.test {
                subject.setFavorite(checked = false)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("screen is updated") { expectItem() shouldBe false }
            }
        }
    }

    Given("validate timer is called") {
        whenever(timerRepository.saveNewTimer(any())).thenReturn(TIMER_ID.toLong())
        subject.eventFlow.test {
            subject.validateTimer(TITLE)
            Then("date is fetched") { verify(date).getCurrentDate() }
            Then("timer is updated in store") { verify(timerStore).update(any()) }
            Then("timer is saved to repository") { verify(timerRepository).saveNewTimer(TIMER) }
            Then("navigate event is retrieved") { verify(directions).toTimer(TIMER_ID) }
            Then("timer is fetched from store") { verify(timerStore).get() }
            Then("navigation event fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("add interval is clicked") {
        subject.eventFlow.test {
            subject.onGoToAddIntervalClicked()
            Then("navigate event is retrieved") { verify(directions).toCreateInterval() }
            Then("navigation event fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("sounds is clicked") {
        subject.eventFlow.test {
            subject.onGoToSoundsBottomSheet()
            Then("navigate event is retrieved") { verify(directions).toSounds(SOUND_ID) }
            Then("navigation event fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }
})