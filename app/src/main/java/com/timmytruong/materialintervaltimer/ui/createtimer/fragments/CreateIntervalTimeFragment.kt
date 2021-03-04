package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_FULL
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalTimeFragment : BaseFragment<FragmentCreateIntervalTimeBinding>() {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val layoutId: Int = R.layout.fragment_create_interval_time

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventHandler: (Pair<String, Any>) -> Unit = {
        when (it.first) {
            NAVIGATE -> navigate(it.second as NavDirections)
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
        binding.viewModel = createTimerViewModel
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.interval.observe(viewLifecycleOwner, intervalObserver)
    }
}