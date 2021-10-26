package com.timmytruong.materialintervaltimer.ui.create.timer.views

import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.model.sounds
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class IntervalSoundsViewModelTest : BehaviorSpec({
    isolationMode = IsolationMode.InstancePerLeaf

    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val timerStore: Store<Timer> = mock()
    val sounds = sounds()
    val screen = IntervalSoundsBottomSheetScreen()
    val resources: ResourceProvider = mock()

    fun subject() = IntervalSoundsViewModel(
        ioDispatcher = ioDispatcher,
        mainDispatcher = mainDispatcher,
        timerStore = timerStore,
        sounds = sounds,
        soundsBottomSheet = screen,
        resources = resources
    )

    Given("Fetch sounds is called") {
        val viewModel = subject()
        viewModel.fetchSounds(-1)
        Then("Sound bindings is set") {
            assertEquals(sounds.size, screen.list.size)
        }

        When("Sound item is clicked") {
            viewModel.eventFlow.test {
                screen.list.first().clicks.invoke(0)
                val event = expectItem()
                Then("Event is play sound") { assertTrue(event is Event.PlaySound) }
                Then("Timer store is updated") { verify(timerStore).update(any()) }
                Then("Play sound is fired") {
                    assertEquals(1, (event as Event.PlaySound).id)
                }
            }
        }
    }

    Given("The bottom sheet is dismissed") {
        val viewmodel = subject()
        viewmodel.eventFlow.test {
            viewmodel.dismissSoundsBottomSheet()
            assertEquals(Event.BottomSheet.Dismiss, expectItem())
        }
    }
})