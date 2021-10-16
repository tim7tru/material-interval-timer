package com.timmytruong.materialintervaltimer.ui.create.interval

import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateIntervalViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    private val resources: ResourceProvider,
    @IntervalStore private val intervalStore: Store<Interval>,
    private val screen: CreateIntervalScreen
) : BaseViewModel() {

    init {
        startSuspending(ioDispatcher) {
            intervalStore.observe.collectLatest { interval ->
                screen.intervalIconTag.set(resources.tagFromDrawableId(interval.iconId))
                screen.intervalTitle.set(interval.name)
            }
        }
    }

    fun setIntervalIcon(tag: String) = startSuspending(ioDispatcher) {
        val id = resources.drawableIdFromTag(tag = tag)
        intervalStore.update { it.iconId = id ?: -1 }
        screen.intervalIconTag.set(tag)
    }

    fun setEnabled(checked: Boolean) = screen.enableIcon.set(checked)

    fun onTitleChanged(text: CharSequence) = startSuspending(ioDispatcher) {
        intervalStore.update { it.name = text.toString() }
    }

    fun validateTitle(title: String) = startSuspending(ioDispatcher) {
        intervalStore.update { it.name = title }
        navigateWith(action = screen.nextAction())
    }

    fun clearStore() = startSuspending(ioDispatcher) { intervalStore.update { it.clear() } }

    fun fetchInterval() = startSuspending(ioDispatcher) { intervalStore.refresh() }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateIntervalViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideScreen() = CreateIntervalScreen()
}