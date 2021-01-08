package com.timmytruong.materialintervaltimer.ui.createTimer.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.ui.createTimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalFragment : BaseFragment(), OnClickListeners.CreateIntervalFrag {

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val errorObserver: Observer<Event<ErrorType>>
        get() = Observer { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    ErrorType.EMPTY_INPUT -> showInvalidTitleError()
                    else -> {
                    }
                }
            }
        }

    private lateinit var binding: FragmentCreateIntervalBinding

    private val completionObserver = Observer<Event<Boolean>> {
        it?.getContentIfNotHandled()?.let { complete ->
            if (complete) {
                goToCreateIntervalTime()
            }
        }
    }

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
        Log.d("ViewModel", "CreateViewModel $createTimerViewModel")
    }

    override fun bindView() {
        binding.onClick = this
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.interval.observe(viewLifecycleOwner, intervalObserver)
        createTimerViewModel.completionEvent.observe(viewLifecycleOwner, completionObserver)
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

    private fun goToCreateIntervalTime() {
        val action =
            CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
        view?.let { Navigation.findNavController(it).navigate(action) }
    }

    private fun showInvalidTitleError() {
        binding.fragmentCreateIntervalTitleInput.error = getString(R.string.noTitleError)
    }

    private fun setIconCheckedBackground(image: ImageView, checked: Boolean) {
        when (checked) {
            true -> image.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorSecondaryAccent
                )
            false -> image.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorPrimary
                )
        }
    }

}