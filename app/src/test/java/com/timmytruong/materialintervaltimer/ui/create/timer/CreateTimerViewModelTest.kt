package com.timmytruong.materialintervaltimer.ui.create.timer

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.*
import com.timmytruong.materialintervaltimer.utils.providers.DateProvider
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
                        assertEquals(0, initial.size)
                    }
                }
                Then("initial timer title is emitted") {
                    subject.title.test {
                        subject.fetchCurrentTimer(true)
                        assertEquals("", expectItem())
                    }
                }
                Then("none is emitted") {
                    subject.sound.test {
                        subject.fetchCurrentTimer(true)
                        assertEquals("None", expectItem())
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
                    assertEquals(0, initial.size)
                    val item = expectItem()
                    assertEquals(1, item.size)
                    with(item.first()) {
                        assertEquals("", title)
                        assertEquals(0, icon)
                        assertEquals(TOTAL_TIME, time)
                        assertFalse(hasHeaders)
                    }
                }
            }
            Then("initial and new timer title is emitted") {
                subject.title.test {
                    subject.fetchCurrentTimer(true)
                    assertEquals("", expectItem())
                    assertEquals(TITLE, expectItem())
                }
            }
            Then("initial and new sound is emitted") {
                subject.sound.test {
                    subject.fetchCurrentTimer(true)
                    assertEquals("None", expectItem())
                    assertEquals(SOUND_NAME, expectItem())
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
                    assertFalse(expectItem())
                    assertTrue(expectItem())
                }
            }
        }
        When("repeat is false") {
            subject.shouldRepeat.test {
                subject.setRepeat(false)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("value is emitted") { assertFalse(expectItem()) }
            }
        }
    }

    Given("favorite is triggered") {
        When("favorite is true") {
            subject.isSaved.test {
                subject.setFavorite(true)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("initial value is confirmed and new value is emitted") {
                    assertFalse(expectItem())
                    assertTrue(expectItem())
                }
            }
        }
        When("favourite is false") {
            subject.isSaved.test {
                subject.setFavorite(checked = false)
                Then("store is updated") { verify(timerStore).update(any()) }
                Then("screen is updated") { assertFalse(expectItem()) }
            }
        }
    }

    Given("validate timer is called") {
        whenever(timerRepository.saveNewTimer(any())).thenReturn(TIMER_ID.toLong())
        subject.navigateFlow.test {
            subject.validateTimer(TITLE)
            Then("date is fetched") { verify(date).getCurrentDate() }
            Then("timer is updated in store") { verify(timerStore).update(any()) }
            Then("timer is saved to repository") { verify(timerRepository).saveNewTimer(TIMER) }
            Then("navigate event is retrieved") { verify(directions).toTimer(TIMER_ID) }
            Then("timer is fetched from store") { verify(timerStore).get() }
            Then("navigation event fired") { assertEquals(expectItem(), navAction) }
        }
    }

    Given("add interval is clicked") {
        subject.navigateFlow.test {
            subject.onGoToAddIntervalClicked()
            Then("navigate event is retrieved") { verify(directions).toCreateInterval() }
            Then("navigation event fired") { assertEquals(expectItem(), navAction) }
        }
    }

    Given("sounds is clicked") {
        subject.navigateFlow.test {
            subject.onGoToSoundsBottomSheet()
            Then("navigate event is retrieved") { verify(directions).toSounds(SOUND_ID) }
            Then("navigation event fired") { assertEquals(expectItem(), navAction) }
        }
    }
})