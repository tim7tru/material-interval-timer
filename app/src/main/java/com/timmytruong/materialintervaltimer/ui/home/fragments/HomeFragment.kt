package com.timmytruong.materialintervaltimer.ui.home.fragments

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.home.HomeClicks
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.ui.home.adapters.HorizontalTimerItemAdapter
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment(), HomeClicks.Main {

    @Inject @Recents
    lateinit var recentsAdapter: HorizontalTimerItemAdapter

    @Inject @Favourites
    lateinit var favouritesAdapter: HorizontalTimerItemAdapter

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private lateinit var binding: FragmentHomeBinding

    private val favouriteTimersObserver = Observer<List<Timer>> {
        binding.isFavouritesEmpty = it.isEmpty()
        favouritesAdapter.addList(it)
    }

    private val recentTimersObserver = Observer<List<Timer>> {
        binding.isRecentsEmpty = it.isEmpty()
        recentsAdapter.addList(it)
    }

    override val baseViewModel: BaseViewModel
        get() = homeViewModel

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    is Timer -> handleTimerEvent(timer = it)
                    else -> { /** Do Nothing **/ }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        return binding.root
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
        Navigation.findNavController(requireView()).navigate(action)
    }

    override fun onAddClicked(view: View) {
        val action = HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment()
        action.clearViewModel = true
        Navigation.findNavController(requireView()).navigate(action)
    }
}