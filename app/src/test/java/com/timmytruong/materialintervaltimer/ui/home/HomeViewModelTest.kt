package com.timmytruong.materialintervaltimer.ui.home

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.TIMER_ID
import com.timmytruong.materialintervaltimer.model.TITLE
import com.timmytruong.materialintervaltimer.model.TOTAL_TIME
import com.timmytruong.materialintervaltimer.model.timer
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerItem
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class HomeViewModelTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

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
            Then("verify there is only one timer") {
                assertEquals(1, item.size)
            }
            Then("timer is transformed correctly") {
                with (item.first()) {
                    assertEquals(TIMER_ID, id)
                    assertEquals(TOTAL_TIME, time)
                    assertEquals(TITLE, title)
                    assertEquals(1, intervalCount)
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
            Then("verify there is only one timer") {
                assertEquals(1, item.size)
            }
            Then("timer is transformed correctly") {
                with (item.first()) {
                    assertEquals(TIMER_ID, id)
                    assertEquals(TOTAL_TIME, time)
                    assertEquals(TITLE, title)
                    assertEquals(1, intervalCount)
                }
            }
        }
    }

    Given("recent timer is clicked") {
        whenever(timerRepository.getRecentTimers()).thenReturn(flowOf(listOf(timer(isFavorited = false))))
        subject.recents.test {
            subject.fetchRecents()

            val item = expectItem()

            Then("navigation action is retrieved") {
                item.first().clicks.invoke(0)
                verify(directions).toBottomSheet(TIMER_ID, false)
            }

            Then("action is emitted to nav flow") {
                subject.navigateFlow.test {
                    item.first().clicks.invoke(0)
                    assertEquals(expectItem(), navAction)
                }
            }
        }
    }

    Given("favorite timer is clicked") {
        whenever(timerRepository.getFavoritedTimers()).thenReturn(flowOf(listOf(timer(isFavorited = true))))
        subject.favorites.test {
            subject.fetchFavorites()

            val item = expectItem()

            Then("navigation action is retrieved") {
                item.first().clicks.invoke(0)
                verify(directions).toBottomSheet(TIMER_ID, true)
            }

            Then("action is emitted to nav flow") {
                subject.navigateFlow.test {
                    item.first().clicks.invoke(0)
                    assertEquals(expectItem(), navAction)
                }
            }
        }
    }

    Given("on add clicked") {
        subject.navigateFlow.test {
            subject.onAddClicked()
            Then("navigation action is fetched from screen") { verify(directions).toCreateTimer() }
            Then("navigation action is fired") { assertEquals(expectItem(), navAction) }
        }
    }

    Given("on see all favorites clicked") {
        subject.navigateFlow.test {
            subject.onFavoritesSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(directions).toTimerList(TimerType.FAVORITES) }
            Then("navigation action is fired") { assertEquals(expectItem(), navAction) }
        }
    }

    Given("on see all recents clicked") {
        subject.navigateFlow.test {
            subject.onRecentsSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(directions).toTimerList(TimerType.RECENTS) }
            Then("navigation action is fired") { assertEquals(expectItem(), navAction) }
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

            Then("length is trimmed to 7") {
                assertEquals(7, item.size)
            }
        }
    }
})