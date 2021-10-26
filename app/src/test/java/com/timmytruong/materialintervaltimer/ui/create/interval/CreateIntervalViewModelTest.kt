package com.timmytruong.materialintervaltimer.ui.create.interval

import androidx.navigation.NavDirections
import app.cash.turbine.test
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.model.INTERVAL
import com.timmytruong.materialintervaltimer.model.INTERVAL_ICON_ID
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.TITLE
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.time.ExperimentalTime

private const val TAG = "tag"
private const val DRAWABLE = 1

@ExperimentalTime
@ExperimentalCoroutinesApi
class CreateIntervalViewModelTest : BehaviorSpec({

    isolationMode = IsolationMode.InstancePerLeaf

    val ioDispatcher = TestCoroutineDispatcher()
    val mainDispatcher = TestCoroutineDispatcher()
    val resources: ResourceProvider = mock()
    val store: Store<Interval> = mock()
    val screen = CreateIntervalScreen()

    fun subject(createIntervalScreen: CreateIntervalScreen = screen) = CreateIntervalViewModel(
        ioDispatcher = ioDispatcher,
        mainDispatcher = mainDispatcher,
        resources = resources,
        intervalStore = store,
        screen = createIntervalScreen
    )

    Given("Observe is called") {
        When("Interval is emitted") {
            whenever(store.observe).thenReturn(flowOf(INTERVAL))
            whenever(resources.tagFromDrawableId(any())).thenReturn(TAG)
            subject().observe()

            Then("Tag is retrieved from resources") {
                verify(resources).tagFromDrawableId(INTERVAL_ICON_ID)
            }
            Then("Screen icon tag is set") {
                assertEquals(TAG, screen.intervalIconTag.get())
            }
            Then("Screen title is set") {
                assertEquals(TITLE, screen.intervalTitle.get())
            }
        }
    }

    Given("Set interval icon is called") {
        whenever(resources.drawableIdFromTag(TAG)).thenReturn(DRAWABLE)
        subject().setIntervalIcon(TAG)
        Then("Interval store is updated") { verify(store).update(any()) }
        Then("Drawable is retrieved from resources") {
            verify(resources).drawableIdFromTag(TAG)
        }
        Then("Screen icon tag is set") {
            assertEquals(TAG, screen.intervalIconTag.get())
        }
    }

    Given("Set enabled is called") {
        When("Enabled is true") {
            subject().setEnabled(true)
            Then("Screen enable icon is set") { assertTrue(screen.enableIcon.get()) }
        }
        When ("Enabled is false") {
            subject().setEnabled(false)
            Then("Screen enable icon is set") { assertFalse(screen.enableIcon.get()) }
        }
    }

    Given("Title has changed") {
        subject().onTitleChanged(TITLE)
        Then("Interval store is updated") {
            verify(store).update(any())
        }
    }

    Given("Validate title is called") {
        val action: NavDirections = mock()
        val mockScreen: CreateIntervalScreen = mock { on { nextAction() }.thenReturn(action) }
        val subject = subject(mockScreen)
        subject.navigateFlow.test {
            subject.validateTitle(TITLE)
            Then("Interval store is updated") { verify(store).update(any()) }
            Then("Navigation action is fired") { assertEquals(action, expectItem()) }
        }
    }

    Given("Clear store is called") {
        subject().clearStore()
        Then("Interval store is updated") {
            verify(store).update(any())
        }
    }

    Given("Fetch interval is called") {
        subject().fetchInterval()
        Then("Store is refreshed") {
            verify(store).refresh()
        }
    }
})