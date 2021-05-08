package com.timmytruong.materialintervaltimer.ui.home

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.model.*
import com.timmytruong.materialintervaltimer.ui.reusable.action.TimerActionBottomSheetScreen
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class HomeViewModelTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val resources: ResourceProvider = mock()
    val mainDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    val backgroundDispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
    val timerRepository: TimerRepository = mock()

    val screen = HomeScreen()
    val bottomSheet = TimerActionBottomSheetScreen()

    fun viewModel(homeScreen: HomeScreen = screen): HomeViewModel {
        return HomeViewModel(
            mainDispatcher,
            backgroundDispatcher,
            resources,
            timerRepository,
            homeScreen,
            bottomSheet
        )
    }

    fun stubRecentTimers() {
        stub { on { timerRepository.getRecentTimers() }.doReturn(flowOf(listOf(TIMER))) }
    }

    fun stubFavouriteTimers() {
        stub { on { timerRepository.getFavouritedTimers() }.doReturn(flowOf(listOf(TIMER))) }
    }

    Given("fetch recent timers is called") {
        val viewModel = viewModel()

        viewModel.recents.test {
            stubRecentTimers()
            viewModel.fetchRecentTimers()
            val expected = expectItem()
            Then("timers is fetched from repo") {
                verify(timerRepository).getRecentTimers()
            }
            Then("time is assigned") {
                verify(resources).string(
                    R.string.timerTimeFormat,
                    ONE_SEC_HOUR,
                    ONE_SEC_MIN,
                    ONE_SEC_SEC
                )
            }
            Then("interval count is assigned") {
                verify(resources).string(R.string.number_of_intervals_format, 1.toString())
            }
            Then("expected item has size of one ") {
                assert(expected.size == 1)
            }
            Then("expected item title is the assigned title") {
                assert(expected.firstOrNull()?.title?.get() == TITLE)
            }
            Then("expected item id is the assigned id") {
                assert(expected.firstOrNull()?.timerId == TIMER_ID)
            }
        }
    }

    Given("recent timer is clicked") {
        val action: NavDirections = mock()
        val mockScreen = mock<HomeScreen>().stub { on { this.navToBottomSheet() }.doReturn(action) }
        val viewModel = viewModel(mockScreen)

        viewModel.recents.test {
            stubRecentTimers()
            viewModel.fetchRecentTimers()
            val item = expectItem().first()
            item.clicks.invoke(item)
            Then("bottom sheet id is set") { assert(bottomSheet.timerId.get() == TIMER_ID) }
            Then("bottom sheet favourite is set") { assert(!bottomSheet.isFavourite.get()) }
            Then("navigate action is retrieved") { verify(mockScreen).navToBottomSheet() }
        }
    }

    Given("fetch favourite timers is called") {
        val viewModel = viewModel()

        viewModel.favourites.test {
            stubFavouriteTimers()
            viewModel.fetchFavouriteTimers()
            val expected = expectItem()
            Then("timers is fetched from repo") {
                verify(timerRepository).getFavouritedTimers()
            }
            Then("time is assigned") {
                verify(resources).string(
                    R.string.timerTimeFormat,
                    ONE_SEC_HOUR,
                    ONE_SEC_MIN,
                    ONE_SEC_SEC
                )
            }
            Then("interval count is assigned") {
                verify(resources).string(R.string.number_of_intervals_format, 1.toString())
            }
            Then("expected item has size of one ") {
                assert(expected.size == 1)
            }
            Then("expected item title is the assigned title") {
                assert(expected.firstOrNull()?.title?.get() == TITLE)
            }
            Then("expected item id is the assigned id") {
                assert(expected.firstOrNull()?.timerId == TIMER_ID)
            }
        }
    }

    Given("favourite timer is clicked") {
        val action: NavDirections = mock()
        val mockScreen = mock<HomeScreen>().stub { on { this.navToBottomSheet() }.doReturn(action) }
        val viewModel = viewModel(mockScreen)

        viewModel.favourites.test {
            stubFavouriteTimers()
            viewModel.fetchFavouriteTimers()
            val item = expectItem().first()
            item.clicks.invoke(item)
            Then("bottom sheet id is set") { assert(bottomSheet.timerId.get() == TIMER_ID) }
            Then("bottom sheet favourite is set") { assert(!bottomSheet.isFavourite.get()) }
            Then("navigate action is retrieved") { verify(mockScreen).navToBottomSheet() }
        }
    }

    Given("on add clicked") {
        val action: NavDirections = mock()
        val mockScreen = mock<HomeScreen>().stub { on { this.navToCreateTimer() }.doReturn(action) }
        val viewModel = viewModel(mockScreen)
        viewModel.navigateFlow.test {
            viewModel.onAddClicked()
            Then("navigation action is fetched from screen") { verify(mockScreen).navToCreateTimer() }
            Then("navigation action is fired") { assert(expectItem() == action) }
        }
    }
})