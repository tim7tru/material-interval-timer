package com.timmytruong.materialintervaltimer.ui.create.interval

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.data.model.INTERVAL
import com.timmytruong.materialintervaltimer.data.model.Interval
import com.timmytruong.materialintervaltimer.data.model.TITLE
import com.timmytruong.materialintervaltimer.assertThrows
import com.timmytruong.materialintervaltimer.utils.Event
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.mockito.kotlin.*
import kotlin.time.ExperimentalTime
import io.kotest.matchers.types.shouldBeInstanceOf

private const val DRAWABLE = 1

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateIntervalViewModelTest : BehaviorSpec({

    val navAction: NavDirections = mock()
    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val store: Store<Interval> = mock {
        on { observe } doReturn emptyFlow()
        on { get() } doReturn INTERVAL
    }
    val directions: CreateIntervalDirections = mock {
        on { navToTime() } doReturn navAction
    }
    val iconIds = listOf(DRAWABLE, DRAWABLE, DRAWABLE, DRAWABLE)

    val subject = CreateIntervalViewModel(
        ioDispatcher = ioDispatcher,
        mainDispatcher = mainDispatcher,
        intervalStore = store,
        directions = directions,
        iconIds = iconIds
    )

    Given("fetch interval is called") {
        When("clear stores is true") {
            And("interval is emitted") {
                whenever(store.observe).thenReturn(flowOf(INTERVAL))
                subject.title.test {
                    subject.fetchInterval(true)
                    Then("title is emitted") { expectItem() shouldBe TITLE }
                    Then("store is updated") { verify(store).update(any()) }
                    cancelAndConsumeRemainingEvents()
                }
            }

            And("no interval is emitted") {
                subject.title.test {
                    subject.fetchInterval(true)
                    Then("expect item times out") {
                        assertThrows<TimeoutCancellationException> { expectItem() }
                    }
                    Then("store is updated") { verify(store).update(any()) }
                }
            }
        }

        When("clear stores is false") {
            subject.fetchInterval(false)
            Then("store is refreshed") {
                verify(store).refresh()
            }
        }
    }

    Given("icons are initialized") {
        subject.icons.test {
            subject.onEnabledToggled(true)
            val icons = expectItem()

            Then("icons size matches icon ids size") { icons.size shouldBe 4 }
            Then("ensure icons are transformed correctly") {
                icons.forEach {
                    it.id shouldBe DRAWABLE
                    it.isSelected shouldBe false
                }
            }
            Then("no interaction with the store") { verifyNoInteractions(store) }

            When("icon is clicked") {
                icons.first().clicks.invoke(0)
                Then("store is updated") { verify(store).update(any()) }

                val newIcons = expectItem()
                Then("icons are transformed and emitted again") {
                    newIcons.forEachIndexed { idx, icon ->
                        icon.id shouldBe DRAWABLE
                        icon.isSelected shouldBe (idx == 0)
                    }
                }
                Then("ensure first icon is selected and no others") {
                    newIcons.forEach {
                        it.id shouldBe DRAWABLE
                        if (it == newIcons.first()) it.isSelected shouldBe true
                        else it.isSelected shouldBe false
                    }
                }
            }
        }
    }

    Given("on enabled togged is called") {
        When("enabled is true") {
            Then("icons are emitted") {
                subject.icons.test {
                    subject.onEnabledToggled(true)
                    expectItem().size shouldBe 4
                }
            }
            Then("enabled is emitted") {
                subject.enableIcon.test {
                    subject.onEnabledToggled(true)
                    expectItem() shouldBe true
                }
            }
        }

        When("enabled is false") {
            Then("no icons are emitted") {
                subject.icons.test {
                    subject.onEnabledToggled(false)
                    assertThrows<TimeoutCancellationException> { expectItem() }
                }
            }
            Then("enabled is emitted") {
                subject.enableIcon.test {
                    subject.onEnabledToggled(false)
                    expectItem() shouldBe false
                }
            }
        }
    }

    Given("Title has changed") {
        subject.onTitleChanged(TITLE)
        Then("Interval store is updated") {
            verify(store).update(any())
        }
    }

    Given("next is clicked") {
        subject.eventFlow.test {
            subject.onNextClicked()
            Then("navigation action is retrieved") { verify(directions).navToTime() }
            Then("action is emitted") {
                with(expectItem()) {
                    this.shouldBeInstanceOf<Event.Navigate>()
                    this.directions shouldBe navAction
                }
            }
        }
    }
})