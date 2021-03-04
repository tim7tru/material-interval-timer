package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.base.NAVIGATE
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_HALF
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_ZERO
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.INPUT_ERROR
import com.timmytruong.materialintervaltimer.utils.events.UNKNOWN_ERROR
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalFragment : BaseFragment<FragmentCreateIntervalBinding>() {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val layoutId: Int = R.layout.fragment_create_interval

    override val eventHandler: (Pair<String, Any>) -> Unit = {
        when (it.first) {
            INPUT_ERROR -> handleInputError()
            UNKNOWN_ERROR -> handleUnknownError()
            NAVIGATE -> navigate(it.second as NavDirections)
        }
    }

    private val intervalObserver = Observer<Interval> { interval ->
        interval.interval_icon_id.let {
            val tag =
                if (it != -1) DesignUtils.getTagFromDrawableId(context = ctx, id = it)
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
        updateProgressBar(PROGRESS_HALF)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        updateProgressBar(PROGRESS_HALF)
    }

    override fun onPause() {
        updateProgressBar(PROGRESS_ZERO)
        super.onPause()
    }

    override fun bindView() {
        binding.viewModel = createTimerViewModel
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.interval.observe(viewLifecycleOwner, intervalObserver)
    }

    override fun handleInputError() {
        binding.fragmentCreateIntervalTitleInput.error = getString(R.string.noTitleError)
    }
}