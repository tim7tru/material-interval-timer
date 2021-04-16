package com.timmytruong.materialintervaltimer.ui.list

import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentFavouritesBinding
import com.timmytruong.materialintervaltimer.ui.reusable.VerticalTimerListAdapter
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

    override fun bindView() {
        binding?.fragmentFavouritesRecycler?.adapter = verticalTimerListAdapter
        startSuspending {
            screen.timers.collectLatest {
                if (it.isEmpty()) screen.isEmpty.set(true)
                verticalTimerListAdapter.addList(it)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindView()
        viewModel.fetchTimers()
    }
}