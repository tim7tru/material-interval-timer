package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.createtimer.adapters.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.createtimer.views.IntervalSoundsBottomSheet
import com.timmytruong.materialintervaltimer.utils.DesignUtils.showSnackbarError
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import com.timmytruong.materialintervaltimer.utils.events.UNKNOWN_ERROR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment : BaseFragment<FragmentCreateTimerBinding>() {

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val layoutId: Int = R.layout.fragment_create_timer

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventHandler: (Pair<String, Any>) -> Unit = {
        when (it.first) {
            INPUT_ERROR -> handleInputError()
            UNKNOWN_ERROR -> handleUnknownError()
            NAVIGATE -> navigate(it.second as NavDirections)
        }
    }

    private val args: CreateTimerFragmentArgs by navArgs()

    private val timerObserver = Observer<Timer> { timer ->
        timer?.let {
            binding.timer = it

            it.timer_intervals.let { list ->
                intervalAdapter.addList(list)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        checkArguments()
    }

    override fun onResume() {
        super.onResume()
        toggleProgressBarVisibility(show = false)
    }

    override fun onPause() {
        super.onPause()
        createTimerViewModel.setTimerTitle(binding.fragmentCreateTimerTitleInput.text.toString())
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.timer.observe(viewLifecycleOwner, timerObserver)
    }

    override fun bindView() {
        binding.viewModel = createTimerViewModel
        binding.fragmentCreateTimerTaskList.adapter = intervalAdapter
    }

    override fun handleInputError() = showSnackbarError(
        contextView = v,
        message = getString(R.string.emptyIntervalListError)
    )

    private fun checkArguments() {
        if (args.clearViewModel) createTimerViewModel.clearTimer()
    }
}