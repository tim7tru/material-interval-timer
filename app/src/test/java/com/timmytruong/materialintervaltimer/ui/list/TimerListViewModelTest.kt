package com.timmytruong.materialintervaltimer.ui.list

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class TimerListViewModelTest: BehaviorSpec({

    val navAction: NavDirections = mock()
    val repository: TimerRepository = mock()
    val directions: TimerListDirctions = mock {
        on { navToBottomSheet(any(), any()) } doReturn navAction
    }
    val subject = TimerListViewModel(
        ioDispatcher = TestCoroutineDispatcher(),
        mainDispatcher = TestCoroutineDispatcher(),
        timerRepository = repository,
        directions = directions
    )

    Given("fetch timers is called") {
        When("timer type is recents") {
            And("there are recent timers") {
                whenever(repository.getRecentTimers()).thenReturn(flowOf(listOf(TIMER)))
                subject.timers.test {
                    subject.fetchTimers(TimerType.RECENTS)
                    val list = expectItem()
                    Then("recents are fetched from repository") {
                        verify(repository).getRecentTimers()
                    }
                    Then("list has one item") { list.size shouldBe 1 }
                    Then("timer transformed correctly") {
                        with (list.first()) {
                            id shouldBe TIMER_ID
                            time shouldBe TOTAL_TIME
                            title shouldBe TITLE
                            intervalCount shouldBe 1
                        }
                    }
                }
            }

            And("there are no recent timers") {
                whenever(repository.getRecentTimers()).thenReturn(flowOf(emptyList()))
                subject.timers.test {
                    subject.fetchTimers(TimerType.RECENTS)
                    Then("recents are fetched from repository") {
                        verify(repository).getRecentTimers()
                    }
                    Then("expect item is empty list") {
                        expectItem() shouldBe emptyList()
                    }
                }
            }
        }

        When("timer type is favorites") {
            And("there are favorite timers") {
                whenever(repository.getFavoritedTimers()).thenReturn(flowOf(listOf(TIMER)))
                subject.timers.test {
                    subject.fetchTimers(TimerType.FAVORITES)
                    val list = expectItem()
                    Then("recents are fetched from repository") {
                        verify(repository).getFavoritedTimers()
                    }
                    Then("list has one item") { list.size shouldBe 1 }
                    Then("timer transformed correctly") {
                        with (list.first()) {
                            id shouldBe TIMER_ID
                            time shouldBe TOTAL_TIME
                            title shouldBe TITLE
                            intervalCount shouldBe 1
                        }
                    }
                }
            }

            And("there are no favorite timers") {
                whenever(repository.getFavoritedTimers()).thenReturn(flowOf(emptyList()))
                subject.timers.test {
                    subject.fetchTimers(TimerType.FAVORITES)
                    Then("favorites are fetched from repository") {
                        verify(repository).getFavoritedTimers()
                    }
                    Then("expect item is empty list") {
                        expectItem() shouldBe emptyList()
                    }
                }
            }
        }
    }

    Given("timer is clicked") {
        When("timer is favorite") {
            val timer = timer(isFavorited = true)
            whenever(repository.getFavoritedTimers()).thenReturn(flowOf(listOf(timer)))
            subject.timers.test {
                subject.fetchTimers(TimerType.FAVORITES)
                val item = expectItem().first()
                subject.navigateFlow.test {
                    item.click()
                    Then("navigation action is retrieved") {
                        verify(directions).navToBottomSheet(TIMER_ID, true)
                    }
                    Then("navigation event is fired") { expectItem() shouldBe navAction }
                }
            }
        }
        When("timer is recents") {
            val timer = timer(isFavorited = false)
            whenever(repository.getRecentTimers()).thenReturn(flowOf(listOf(timer)))
            subject.timers.test {
                subject.fetchTimers(TimerType.RECENTS)
                val item = expectItem().first()
                subject.navigateFlow.test {
                    item.click()
                    Then("navigation action is retrieved") {
                        verify(directions).navToBottomSheet(TIMER_ID, false)
                    }
                    Then("navigation event is fired") { expectItem() shouldBe navAction }
                }
            }
        }
    }
})