package com.timmytruong.materialintervaltimer.ui.home.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeClicks
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.ui.home.TIMER
import com.timmytruong.materialintervaltimer.ui.home.adapters.HorizontalTimerItemAdapter
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), HomeClicks.Main {

    @Inject
    @Recents
    lateinit var recentsAdapter: HorizontalTimerItemAdapter

    @Inject
    @Favourites
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

    override val layoutId: Int
        get() = R.layout.fragment_home

    override val baseViewModel: BaseViewModel
        get() = homeViewModel

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            handleEvent(event) {
                when (it.first) {
                    TIMER -> handleTimerEvent(timer = it.second as Timer)
                }
            }
        }

    override fun bindView() {
        binding.onClick = this
        binding.fragmentHomeRecentFrag.horizontalRecycler.adapter = recentsAdapter
        binding.fragmentHomeFavouritesFrag.horizontalRecycler.adapter = favouritesAdapter
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        homeViewModel.favouriteTimers.observe(viewLifecycleOwner, favouriteTimersObserver)
        homeViewModel.recentTimers.observe(viewLifecycleOwner, recentTimersObserver)
    }

    override fun onAddClicked(view: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment()
        action.clearViewModel = true
        findNavController().navigate(action)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        homeViewModel.getFavouriteTimers()
        homeViewModel.getRecentTimers()
    }

    private fun handleTimerEvent(timer: Timer) {
        val action = HomeFragmentDirections.actionHomeFragmentToTimerActionBottomSheet()
        action.timerId = timer.id
        action.isFavourited = timer.timer_saved
        findNavController().navigate(action)
    }
}

@InstallIn(FragmentComponent::class)
@Module
class HomeModule {

    @FragmentScoped
    @Recents
    @Provides
    fun provideRecentsAdapter(homeViewModel: HomeViewModel) =
        HorizontalTimerItemAdapter(homeViewModel)

    @FragmentScoped
    @Favourites
    @Provides
    fun provideFavouritesAdapter(homeViewModel: HomeViewModel) =
        HorizontalTimerItemAdapter(homeViewModel)
}