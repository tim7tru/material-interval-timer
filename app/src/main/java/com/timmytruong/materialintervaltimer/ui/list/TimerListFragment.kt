package com.timmytruong.materialintervaltimer.ui.list

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerListBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.VerticalTimerAdapter
import com.timmytruong.materialintervaltimer.utils.hide
import com.timmytruong.materialintervaltimer.utils.name
import com.timmytruong.materialintervaltimer.utils.set
import com.timmytruong.materialintervaltimer.utils.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

@AndroidEntryPoint
class TimerListFragment : BaseFragment<TimerListScreen, TimerListViewModel, FragmentTimerListBinding>() {

    @Inject
    lateinit var verticalTimerAdapter: VerticalTimerAdapter

    @Inject
    override lateinit var screen: TimerListScreen

    override val name: String = this.name()

    override val layoutId: Int = R.layout.fragment_timer_list

    override val viewModel: TimerListViewModel by viewModels()

    override val hasBackPress: Boolean = false

    private val args: TimerListFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimers(args.type)
        startSuspending {
            screen.timers.collect {
                checkEmptyState(it.isEmpty())
                verticalTimerAdapter.addList(it)
            }
        }
    }

    override fun bindView() {
        binding?.recycler?.adapter = verticalTimerAdapter
        when (args.type) {
            TimerType.RECENTS -> binding?.header?.set(R.string.recentTimers)
            TimerType.FAVOURITES -> binding?.header?.set(R.string.favouriteTimers)
        }
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
        super.onDestroyView()
    }

    private fun checkEmptyState(isEmpty: Boolean) {
        if (isEmpty) binding?.header?.hide() else binding?.header?.show()
        if (isEmpty) binding?.recycler?.hide() else binding?.recycler?.show()
        if (isEmpty) binding?.emptyState?.show() else binding?.emptyState?.hide()
    }
}

enum class TimerType {
    RECENTS,
    FAVOURITES
}

data class TimerListScreen(
    var timers: Flow<@JvmSuppressWildcards List<TimerItem>> = emptyFlow()
) : BaseScreen() {

    fun navToBottomSheet(id: Int, favourited: Boolean) =
        TimerListFragmentDirections.toActionBottomSheet(timerId = id, favourited = favourited)
}