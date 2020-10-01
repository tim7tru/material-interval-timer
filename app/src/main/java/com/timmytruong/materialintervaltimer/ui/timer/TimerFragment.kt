package com.timmytruong.materialintervaltimer.ui.timer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.model.Dialog
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import com.timmytruong.materialintervaltimer.utils.enums.TimerState
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.reusable.TimerDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_timer.*
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : Fragment(), OnClickListeners.TimerFrag, BaseFragment<TimerViewModel> {

    @Inject
    lateinit var timerViewModel: TimerViewModel

    @Inject
    lateinit var intervalItemAdapter: TimerIntervalAdapter

    @Inject
    lateinit var timerDialog: TimerDialog

    private lateinit var menu: Menu

    private lateinit var binding: FragmentTimerBinding

    private val args: TimerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_bar, menu)
        this.menu = menu
        menu.findItem(R.id.favorite).isVisible = true
        menu.findItem(R.id.favorite).setOnMenuItemClickListener(onFavouriteClicked)
        menu.findItem(R.id.soundOn).isVisible = true
        menu.findItem(R.id.soundOn).setOnMenuItemClickListener(onSoundIconClicked)
        menu.findItem(R.id.soundOff).setOnMenuItemClickListener(onSoundIconClicked)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private val onSoundIconClicked = MenuItem.OnMenuItemClickListener {
        menu.findItem(R.id.soundOn).isVisible = it.itemId != R.id.soundOn
        menu.findItem(R.id.soundOff).isVisible = it.itemId == R.id.soundOn
        timerViewModel.setSound(muted = it.itemId == R.id.soundOn)
        return@OnMenuItemClickListener true
    }

    private val onFavouriteClicked = MenuItem.OnMenuItemClickListener {
        val checked = menu.findItem(R.id.favorite).isChecked
        menu.findItem(R.id.favorite).iconTintList =
            if (!checked) {
                ContextCompat.getColorStateList(requireContext(), R.color.colorGolden)
            } else {
                ContextCompat.getColorStateList(requireContext(), R.color.colorSecondaryDark)
            }
        menu.findItem(R.id.favorite).isChecked = !checked
        timerViewModel.setSaved(save = !checked)
        return@OnMenuItemClickListener true
    }

    override val errorObserver: Observer<ErrorType> = Observer {}

    private val intervalSoundObserver = Observer<IntervalSound> { sound ->
        sound?.let {
            if (binding.timerState == TimerState.RUNNING && it.sound_id != -1)
                MediaPlayer.create(requireContext(), it.sound_id).start()
        }
    }

    private val intervalsObserver = Observer<ArrayList<Interval>> {
        intervalItemAdapter.newList(it)
    }

    private val countDownTimerObserver = Observer<CountDownTimer> {
        if (binding.timerState == TimerState.RUNNING) {
            it?.start()
        }
    }

    private val timeObserver = Observer<String> {
        binding.time = DesignUtils.formatNormalizedTime(
            time = it,
            format = getString(R.string.timerTimeFormat)
        )
    }

    private val progressBarObserver = Observer<Int> {
        if (it == 0) {
            binding.fragmentTimerProgressCircle.progress = 100
            return@Observer
        }
        DesignUtils.updateProgressBar(
            view = fragmentTimerProgressCircle,
            progress = it
        )
    }

    private val timerStateObserver = Observer<TimerState> {
        binding.timerState = it
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnBackPressed(view = view)
        bindOnClick()
        setupIntervalList()
        subscribeObservers(viewModel = timerViewModel)
        fetchTimer()
    }

    override fun subscribeObservers(viewModel: TimerViewModel) {
        viewModel.intervalSound.observe(viewLifecycleOwner, intervalSoundObserver)
        viewModel.intervals.observe(viewLifecycleOwner, intervalsObserver)
        viewModel.timerState.observe(viewLifecycleOwner, timerStateObserver)
        viewModel.progressBar.observe(viewLifecycleOwner, progressBarObserver)
        viewModel.time.observe(viewLifecycleOwner, timeObserver)
        viewModel.countDownTimer.observe(viewLifecycleOwner, countDownTimerObserver)
    }

    override fun bindOnClick() {
        binding.onClick = this
    }

    override fun onPlayPauseClicked(view: View) {
        val state = binding.timerState

        val newState =
            if (state == TimerState.RUNNING) TimerState.PAUSED
            else TimerState.RUNNING

        timerViewModel.setTimerState(timerState = newState)
    }

    override fun onStopClicked(view: View) {
        timerViewModel.setTimerState(timerState = TimerState.STOPPED)
    }

    override fun showCloseDialog(view: View) {
        val dialog = Dialog(
            title = getString(R.string.dialogCloseTimerTitle),
            message = getString(R.string.dialogCloseTimerMessage),
            negativeMessage = getString(R.string.cancel),
            positiveMessage = getString(R.string.exit),
            callback = this
        )

        timerDialog.build(act = requireActivity(), dialog = dialog)
        timerDialog.show()
    }

    override fun onPositiveDialogClicked(view: View) {
        timerViewModel.saveIfChecked()
        goToHome(view = view)
    }

    override fun onNegativeDialogClicked(view: View) { /** Do Nothing */ }

    private fun fetchTimer() {
        timerViewModel.getTimer(id = args.timerId)
    }

    private fun setupIntervalList() {
        bottom_sheet_recycler.adapter = intervalItemAdapter
    }

    private fun goToHome(view: View) {
        val action = TimerFragmentDirections.actionTimerFragmentToHomeFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

    private fun setupOnBackPressed(view: View) {
        try {
            (activity as MainActivity).onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
                OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    timerViewModel.setTimerState(TimerState.PAUSED)
                    showCloseDialog(view = view)
                }
            })
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }
}