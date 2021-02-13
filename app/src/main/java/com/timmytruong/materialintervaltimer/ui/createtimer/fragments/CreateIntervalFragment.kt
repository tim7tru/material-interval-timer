package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerClicks
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.Error
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalFragment : BaseFragment(), CreateTimerClicks.Interval {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    is Error.InputError -> handleInputError()
                    is Error.UnknownError -> handleUnknownError()
                    is Boolean -> handleCompletionEvent(completed = it)
                }
            }
        }

    private lateinit var binding: FragmentCreateIntervalBinding

    private val intervalObserver = Observer<Interval> { interval ->
        interval.interval_icon_id.let {
            val tag = if (it != -1) DesignUtils.getTagFromDrawableId(
                context = requireContext(),
                id = it
            ) else it
            val grid = binding.fragmentCreateIntervalIconGrid
            val count = grid.childCount
            for (i in 0 until count) {
                val view = grid.getChildAt(i)
                setIconCheckedBackground(image = view as ImageView, checked = view.tag == tag)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_interval, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar(progress = 50)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        updateProgressBar(progress = 50)
    }

    override fun onBackPressed(): Boolean {
        updateProgressBar(progress = 0)
        return super.onBackPressed()
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

    private fun handleCompletionEvent(completed: Boolean) {
        if (completed) {
            val action =
                CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
            view?.let { Navigation.findNavController(it).navigate(action) }
        }
    }

    private fun handleInputError() {
        binding.fragmentCreateIntervalTitleInput.error = getString(R.string.noTitleError)
    }

    private fun setIconCheckedBackground(image: ImageView, checked: Boolean) {
        when (checked) {
            true -> {
                image.backgroundTintList =
                    DesignUtils.getColour(requireContext(), R.color.colorSecondaryAccent)
            }
            false -> {
                image.backgroundTintList =
                    DesignUtils.getColour(requireContext(), R.color.colorBackgroundLight)
            }
        }
    }
}