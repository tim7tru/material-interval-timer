package com.timmytruong.materialintervaltimer.ui.create.interval

import android.content.Context
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.utils.getDrawableIdFromTag
import com.timmytruong.materialintervaltimer.utils.getTagFromDrawableId
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@HiltViewModel
class CreateIntervalViewModel @Inject constructor(
    @ApplicationContext private val ctx: Context,
    @IntervalStore private val intervalStore: Store<Interval>,
    private val screen: CreateIntervalScreen,
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    init {
        startSuspending(ioDispatcher) {
            intervalStore.observe.collectLatest {
                screen.intervalIconTag.set(getTagFromDrawableId(ctx, it.interval_icon_id))
                screen.intervalTitle.set(it.interval_name)
            }
        }
    }

    fun setIntervalIcon(tag: String) = startSuspending(ioDispatcher) {
        val id = getDrawableIdFromTag(context = ctx, tag = tag)
        intervalStore.update { it.interval_icon_id = id }
        screen.intervalIconTag.set(getTagFromDrawableId(context = ctx, id = id))
    }

    fun setEnabled(checked: Boolean) = screen.enableIcon.set(checked)

    fun onTitleChanged(text: CharSequence) = startSuspending(ioDispatcher) {
        intervalStore.update { it.interval_name = text.toString() }
    }

    fun validateTitle(title: String) = startSuspending(ioDispatcher) {
        intervalStore.update { it.interval_name = title }
        navigateWith(action = screen.nextAction())
    }

    fun clearStore() = startSuspending(ioDispatcher) { intervalStore.update { it.clear() } }

    fun fetchInterval() = startSuspending(ioDispatcher) { intervalStore.forceEmit() }
}

@InstallIn(ActivityRetainedComponent::class)
@Module
class CreateIntervalViewModelModule {

    @ActivityRetainedScoped
    @Provides
    fun provideScreen() = CreateIntervalScreen()
}