package com.timmytruong.materialintervaltimer.ui.create.timer

import android.content.Context
import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.DISMISS_EVENT
import com.timmytruong.materialintervaltimer.data.TimerRepository
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.*
import com.timmytruong.materialintervaltimer.ui.create.timer.views.IntervalSoundsBottomSheetScreen
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.assertFalse
import org.mockito.kotlin.*
import java.lang.ref.WeakReference
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateTimerViewModelTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val ctx: WeakReference<Context> = mock()
    val timerStore: Store<Timer> = mock()
    val mainDispatcher = TestCoroutineDispatcher()
    val backgroundDispatcher = TestCoroutineDispatcher()
    val timerRepository: TimerRepository = mock()
    val screen = CreateTimerScreen()
    val bottomSheet = IntervalSoundsBottomSheetScreen()
    val sounds = listOf(
        IntervalSound(-1, "None", true),
        IntervalSound(R.raw.beep, "Beep", false),
        IntervalSound(R.raw.another_beep, "Another beep", false),
        IntervalSound(R.raw.censor, "Censor", false),
        IntervalSound(R.raw.ding, "Ding", false),
        IntervalSound(R.raw.elevator, "Elevator", false),
        IntervalSound(R.raw.error, "Error", false),
        IntervalSound(R.raw.robot, "Robot", false),
        IntervalSound(R.raw.synth, "Synth", false),
    )

    fun create(timerScreen: CreateTimerScreen = screen) = CreateTimerViewModel(
        ctx = ctx,
        timerStore = timerStore,
        mainDispatcher = mainDispatcher,
        ioDispatcher = backgroundDispatcher,
        screen = timerScreen,
        soundsBottomSheet = bottomSheet,
        timerLocalDataSource = timerRepository,
        sounds = sounds
    )

    stub { on { timerStore.observe }.doReturn(emptyFlow()) }

    Given("store is updated with timer") {
        stub { on { timerStore.observe }.doReturn(flowOf(TIMER)) }
        create()
        Then("timer count is set") {
            assert(screen.timerIntervalCount.get() == INTERVAL_COUNT)
        }
        Then("title is set") {
            assert(screen.timerTitle.get() == TITLE)
        }
        Then("interval sound name is set") {
            assert(screen.timerSelectedSound.get() == SOUND_NAME)
        }
    }

    Given("clear timer is called") {
        create().clearTimer()
        Then("store is updated") { verify(timerStore).update(any()) }
    }

    Given("timer title is inputted") {
        create().setTimerTitle(TITLE)
        Then("store is updated") { verify(timerStore).update(any()) }
    }

    Given("repeat is triggered") {
        When("repeat is true") {
            create().setRepeat(true)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assert(screen.timerIsRepeated.get()) }
        }
        When("repeat is false") {
            create().setRepeat(false)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertFalse(screen.timerIsRepeated.get()) }
        }
    }

    Given("favourite is triggered") {
        When("favourite is true") {
            create().setFavourite(true)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assert(screen.timerIsSaved.get()) }
        }
        When("favourite is false") {
            create().setRepeat(false)
            Then("store is updated") { verify(timerStore).update(any()) }
            Then("screen is updated") { assertFalse(screen.timerIsSaved.get()) }
        }
    }

    Given("validate timer is called") {
        val navScreen: CreateTimerScreen = mock()
        val action: NavDirections = mock()
        stub { on { navScreen.navToTimer(any()) }.doReturn(action) }
        stub { on { timerStore.get() }.doReturn(TIMER) }
        timerRepository::saveNewTimer.stub { onBlocking { this.invoke(any()) }.doReturn(TIMER_ID.toLong()) }
        val viewModel = create(navScreen)

        viewModel.navigateFlow.test {
            viewModel.validateTimer(TITLE)
            Then("timer is saved to repository") { verify(timerRepository).saveNewTimer(TIMER) }
            Then("navigate event is retrieved") { verify(navScreen).navToTimer(TIMER_ID) }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }

    Given("dismiss bottom sheet is called") {
        val viewModel = create()
        viewModel.eventFlow.test {
            viewModel.dismissSoundsBottomSheet()
            Then("dismiss event is fired") { assert(expectItem() == DISMISS_EVENT) }
        }
    }

    Given("add interval is clicked") {
        val navScreen: CreateTimerScreen = mock()
        val action: NavDirections = mock()
        navScreen::navToAddInterval.stub { on { this.invoke() }.doReturn(action) }
        val viewModel = create(navScreen)
        viewModel.navigateFlow.test {
            viewModel.onGoToAddIntervalClicked()
            Then("navigate event is retrieved") { verify(navScreen).navToAddInterval() }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }

    Given("sounds is clicked") {
        val navScreen: CreateTimerScreen = mock()
        val action: NavDirections = mock()
        navScreen::navToSoundBottomSheet.stub { on { this.invoke() }.doReturn(action) }
        val viewModel = create(navScreen)
        viewModel.navigateFlow.test {
            viewModel.onGoToSoundsBottomSheet()
            Then("navigate event is retrieved") { verify(navScreen).navToSoundBottomSheet() }
            Then("navigation event fired") { assert(expectItem() == action) }
        }
    }
})