package com.timmytruong.materialintervaltimer.ui.createtimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimer
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.createtimer.adapters.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.createtimer.views.IntervalSoundsBottomSheet
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.Error
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment : BaseFragment(), CreateTimer.Main {

    @Inject
    lateinit var bottomSheetFragment: IntervalSoundsBottomSheet

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event?.getContentIfNotHandled()?.let {
                when (it) {
                    is Boolean -> handleCompletionEvent(completed = it)
                    is Error.InputError -> handleInputError(inputError = it)
                    is Error.UnknownError -> handleUnknownError()
                }
            }
        }

    private lateinit var binding: FragmentCreateTimerBinding

    private var timerId: Int? = null

    private val args: CreateTimerFragmentArgs by navArgs()

    private val timerObserver = Observer<Timer> { timer ->
        timer?.let {
            binding.timer = it

            if (it.id != 0) {
                timerId = it.id
            }

            it.timer_intervals.let {
                intervalAdapter.newList(list = it)
            }

            dismissBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_timer, container, false)
        return binding.root
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

    override fun goToAddInterval(view: View) {
        val action =
            CreateTimerFragmentDirections.actionCreateTimerFragmentToCreateIntervalFragment()
        Navigation.findNavController(view).navigate(action)
    }

    override fun onRepeatClicked(view: View) {
        createTimerViewModel.setRepeat(checked = !binding.fragmentCreateTimerRepeatSwitch.isChecked)
    }

    override fun onSaveClicked(view: View) {
        val newState = !binding.fragmentCreateTimerSavedSwitch.isChecked
        createTimerViewModel.setSaved(checked = newState)
    }

    override fun onSoundClicked(view: View) {
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    override fun onStartTimerClicked(view: View) {
        val title = binding.fragmentCreateTimerTitleInput.text.toString()
        createTimerViewModel.validateTimer(title = title)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        createTimerViewModel.timer.observe(viewLifecycleOwner, timerObserver)
    }

    override fun bindView() {
        binding.fragmentCreateTimerTaskList.adapter = intervalAdapter
        binding.onClick = this
    }

    private fun handleInputError(inputError: Error.InputError) {
        DesignUtils.showSnackbarError(
            contextView = requireView(),
            message = getString(R.string.emptyIntervalListError)
        )
    }

    private fun handleCompletionEvent(completed: Boolean) {
        if (completed) timerId?.let { id -> goToTimer(id = id) }
    }

    private fun goToTimer(id: Int) {
        val action =
            CreateTimerFragmentDirections.actionCreateTimerFragmentToTimerFragment()
        action.timerId = id
        Navigation.findNavController(requireView()).navigate(action)
    }

    private fun checkArguments() {
        if (args.clearViewModel) createTimerViewModel.clearTimer()
    }

    private fun dismissBottomSheet() {
        if (bottomSheetFragment.isVisible) {
            bottomSheetFragment.dismiss()
        }
    }
}