package com.timmytruong.materialintervaltimer.ui.list

import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentRecentsBinding
import com.timmytruong.materialintervaltimer.ui.reusable.VerticalTimerListAdapter
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class RecentsFragment : BaseFragment<TimerListScreen, TimerListViewModel, FragmentRecentsBinding>() {

    @Inject
    lateinit var verticalTimerListAdapter: VerticalTimerListAdapter

    @Inject
    override lateinit var screen: TimerListScreen

    override val layoutId: Int = R.layout.fragment_recents

    override val name: String = name()

    override val viewModel: TimerListViewModel by viewModels()

    override fun bindView() {
        binding?.fragmentRecentsRecycler?.adapter = verticalTimerListAdapter
        startSuspending {
            screen.timers.collectLatest {
                if (it.isEmpty()) screen.isEmpty.set(true)
                verticalTimerListAdapter.addList(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchTimers()
    }
}