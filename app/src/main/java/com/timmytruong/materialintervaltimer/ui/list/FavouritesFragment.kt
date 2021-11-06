package com.timmytruong.materialintervaltimer.ui.list

import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentFavouritesBinding
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.VerticalTimerListAdapter
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesFragment : BaseFragment<TimerListScreen, TimerListViewModel, FragmentFavouritesBinding>() {

    @Inject
    lateinit var verticalTimerListAdapter: VerticalTimerListAdapter

    @Inject
    override lateinit var screen: TimerListScreen

    override val name: String = this.name()

    override val layoutId: Int = R.layout.fragment_favourites

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
        binding?.fragmentFavouritesRecycler?.adapter = verticalTimerListAdapter
    }

    override fun onDestroyView() {
        binding?.fragmentFavouritesRecycler?.adapter = null
        super.onDestroyView()
    }
}