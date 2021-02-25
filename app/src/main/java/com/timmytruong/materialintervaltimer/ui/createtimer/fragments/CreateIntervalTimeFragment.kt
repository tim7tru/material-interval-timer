package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.COMPLETION_EVENT
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerClicks
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_FULL
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalTimeFragment : BaseFragment<FragmentCreateIntervalTimeBinding>(),
    CreateTimerClicks.Time {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val layoutId: Int
        get() = R.layout.fragment_create_interval_time

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            isEventHandled(event)?.let {
                when (it.first) {
                    COMPLETION_EVENT -> handleCompletionEvent()
                }
            }
        }

    private val intervalObserver = Observer<Interval> { binding.interval = it }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        updateProgressBar(progress = PROGRESS_FULL)
    }

    override fun bindView() {
        binding.onClick = this
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.interval.observe(viewLifecycleOwner, intervalObserver)
    }

    override fun onNumberClicked(view: View) {
        createTimerViewModel.addToTime(newNumber = (view as? MaterialButton)?.text.toString())
    }

    override fun onBackClicked(view: View) {
        createTimerViewModel.removeFromTime()
    }

    override fun onAddClicked(view: View) {
        createTimerViewModel.addInterval()
    }

    private fun handleCompletionEvent() {
        val action =
            CreateIntervalTimeFragmentDirections.actionCreateIntervalTimeFragmentToCreateTimerFragment()
        action.clearViewModel = false
        findNavController().navigate(action)
    }
}