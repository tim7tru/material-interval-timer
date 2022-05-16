package com.timmytruong.materialintervaltimer.ui.home

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.model.TIMER_ID
import com.timmytruong.materialintervaltimer.data.model.TITLE
import com.timmytruong.materialintervaltimer.data.model.TOTAL_TIME
import com.timmytruong.materialintervaltimer.data.model.timer
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class HomeViewModelTest : BehaviorSpec({

    val navAction: NavDirections = mock()
    val timerRepository: TimerRepository = mock()
    val directions: HomeDirections = mock {
        on { toBottomSheet(any(), any()) } doReturn navAction
        on { toTimerList(any()) } doReturn navAction
        on { toCreateTimer() } doReturn navAction
    }
    val subject = HomeViewModel(
        ioDispatcher = TestCoroutineDispatcher(),
        mainDispatcher = TestCoroutineDispatcher(),
        timerRepository = timerRepository,
        directions = directions
    )

    Given("fetch recents is called") {
        whenever(timerRepository.getRecentTimers()).thenReturn(flowOf(listOf(timer(isFavorited = false))))
        subject.recents.test {
            subject.fetchRecents()

            val item = expectItem()

            Then("Favorites is fetched from repository") {
                verify(timerRepository).getRecentTimers()
            }
            Then("verify there is only one timer") { item.size shouldBe 1 }
            Then("timer is transformed correctly") {
                with (item.first()) {
                    id shouldBe TIMER_ID
                    time shouldBe TOTAL_TIME
                    title shouldBe TITLE
                    intervalCount shouldBe 1
                }
            }
        }
    }

    Given("fetch favorites is called") {
        whenever(timerRepository.getFavoritedTimers()).thenReturn(flowOf(listOf(timer(isFavorited = true))))
        subject.favorites.test {
            subject.fetchFavorites()

            val item = expectItem()

            Then("favorites is fetched from repository") {
                verify(timerRepository).getFavoritedTimers()
            }
            Then("verify there is only one timer") { item.size shouldBe 1 }
            Then("timer is transformed correctly") {
                with (item.first()) {
                    id shouldBe TIMER_ID
                    time shouldBe TOTAL_TIME
                    title shouldBe TITLE
                    intervalCount shouldBe 1
                }
            }
        }
    }

    Given("recent timer is clicked") {
        whenever(timerRepository.getRecentTimers()).thenReturn(flowOf(listOf(timer(isFavorited = false))))
        subject.recents.test {
            subject.fetchRecents()

            val item = expectItem()

            subject.eventFlow.test {
                item.first().click()
                Then("navigation action is retrieved") {
                    verify(directions).toBottomSheet(TIMER_ID, false)
                }
                Then("action is emitted to nav flow") {
                    with(expectItem()) {
                        this.shouldBeInstanceOf<Event.Navigate>()
                        this.directions shouldBe navAction
                    }
                }
            }
        }
    }

    Given("favorite timer is clicked") {
        whenever(timerRepository.getFavoritedTimers()).thenReturn(flowOf(listOf(timer(isFavorited = true))))
        subject.favorites.test {
            subject.fetchFavorites()

            val item = expectItem()

            subject.eventFlow.test {
                item.first().click()
                Then("navigation action is retrieved") {
                    verify(directions).toBottomSheet(TIMER_ID, true)
                }
                Then("action is emitted to nav flow") {
                    with(expectItem()) {
                        this.shouldBeInstanceOf<Event.Navigate>()
                        this.directions shouldBe navAction
                    }
                }
            }
        }
    }

    Given("on add clicked") {
        subject.eventFlow.test {
            subject.onAddClicked()
            Then("navigation action is fetched from screen") { verify(directions).toCreateTimer() }
            Then("navigation action is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("on see all favorites clicked") {
        subject.eventFlow.test {
            subject.onFavoritesSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(directions).toTimerList(TimerType.FAVORITES) }
            Then("navigation action is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("on see all recents clicked") {
        subject.eventFlow.test {
            subject.onRecentsSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(directions).toTimerList(TimerType.RECENTS) }
            Then("navigation action is fired") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }

    Given("more than 7 timers are returned from repository") {
        whenever(timerRepository.getRecentTimers()).thenReturn(
            flowOf(
                listOf(
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                    timer(isFavorited = false),
                )
            )
        )

        subject.recents.test {
            subject.fetchRecents()
            val item = expectItem()

            Then("length is trimmed to 7") { item.size shouldBe 7 }
        }
    }
})