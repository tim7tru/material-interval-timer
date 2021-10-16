package com.timmytruong.materialintervaltimer.ui.list

import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentRecentsBinding
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.VerticalTimerListAdapter
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

    override val name: String = this.name()

    override val layoutId: Int = R.layout.fragment_recents

    override val viewModel: TimerListViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimers()
        startSuspending {
            screen.timers.collectLatest {
                screen.isEmpty.set(it.isEmpty())
                verticalTimerListAdapter.addList(it)
            }
        }
    }

    override fun bindView() {
        binding?.fragmentRecentsRecycler?.adapter = verticalTimerListAdapter
    }

    override fun onDestroyView() {
        binding?.fragmentRecentsRecycler?.adapter = null
        super.onDestroyView()
    }

}