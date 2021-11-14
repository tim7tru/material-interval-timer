package com.timmytruong.materialintervaltimer.ui.create.interval

import com.timmytruong.materialintervaltimer.data.local.Store
import com.timmytruong.materialintervaltimer.di.BackgroundDispatcher
import com.timmytruong.materialintervaltimer.di.IntervalStore
import com.timmytruong.materialintervaltimer.di.MainDispatcher
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.base.BaseViewModel
import com.timmytruong.materialintervaltimer.ui.create.interval.grid.IconGridItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateIntervalViewModel @Inject constructor(
    @BackgroundDispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher override val mainDispatcher: CoroutineDispatcher,
    @IntervalStore private val intervalStore: Store<Interval>,
    private val directions: CreateIntervalDirections,
    private val iconIds: List<Int>
) : BaseViewModel() {

    private val iconItems: List<IconGridItem> = iconIds.map { it.toListItem() }

    private val _enableIcon = MutableSharedFlow<Boolean>(replay = 1)
    val enableIcon: Flow<Boolean> = _enableIcon

    private val _icons = MutableSharedFlow<List<IconGridItem>>(replay = 1)
    val icons: Flow<List<IconGridItem>> = _icons

    private val _title = MutableSharedFlow<String>(replay = 1)
    val title: Flow<String> = _title

    private fun setIntervalIcon(position: Int) = startSuspending(ioDispatcher) {
        setIconSelected {
            if (it == position) {
                intervalStore.update { interval -> interval.iconId = iconIds[position] }
                true
            } else {
                false
            }
        }
    }

    fun fetchInterval(clearStore: Boolean) = startSuspending(ioDispatcher) { scope ->
        intervalStore.observe.onEach { _title.emit(it.name) }.launchIn(scope)

        when (clearStore) {
            true -> intervalStore.update { it.clear() }
            false -> intervalStore.refresh()
        }
    }

    fun onEnabledToggled(checked: Boolean) = startSuspending(ioDispatcher) {
        if (checked) _icons.emit(iconItems)
        _enableIcon.emit(checked)
    }

    fun onTitleChanged(text: String) = startSuspending(ioDispatcher) {
        intervalStore.update { it.name = text }
    }

    fun onNextClicked() = startSuspending(ioDispatcher) {
        navigateWith(action = directions.navToTime())
    }

    private suspend fun setIconSelected(predicate: suspend (Int) -> Boolean) {
        iconItems.forEachIndexed { idx, icon -> icon.isSelected = predicate.invoke(idx) }
        _icons.emit(iconItems)
    }

    private fun Int.toListItem() = IconGridItem(
        id = this,
        isSelected = false,
        clicks = { setIntervalIcon(it) }
    )
}