package com.timmytruong.materialintervaltimer.ui.createTimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.createTimer.adapters.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.createTimer.CreateTimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_timer.*
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment : Fragment(), BaseFragment<CreateTimerViewModel>, OnClickListeners.CreateTimerFrag {

    @Inject
    lateinit var bottomSheetFragment: IntervalSoundsBottomSheet

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    private lateinit var binding: FragmentCreateTimerBinding

    private val args: CreateTimerFragmentArgs by navArgs()

    private val soundObserver = Observer<IntervalSound> {
        if (bottomSheetFragment.isVisible) bottomSheetFragment.dismiss()
        binding.soundName = it.sound_name
    }

    private val intervalsObserver = Observer<ArrayList<Interval>> {
        binding.isEmpty = it.size == 0
        intervalAdapter.newList(it)
    }

    private val repeatObserver = Observer<Boolean> { binding.repeat = it }

    private val saveObserver = Observer<Boolean> { binding.saved = it }

    private val timerTitleObserver = Observer<String> { binding.title = it }

    private val timerObserver = Observer<Timer> {
        it?.let {
            if (it.id != -1) {
                val action = CreateTimerFragmentDirections.actionCreateTimerFragmentToTimerFragment()
                action.timerId = it.id
                Navigation.findNavController(fragmentCreateTimerTitleTitle).navigate(action)
            }
        }
    }

    override val errorObserver: Observer<ErrorType> = Observer {
        when (it) {
            ErrorType.EMPTY_INPUT -> {
                this@CreateTimerFragment.view?.let { view ->
                    DesignUtils.showSnackbarError(view, getString(R.string.emptyIntervalListError))
                }
            }
            else -> {}
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_create_timer, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkArguments()

        setupOnBackPressed(view = view)

        subscribeObservers(viewModel = createTimerViewModel)

        bindOnClick()

        setIntervalList()
    }

    override fun onResume() {
        super.onResume()
        hideProgressBar()
    }

    override fun onPause() {
        super.onPause()
        createTimerViewModel.setTimerTitle(fragmentCreateTimerTitleInput.text.toString())
    }

    override fun goToHome(view: View) {
        val action = CreateTimerFragmentDirections.actionCreateTimerFragmentToHomeFragment()
        Navigation.findNavController(view).navigate(action)
    }

    override fun goToAddInterval(view: View) {
        val action = CreateTimerFragmentDirections.actionCreateTimerFragmentToCreateIntervalFragment()
        Navigation.findNavController(view).navigate(action)
    }

    override fun onRepeatClicked(view: View) {
        createTimerViewModel.setRepeat(checked = !fragmentCreateTimerRepeatSwitch.isChecked)
    }

    override fun onSaveClicked(view: View) {
        createTimerViewModel.setSaved(checked = !fragmentCreateTimerSavedSwitch.isChecked)
    }

    override fun onSoundClicked(view: View) {
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }

    override fun goToTimer(view: View) {
        createTimerViewModel.validateTimer(fragmentCreateTimerTitleTitle.text.toString())
    }

    private fun setupOnBackPressed(view: View) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                goToHome(view = view)
            }
        })
    }

    private fun hideProgressBar() {
        try {
            (activity as MainActivity).toggleProgressBarVisibility(show = false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIntervalList() {
        fragmentCreateTimerTaskList.adapter = intervalAdapter
    }

    private fun checkArguments() {
        if (args.clearViewModel) createTimerViewModel.clearTimer()
    }

    override fun subscribeObservers(viewModel: CreateTimerViewModel) {
        viewModel.selectedSound.observe(viewLifecycleOwner, soundObserver)
        viewModel.repeatChecked.observe(viewLifecycleOwner, repeatObserver)
        viewModel.savedChecked.observe(viewLifecycleOwner, saveObserver)
        viewModel.intervals.observe(viewLifecycleOwner, intervalsObserver)
        viewModel.timerTitle.observe(viewLifecycleOwner, timerTitleObserver)
        viewModel.timer.observe(viewLifecycleOwner, timerObserver)
        viewModel.error.observe(viewLifecycleOwner, errorObserver)
    }

    override fun bindOnClick() { binding.onClick = this }
}