package com.timmytruong.materialintervaltimer.ui.list

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentRecentsBinding
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.reusable.VerticalTimerListAdapter
import com.timmytruong.materialintervaltimer.utils.events.Error
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@AndroidEntryPoint
class RecentsFragment : BaseFragment<FragmentRecentsBinding>() {

    @Inject
    lateinit var verticalTimerListAdapter: VerticalTimerListAdapter

    @Inject
    lateinit var timerListViewModel: TimerListViewModel

    @Inject @Recents
    lateinit var recentsObserver: Observer<List<Timer>>

    override val layoutId: Int
        get() = R.layout.fragment_recents

    override val baseViewModel: BaseViewModel
        get() = timerListViewModel

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is Error.QualifierError -> handleQualifierError(it.qualifier)
                    is Timer -> handleTimerClick(timer = it)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        timerListViewModel.fetchRecents()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        timerListViewModel.recents.observe(viewLifecycleOwner, recentsObserver)
    }

    override fun bindView() {
        binding.fragmentRecentsRecycler.adapter = verticalTimerListAdapter
    }

    override fun handleQualifierError(qualifier: String) {
        when (qualifier) {
            EMPTY_LIST_ERROR -> toggleEmptyListError(show = true)
        }
    }

    private fun handleTimerClick(timer: Timer) {
        val action = RecentsFragmentDirections.actionRecentsFragmentToTimerActionBottomSheet()
        action.timerId = timer.id
        action.isFavourited = timer.timer_saved
        findNavController().navigate(action)
    }

    private fun toggleEmptyListError(show: Boolean) {

    }
}

@InstallIn(FragmentComponent::class)
@Module
class RecentsModule {

    @Recents
    @FragmentScoped
    @Provides
    fun provideRecentsObserver(
        timerListViewModel: TimerListViewModel,
        verticalTimerListAdapter: VerticalTimerListAdapter
    ): Observer<List<Timer>> = Observer<List<Timer>> {
        if (it.isEmpty()) timerListViewModel.onEmptyList()
        else verticalTimerListAdapter.addList(it)
    }
}