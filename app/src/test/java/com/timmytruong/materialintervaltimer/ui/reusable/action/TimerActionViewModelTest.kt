package com.timmytruong.materialintervaltimer.ui.reusable.action

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.model.TIMER
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@ExperimentalTime
class TimerActionViewModelTest : BehaviorSpec({

    val dispatcher = TestCoroutineDispatcher()
    val navAction = mock<NavDirections>()
    val timerRepository = mock<TimerRepository>()
    val directions = mock<TimerActionDirections> { on { toTimer(any()) } doReturn navAction }

    val subject = TimerActionViewModel(
        ioDispatcher = dispatcher,
        mainDispatcher = dispatcher,
        timerRepository = timerRepository,
        directions = directions
    )

    Given("fetch timer is called") {
        When("timer is favorited") {
            subject.favorite.test {
                whenever(timerRepository.getTimerById(any())).thenReturn(TIMER.apply { isFavorited = true })

                expectItem() shouldBe false

                subject.fetchTimer(TIMER.id)

                Then("the timer is fetched from the repository") {
                    verify(timerRepository).getTimerById(TIMER.id)
                }
                Then("favorited is emitted") {
                    expectItem() shouldBe true
                }
            }
        }

        When("timer is not favorited") {
            subject.favorite.test {
                whenever(timerRepository.getTimerById(any())).thenReturn(TIMER)

                subject.fetchTimer(TIMER.id)

                Then("the timer is fetched from the repository") {
                    verify(timerRepository).getTimerById(TIMER.id)
                }
                Then("favorited is emitted") {
                    expectItem() shouldBe false
                }
            }
        }
    }

    Given("on favorited is clicked") {
        When("timer is not favorited") {
            subject.eventFlow.test {
                whenever(timerRepository.getTimerById(any())).thenReturn(TIMER)
                subject.fetchTimer(TIMER.id)

                Then("the timer is fetched from the repository") {
                    verify(timerRepository).getTimerById(TIMER.id)
                }

                subject.onFavoriteClicked()
                Then("timer repository is called") {
                    verify(timerRepository).setFavorite(TIMER.id, favorite = true)
                }
                Then("events are fired") {
                    with(expectItem()) {
                        this.shouldBeInstanceOf<Event.ToastMessage>()
                        this.message shouldBe R.string.favorited
                    }
                    expectItem().shouldBeInstanceOf<Event.BottomSheet.Dismiss>()

                }
            }
        }

        When("timer is favorited") {
            subject.eventFlow.test {
                whenever(timerRepository.getTimerById(any())).thenReturn(TIMER.apply { isFavorited = true })
                subject.fetchTimer(TIMER.id)

                Then("the timer is fetched from the repository") {
                    verify(timerRepository).getTimerById(TIMER.id)
                }

                subject.onFavoriteClicked()

                Then("timer repository is called") {
                    verify(timerRepository).setFavorite(TIMER.id, favorite = false)
                }
                Then("events are fired") {
                    with(expectItem()) {
                        this.shouldBeInstanceOf<Event.ToastMessage>()
                        this.message shouldBe R.string.unfavorited
                    }
                    expectItem().shouldBeInstanceOf<Event.BottomSheet.Dismiss>()
                }
            }
        }
    }

    Given("on delete is clicked") {
        subject.eventFlow.test {
            whenever(timerRepository.getTimerById(any())).thenReturn(TIMER.apply { isFavorited = true })
            subject.fetchTimer(TIMER.id)

            Then("the timer is fetched from the repository") {
                verify(timerRepository).getTimerById(TIMER.id)
            }

            subject.onDeleteClicked()

            Then("timer is deleted from repository") {
                verify(timerRepository).deleteTimer(TIMER.id)
            }
            Then("events are fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.ToastMessage>()
                    this.message shouldBe R.string.deleted
                }
                expectItem().shouldBeInstanceOf<Event.BottomSheet.Dismiss>()
            }
        }
    }

    Given("on start is clicked") {
        subject.eventFlow.test {
            whenever(timerRepository.getTimerById(any())).thenReturn(TIMER.apply { isFavorited = true })
            subject.fetchTimer(TIMER.id)

            Then("the timer is fetched from the repository") {
                verify(timerRepository).getTimerById(TIMER.id)
            }

            subject.onStartClicked()

            Then("directions are fetched") {
                verify(directions).toTimer(TIMER.id)
            }

            Then("navigation event is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }
})