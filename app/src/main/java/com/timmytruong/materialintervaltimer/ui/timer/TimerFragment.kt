package com.timmytruong.materialintervaltimer.ui.timer

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_BAR_PROPERTY
import com.timmytruong.materialintervaltimer.ui.timer.adapters.TimerIntervalAdapter
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.TimerState
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : TimerClicks, BaseFragment<FragmentTimerBinding>() {

    @Inject
    lateinit var timerViewModel: TimerViewModel

    @Inject
    lateinit var intervalItemAdapter: TimerIntervalAdapter

    private lateinit var menu: Menu

    private var progressAnimation: ObjectAnimator? = null

    private var currentIntervalTotalTime: Float = 0f

    private var currentIntervalTimeRemaining: Float = 0f

    private val args: TimerFragmentArgs by navArgs()

    private val onSoundIconClicked = MenuItem.OnMenuItemClickListener {
        menu.findItem(R.id.soundOn).isVisible = it.itemId != R.id.soundOn
        menu.findItem(R.id.soundOff).isVisible = it.itemId == R.id.soundOn
        timerViewModel.setShouldPlaySound(playSound = it.itemId == R.id.soundOn)
        return@OnMenuItemClickListener true
    }

    private val onFavouriteClicked = MenuItem.OnMenuItemClickListener {
        val isChecked = menu.findItem(R.id.favorite).isChecked
        menu.findItem(R.id.favorite).iconTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.favourite_button_color)
        menu.findItem(R.id.favorite).isChecked = !isChecked
        timerViewModel.setShouldSave(save = !isChecked)
        return@OnMenuItemClickListener true
    }

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            isEventHandled(event)?.let {
                when (it.first) {
                    TIMER_STATE -> handleTimerStateEvent(timerState = it.second as TimerState)
                    SOUND -> handleSoundEvent(sound = it.second as IntervalSound)
                    REMAINING_TIME -> handleTotalTimeEvent(totalTime = it.second as Int)
                }
            }
        }

    override val baseViewModel: BaseViewModel
        get() = timerViewModel

    private val timeRemainingObserver = Observer<Int> {
        currentIntervalTimeRemaining = it.toFloat()
        val time = DesignUtils.getTimeFromSeconds(currentIntervalTimeRemaining.toInt() / 1000)
        binding.timeRemaining =
            DesignUtils.formatNormalizedTime(time, getString(R.string.timerTimeFormat))
    }

    private val intervalsObserver = Observer<ArrayList<Interval>> {
        intervalItemAdapter.addList(it)
    }

    private fun cancelProgressAnimation() {
        if (progressAnimation?.isStarted == true) {
            progressAnimation?.cancel()
            progressAnimation = null
        }
    }

    private fun handleTimerStateEvent(timerState: TimerState) {
        when (timerState) {
            TimerState.STOPPED -> {
                cancelProgressAnimation()
                updateProgressBar(from = binding.progress ?: 1000, to = 1000)
                binding.progress = 1000
            }
            TimerState.PAUSED -> {
                cancelProgressAnimation()
                binding.progress =
                    ((currentIntervalTimeRemaining / currentIntervalTotalTime) * 1000).toInt()
            }
            TimerState.RUNNING -> {
                cancelProgressAnimation()
                updateProgressBar(
                    from = binding.progress ?: 1000,
                    to = 0,
                    duration = currentIntervalTimeRemaining.toLong()
                )
            }
        }

        binding.timerState = timerState
    }

    private fun handleSoundEvent(sound: IntervalSound) {
        if (sound.sound_id != -1) {
            val player = MediaPlayer.create(requireContext(), sound.sound_id)
            player?.start()
        }
    }

    private fun handleTotalTimeEvent(totalTime: Int) {
        currentIntervalTotalTime = totalTime.toFloat()
    }

    private fun showFavouriteMenuIcon() {
        menu.findItem(R.id.favorite).isVisible = true
        menu.findItem(R.id.favorite).setOnMenuItemClickListener(onFavouriteClicked)
    }

    private fun showSoundMenuIcon() {
        menu.findItem(R.id.soundOn).isVisible = true
        menu.findItem(R.id.soundOn).setOnMenuItemClickListener(onSoundIconClicked)
        menu.findItem(R.id.soundOff).setOnMenuItemClickListener(onSoundIconClicked)
    }

    private fun getFavouriteColour(isChecked: Boolean): ColorStateList? =
        if (!isChecked) ContextCompat.getColorStateList(requireContext(), R.color.colorGolden)
        else ContextCompat.getColorStateList(requireContext(), R.color.colorSecondaryDark)

    private fun updateProgressBar(from: Int, to: Int, duration: Long = 1000L) {
        progressAnimation =
            ObjectAnimator.ofInt(
                binding.fragmentTimerProgressCircle,
                PROGRESS_BAR_PROPERTY,
                from,
                to
            )
        progressAnimation?.interpolator = LinearInterpolator()
        progressAnimation?.duration = duration
        progressAnimation?.start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_bar, menu)
        this.menu = menu
        showFavouriteMenuIcon()
        showSoundMenuIcon()
    }

    override fun bindView() {
        binding.onClick = this
        binding.bottomSheetRecycler.adapter = intervalItemAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        timerViewModel.fetchTimerFromRoom(id = args.timerId)
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        timerViewModel.intervals.observe(viewLifecycleOwner, intervalsObserver)
        timerViewModel.timeRemaining.observe(viewLifecycleOwner, timeRemainingObserver)
    }

    override fun onPlayPauseClicked(view: View) {
        val newState = when (binding.timerState) {
            TimerState.RUNNING -> TimerState.PAUSED
            else -> TimerState.RUNNING
        }

        timerViewModel.setTimerState(newState = newState)
    }

    override fun onStopClicked(view: View) {
        timerViewModel.setTimerState(newState = TimerState.STOPPED)
    }

    override fun showCloseDialog(view: View) {
//        timerDialog.build(
//            act = requireActivity(),
//            dialog = Dialog(
//                title = getString(R.string.dialogCloseTimerTitle),
//                message = getString(R.string.dialogCloseTimerMessage),
//                negativeMessage = getString(R.string.cancel),
//                positiveMessage = getString(R.string.exit),
//                callback = this
//            )
//        )
//        timerDialog.show()
    }

//    override fun onPositiveDialogClicked(view: View) {
//        timerViewModel.setTimerState(TimerState.STOPPED)
//        goToHome()
//    }

    override fun onBackPressed(): Boolean {
        timerViewModel.setTimerState(newState = TimerState.PAUSED)
        showCloseDialog(view = requireView())
        return false
    }

    override val layoutId: Int
        get() = R.layout.fragment_timer

//    override fun onNegativeDialogClicked(view: View) {
//        /** Do Nothing */
//    }
}