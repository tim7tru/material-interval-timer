package com.timmytruong.materialintervaltimer.ui.timer

import android.content.DialogInterface
import android.media.MediaPlayer
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.di.CircularProgress
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.extensions.invisible
import com.timmytruong.materialintervaltimer.utils.extensions.show
import com.timmytruong.materialintervaltimer.utils.extensions.showIf
import com.timmytruong.materialintervaltimer.utils.extensions.toDisplayTime
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : BaseFragment<TimerViewModel, FragmentTimerBinding>(
    FragmentTimerBinding::inflate
), DialogInterface.OnClickListener {

    @Inject
    lateinit var intervalItemAdapter: IntervalItemAdapter

    @Inject
    @CircularProgress
    lateinit var progressBar: ProgressAnimation

    override val viewModel: TimerViewModel by viewModels()

    override val hasOptionsMenu: Boolean = true

    override val hasBackPress: Boolean = true

    override val fragmentTitle: Int = R.string.timer

    private val mutedButton: MenuItem? by lazy { menu?.findItem(R.id.soundOff) }

    private val unmutedButton: MenuItem? by lazy { menu?.findItem(R.id.soundOn) }

    private val favoriteButton: MenuItem? by lazy { menu?.findItem(R.id.favorite) }

    private val args: TimerFragmentArgs by navArgs()

    override fun onBackPressed() {
        viewModel.handlePause()

        popUpProvider.showDialog(
            activity = requireActivity(),
            title = R.string.dialogCloseTimerTitle,
            message = R.string.dialogCloseTimerMessage,
            positiveMessage = R.string.exit,
            negativeMessage = R.string.cancel,
            clicks = this@TimerFragment
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item) {
            favoriteButton -> viewModel.setShouldSave()
            mutedButton, unmutedButton -> soundToggled(item.itemId)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimer(id = args.timerId)
    }

    override fun bindView() = binding?.apply {
        favoriteButton.show()
        play.setOnClickListener { viewModel.handlePlay() }
        pause.setOnClickListener { viewModel.handlePause() }
        stop.setOnClickListener { viewModel.handleStop() }
        bottomSheetRecycler.adapter = intervalItemAdapter
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.timeRemaining.onEach {  binding?.time?.text = it.toDisplayTime(resources) }.launchIn(scope)
        viewModel.timerState.onEach { bindTimerState(it) }.launchIn(scope)
        viewModel.intervals.onEach { intervalItemAdapter.addList(it) }.launchIn(scope)
    }

    override fun onDestroyView() {
        binding?.bottomSheetRecycler?.adapter = null
        super.onDestroyView()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        if (button == DialogInterface.BUTTON_POSITIVE) viewModel.exit()
        dialog?.dismiss()
    }

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.Timer.Started -> progressBar.startAnimation(
                target = binding?.progress,
                start = event.progress.toInt(),
                end = 0,
                duration = event.timeRemaining
            )
            is Event.Timer.HasSound -> unmutedButton.showIf(event.hasSound)
            is Event.Timer.IsSaved -> favoriteButton.showIf(event.saved)
            is Event.PlaySound -> MediaPlayer.create(requireContext(), event.id).start()
            is Event.Timer.Progress -> binding?.progress?.progress = event.progress.toInt()
            Event.Timer.CancelAnimation -> progressBar.cancelAnimation()
            else -> super.eventHandler(event)
        }
    }

    private fun soundToggled(id: Int) {
        unmutedButton.showIf(id == R.id.soundOff)
        mutedButton.showIf(id == R.id.soundOn)
        viewModel.isMuted = id == R.id.soundOn
    }

    private fun bindTimerState(state: TimerState) = binding?.apply {
        when (state) {
            TimerState.RUNNING -> {
                play.hide()
                pause.show()
            }
            TimerState.PAUSED, TimerState.STOPPED -> {
                play.show()
                pause.invisible()
            }
        }
    }
}

@Open
@ActivityRetainedScoped
class TimerDirections @Inject constructor() {
    fun toHome() = TimerFragmentDirections.toHome()
}