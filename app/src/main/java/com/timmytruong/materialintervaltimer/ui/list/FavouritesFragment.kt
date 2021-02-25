package com.timmytruong.materialintervaltimer.ui.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentFavouritesBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.VerticalTimerListAdapter
import com.timmytruong.materialintervaltimer.utils.events.EMPTY_ERROR
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesFragment : BaseFragment<FragmentFavouritesBinding>() {

    @Inject
    lateinit var verticalTimerListAdapter: VerticalTimerListAdapter

    @Inject
    lateinit var timerListViewModel: TimerListViewModel

    @Inject
    @Favourites
    lateinit var timerListObserver: Observer<List<Timer>>

    override val layoutId: Int
        get() = R.layout.fragment_favourites

    override val baseViewModel: BaseViewModel
        get() = timerListViewModel

    override fun bindView() {
        binding.fragmentFavouritesRecycler.adapter = verticalTimerListAdapter
    }

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            isEventHandled(event)?.let {
                when (it.first) {
                    EMPTY_ERROR -> toggleEmptyListError(true)
                    TIMER -> handleTimerClick(timer = it.second as Timer)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        timerListViewModel.fetchFavourites()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        timerListViewModel.favourites.observe(viewLifecycleOwner, timerListObserver)
    }

    private fun handleTimerClick(timer: Timer) {
        val action = FavouritesFragmentDirections.actionFavouritesFragmentToTimerActionBottomSheet()
        action.timerId = timer.id
        action.isFavourited = timer.timer_saved
        findNavController().navigate(action)
    }

    private fun toggleEmptyListError(show: Boolean) {

    }
}

@InstallIn(FragmentComponent::class)
@Module
class FavouritesModule {

    @Favourites
    @FragmentScoped
    @Provides
    fun provideFavouritesObserver(
        timerListViewModel: TimerListViewModel,
        verticalTimerListAdapter: VerticalTimerListAdapter
    ): Observer<List<Timer>> = Observer<List<Timer>> {
        if (it.isEmpty()) timerListViewModel.onEmptyList()
        else verticalTimerListAdapter.addList(it)
    }
}