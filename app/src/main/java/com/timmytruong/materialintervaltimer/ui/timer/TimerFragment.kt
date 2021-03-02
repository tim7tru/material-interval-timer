package com.timmytruong.materialintervaltimer.ui.timer

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.model.Interval
import com.timmytruong.materialintervaltimer.model.IntervalSound
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_BAR_PROPERTY
import com.timmytruong.materialintervaltimer.ui.timer.adapters.TimerIntervalAdapter
import com.timmytruong.materialintervaltimer.utils.DesignUtils.formatNormalizedTime
import com.timmytruong.materialintervaltimer.utils.DesignUtils.getColorList
import com.timmytruong.materialintervaltimer.utils.DesignUtils.getTimeFromSeconds
import com.timmytruong.materialintervaltimer.utils.DesignUtils.showDialog
import com.timmytruong.materialintervaltimer.utils.constants.FULL_TIME_PROGRESS
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_I
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import com.timmytruong.materialintervaltimer.utils.enums.TimerState
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : TimerClicks, BaseFragment<FragmentTimerBinding>() {

    @Inject
    lateinit var timerViewModel: TimerViewModel

    @Inject
    lateinit var intervalItemAdapter: TimerIntervalAdapter

    @Inject
    lateinit var dialogClicks: DialogInterface.OnClickListener

    @Inject
    lateinit var intervalsObserver: Observer<List<Interval>>

    private lateinit var menu: Menu

    private var progressAnimation: ObjectAnimator? = null

    private var currentIntervalTotalTime: Float = 0f

    private var currentIntervalTimeRemaining: Float = 0f

    private lateinit var favouriteMenuItem: MenuItem

    private val args: TimerFragmentArgs by navArgs()

    private val onSoundIconClicked = MenuItem.OnMenuItemClickListener {
        menu.findItem(R.id.soundOn).isVisible = it.itemId != R.id.soundOn
        menu.findItem(R.id.soundOff).isVisible = it.itemId == R.id.soundOn
        timerViewModel.setShouldPlaySound(playSound = it.itemId == R.id.soundOn)
        return@OnMenuItemClickListener true
    }

    private val onFavouriteClicked = MenuItem.OnMenuItemClickListener {
        val isChecked = menu.findItem(R.id.favorite).isChecked
        menu.findItem(R.id.favorite).isChecked = !isChecked
        timerViewModel.setShouldSave(save = !isChecked)
        return@OnMenuItemClickListener true
    }

    private val timeRemainingObserver = Observer<Int> {
        currentIntervalTimeRemaining = it.toFloat()
        val time = getTimeFromSeconds(currentIntervalTimeRemaining.toInt() / MILLI_IN_SECS_I)
        binding.timeRemaining = formatNormalizedTime(time, getString(R.string.timerTimeFormat))
    }

    override val layoutId: Int
        get() = R.layout.fragment_timer

    override val eventObserver: Observer<Event<Pair<String, Any>>>
        get() = Observer { event ->
            handleEvent(event) {
                when (it.first) {
                    TIMER_STATE -> handleTimerStateEvent(timerState = it.second as TimerState)
                    SOUND -> handleSoundEvent(sound = it.second as IntervalSound)
                    REMAINING_TIME -> handleTotalTimeEvent(totalTime = it.second as Int)
                    EXIT -> goToHome()
                    IS_SAVED -> favouriteMenuItem.isChecked = true
                }
            }
        }

    override val baseViewModel: BaseViewModel
        get() = timerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            timerViewModel.setTimerState(newState = TimerState.PAUSED)
            showCloseDialog()
        }
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

    private fun showCloseDialog(): AlertDialog =
        showDialog(
            context = ctx,
            title = getString(R.string.dialogCloseTimerTitle),
            message = getString(R.string.dialogCloseTimerMessage),
            negativeMessage = getString(R.string.cancel),
            positiveMessage = getString(R.string.exit),
            clicks = dialogClicks
        )

    private fun cancelProgressAnimation() {
        if (progressAnimation?.isStarted == true) {
            progressAnimation?.cancel()
            progressAnimation = null
        }
    }

    private fun handleTimerStateEvent(timerState: TimerState) {
        cancelProgressAnimation()

        when (timerState) {
            TimerState.STOPPED -> {
                updateProgressBar(
                    from = binding.progress ?: FULL_TIME_PROGRESS,
                    to = FULL_TIME_PROGRESS
                )
                binding.progress = FULL_TIME_PROGRESS
            }
            TimerState.PAUSED -> {
                binding.progress =
                    ((currentIntervalTimeRemaining / currentIntervalTotalTime) * MILLI_IN_SECS_I).toInt()
            }
            TimerState.RUNNING -> {
                updateProgressBar(
                    from = binding.progress ?: FULL_TIME_PROGRESS,
                    to = 0,
                    duration = currentIntervalTimeRemaining.toLong()
                )
            }
        }

        binding.timerState = timerState
    }

    private fun handleSoundEvent(sound: IntervalSound) {
        if (sound.sound_id != -1) {
            val player = MediaPlayer.create(ctx, sound.sound_id)
            player?.start()
        }
    }

    private fun handleTotalTimeEvent(totalTime: Int) {
        currentIntervalTotalTime = totalTime.toFloat()
    }

    private fun showFavouriteMenuIcon() {
        favouriteMenuItem = menu.findItem(R.id.favorite).apply {
            iconTintList = getColorList(ctx, R.color.favourite_button_color)
            isCheckable = true
            setOnMenuItemClickListener(onFavouriteClicked)
        }
    }

    private fun showSoundMenuIcon() {
        menu.findItem(R.id.soundOn).isVisible = true
        menu.findItem(R.id.soundOn).setOnMenuItemClickListener(onSoundIconClicked)
        menu.findItem(R.id.soundOff).setOnMenuItemClickListener(onSoundIconClicked)
    }

    private fun goToHome() {
        val action = TimerFragmentDirections.actionTimerFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    private fun updateProgressBar(from: Int, to: Int, duration: Long = MILLI_IN_SECS_L) {
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
}

@InstallIn(FragmentComponent::class)
@Module
class TimerModule {

    @FragmentScoped
    @Provides
    fun provideDialogClicks(
        timerViewModel: TimerViewModel
    ) = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                timerViewModel.setTimerState(TimerState.STOPPED)
                timerViewModel.sendExitEvent()
                dialog.dismiss()
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
        }
    }

    @FragmentScoped
    @Provides
    fun provideIntervalsObserver(
        intervalItemAdapter: TimerIntervalAdapter
    ) = Observer<List<Interval>> {
        intervalItemAdapter.addList(it)
    }
}