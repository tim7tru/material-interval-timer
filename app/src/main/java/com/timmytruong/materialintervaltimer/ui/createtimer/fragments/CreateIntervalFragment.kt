package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.COMPLETION_EVENT
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerClicks
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_HALF
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_ZERO
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.Event
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import com.timmytruong.materialintervaltimer.utils.events.UNKNOWN_ERROR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalFragment : BaseFragment<FragmentCreateIntervalBinding>(),
    CreateTimerClicks.Interval {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val layoutId: Int
        get() = R.layout.fragment_create_interval

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            handleEvent(event) {
                when (it.first) {
                    INPUT_ERROR -> handleInputError()
                    UNKNOWN_ERROR -> handleUnknownError()
                    COMPLETION_EVENT -> handleCompletionEvent()
                }
            }
        }

    private val intervalObserver = Observer<Interval> { interval ->
        interval.interval_icon_id.let {
            val tag =
                if (it != -1) DesignUtils.getTagFromDrawableId(context = requireContext(), id = it)
                else it
            val grid = binding.fragmentCreateIntervalIconGrid
            for (i in 0 until grid.childCount) {
                val view = grid.getChildAt(i)
                (view as ImageView).isSelected = view.tag == tag
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar(progress = PROGRESS_HALF)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        updateProgressBar(progress = PROGRESS_HALF)
    }

    override fun onPause() {
        updateProgressBar(progress = PROGRESS_ZERO)
        super.onPause()
    }

    override fun bindView() {
        binding.onClick = this
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.interval.observe(viewLifecycleOwner, intervalObserver)
    }

    override fun onContinueClicked(view: View) {
        createTimerViewModel.setIntervalTitle(binding.fragmentCreateIntervalTitleInput.text.toString())
    }

    override fun onIconSwitchClicked(view: View) {
        val newState = !binding.fragmentCreateIntervalIconSwitch.isChecked
        if (!newState) {
            createTimerViewModel.setIntervalIcon(id = null)
        }
        binding.iconChecked = newState
    }

    override fun onIconClicked(view: View) {
        createTimerViewModel.setIntervalIcon(
            id = DesignUtils.getDrawableIdFromTag(
                context = view.context,
                tag = view.tag.toString()
            )
        )
    }

    override fun handleInputError() {
        binding.fragmentCreateIntervalTitleInput.error = getString(R.string.noTitleError)
    }

    private fun handleCompletionEvent() {
        val action =
            CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
        findNavController().navigate(action)
    }
}