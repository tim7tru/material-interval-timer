package com.timmytruong.materialintervaltimer.ui.home

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.TIMER_ID
import com.timmytruong.materialintervaltimer.model.timer
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
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

    val resources: ResourceProvider = mock()
    val timerRepository: TimerRepository = mock()
    val screen: HomeScreen = mock()

    fun viewModel(homeScreen: HomeScreen = screen) = HomeViewModel(
        mainDispatcher = TestCoroutineDispatcher(),
        ioDispatcher = TestCoroutineDispatcher(),
        timerRepository = timerRepository,
        screen = homeScreen,
        resources = resources
    )

    Given("recent timer is clicked") {
        val action: NavDirections = mock()
        whenever(screen.navToBottomSheet(any(), any())).thenReturn(action)
        whenever(timerRepository.getRecentTimers()).thenReturn(flowOf(listOf(timer(isFavourited = false))))
        val viewModel = viewModel(screen)

        Then("navigate action is retrieved") {
            viewModel.recents.test {
                viewModel.fetchRecentTimers()
                val item = expectItem()
                item.first().clicks.invoke(0)
                verify(screen).navToBottomSheet(TIMER_ID, false)
            }
        }

        Then("action is emitted to nav flow") {
            viewModel.recents.test {
                viewModel.fetchRecentTimers()
                val item = this@test.expectItem()
                viewModel.navigateFlow.test {
                    item.first().clicks.invoke(0)
                    assertEquals(expectItem(), action)
                }
            }
        }
    }

    Given("favourite timer is clicked") {
        val action: NavDirections = mock()
        whenever(screen.navToBottomSheet(any(), any())).thenReturn(action)
        whenever(timerRepository.getFavouritedTimers()).thenReturn(flowOf(listOf(timer(isFavourited = true))))
        val viewModel = viewModel(screen)

        Then("navigate action is retrieved") {
            viewModel.favourites.test {
                viewModel.fetchFavouriteTimers()
                val item = expectItem().first()
                item.clicks.invoke(0)
                verify(screen).navToBottomSheet(TIMER_ID, true)
            }
        }

        Then("action is emitted to nav flow") {
            viewModel.favourites.test {
                viewModel.fetchFavouriteTimers()
                val item = this@test.expectItem()
                viewModel.navigateFlow.test {
                    item.first().clicks.invoke(0)
                    assertEquals(expectItem(), action)
                }
            }
        }
    }

    Given("on add clicked") {
        val action: NavDirections = mock()
        whenever(screen.navToCreateTimer()).thenReturn(action)
        val viewModel = viewModel(screen)
        viewModel.navigateFlow.test {
            viewModel.onAddClicked()
            Then("navigation action is fetched from screen") { verify(screen).navToCreateTimer() }
            Then("navigation action is fired") { assert(expectItem() == action) }
        }
    }

    Given("on see all favourites clicked") {
        val action: NavDirections = mock()
        whenever(screen.navToFavouriteTimers()).thenReturn(action)
        val viewModel = viewModel(screen)
        viewModel.navigateFlow.test {
            viewModel.onFavouritesSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(screen).navToFavouriteTimers() }
            Then("navigation action is fired") { assert(expectItem() == action) }
        }
    }


    Given("on see all recents clicked") {
        val action: NavDirections = mock()
        whenever(screen.navToRecentTimers()).thenReturn(action)
        val viewModel = viewModel(screen)
        viewModel.navigateFlow.test {
            viewModel.onRecentsSeeAllClicked()
            Then("navigation action is fetched from screen") { verify(screen).navToRecentTimers() }
            Then("navigation action is fired") { assert(expectItem() == action) }
        }
    }
})