package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.data.model.TIMER
import com.timmytruong.materialintervaltimer.data.model.Timer
import com.timmytruong.materialintervaltimer.data.model.sounds
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.time.ExperimentalTime
import io.kotest.matchers.types.shouldBeInstanceOf

@ExperimentalTime
@ExperimentalCoroutinesApi
class IntervalSoundsViewModelTest : BehaviorSpec({

    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val timerStore: Store<Timer> = mock {
        on { observe }.thenReturn(emptyFlow())
        on { get() }.thenReturn(TIMER)
    }
    val sounds = sounds()

    val subject = IntervalSoundsViewModel(
        ioDispatcher = ioDispatcher,
        mainDispatcher = mainDispatcher,
        timerStore = timerStore,
        sounds = sounds
    )

    Given("Fetch sounds is called") {
        subject.soundsFlow.test {
            subject.fetchSounds(-1)
            val list = expectItem()

            Then("Sound items list is emitted") {
                assertEquals(sounds.size, list.size)
            }
            When("Sound item is clicked") {
                subject.eventFlow.test {
                    list.first().clicks.invoke(0)
                    val event = expectItem()
                    Then("Event is play sound") {
                        event.shouldBeInstanceOf<Event.PlaySound>()
                    }
                    Then("Timer store is updated") {
                        verify(timerStore).update(any())
                    }
                    Then("Play sound is fired") {
                        (event as Event.PlaySound).id shouldBe 1
                    }
                }
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    Given("The bottom sheet is dismissed") {
        subject.eventFlow.test {
            subject.dismissSoundsBottomSheet()
            Then("Dismiss event is fired") {
                expectItem() shouldBe Event.BottomSheet.Dismiss
            }
        }
    }
})