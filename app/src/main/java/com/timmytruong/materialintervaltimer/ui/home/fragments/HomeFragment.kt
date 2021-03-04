package com.timmytruong.materialintervaltimer.ui.home.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.ui.home.adapters.HorizontalTimerItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    @Inject
    lateinit var recentsAdapter: HorizontalTimerItemAdapter

    @Inject
    lateinit var favouritesAdapter: HorizontalTimerItemAdapter

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private val favouriteTimersObserver = Observer<List<Timer>> {
        binding.isFavouritesEmpty = it.isEmpty()
        favouritesAdapter.addList(it)
    }

    private val recentTimersObserver = Observer<List<Timer>> {
        binding.isRecentsEmpty = it.isEmpty()
        recentsAdapter.addList(it)
    }

    override val layoutId: Int = R.layout.fragment_home

    override val baseViewModel: BaseViewModel
        get() = homeViewModel

    override val eventHandler: (Pair<String, Any>) -> Unit = {
        when (it.first) {
            NAVIGATE -> navigate(action = it.second as NavDirections)
        }
    }
    override fun bindView() {
        binding.viewModel = homeViewModel
        binding.fragmentHomeRecentFrag.horizontalRecycler.adapter = recentsAdapter
        binding.fragmentHomeFavouritesFrag.horizontalRecycler.adapter = favouritesAdapter
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        homeViewModel.favouriteTimers.observe(viewLifecycleOwner, favouriteTimersObserver)
        homeViewModel.recentTimers.observe(viewLifecycleOwner, recentTimersObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        homeViewModel.getFavouriteTimers()
        homeViewModel.getRecentTimers()
    }
}