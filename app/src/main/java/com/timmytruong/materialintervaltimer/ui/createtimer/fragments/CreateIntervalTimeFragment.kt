package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerClicks
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalTimeFragment : BaseFragment(), CreateTimerClicks.Time {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    is Boolean -> handleCompletionEvent(completed = it)
                }
            }
        }

    private val intervalObserver = Observer<Interval> { binding.interval = it }

    private lateinit var binding: FragmentCreateIntervalTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_create_interval_time,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        updateProgressBar(progress = 100)
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

    private fun handleCompletionEvent(completed: Boolean) {
        if (completed) {
            val action =
                CreateIntervalTimeFragmentDirections.actionCreateIntervalTimeFragmentToCreateTimerFragment()
            action.clearViewModel = false
            view?.let { Navigation.findNavController(it).navigate(action) }
        }
    }
}