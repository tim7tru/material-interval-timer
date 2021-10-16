package com.timmytruong.materialintervaltimer.ui.timer

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.di.CircularProgress
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import com.timmytruong.materialintervaltimer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : BaseFragment<TimerScreen, TimerViewModel, FragmentTimerBinding>(),
    DialogInterface.OnClickListener {

    @Inject
    lateinit var intervalItemAdapter: IntervalItemAdapter

    @Inject
    override lateinit var screen: TimerScreen

    @Inject
    @CircularProgress
    lateinit var progressBar: ProgressAnimation

    override val name: String = this.name()

    override val viewModel: TimerViewModel by viewModels()

    override val layoutId: Int = R.layout.fragment_timer

    private lateinit var menu: Menu

    private val mutedButton: MenuItem by lazy { menu.findItem(R.id.soundOff) }

    private val unmutedButton: MenuItem by lazy { menu.findItem(R.id.soundOn) }

    private val favouriteButton: MenuItem by lazy { menu.findItem(R.id.favorite) }

    private val args: TimerFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_bar, menu)
        this.menu = menu
        favouriteButton.isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item) {
            favouriteButton -> viewModel.setShouldSave()
            mutedButton, unmutedButton -> soundToggled(item)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            viewModel.handlePause()
            showExitDialog()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimer(id = args.timerId)
        startSuspending { screen.intervals.collectLatest(intervalItemAdapter::addList) }
    }

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.screen = screen
        binding?.bottomSheetRecycler?.adapter = intervalItemAdapter
    }

    override fun onDestroyView() {
        binding?.bottomSheetRecycler?.adapter = null
        super.onDestroyView()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        if (button == DialogInterface.BUTTON_POSITIVE) viewModel.exit()
        dialog?.dismiss()
    }

    override fun eventHandler(event: Pair<String, Any>) {
        when (event.first) {
            CANCEL_ANIMATION -> progressBar.cancelAnimation()
            START_ANIMATION -> progressBar.startAnimation(
                target = binding?.fragmentTimerProgressCircle,
                start = screen.progress.get(),
                end = 0,
                duration = event.second as Long
            )
            IS_SAVED -> favouriteButton.isVisible = true
            SHOW_SOUND_TOGGLE -> unmutedButton.isVisible = event.second as Boolean
        }
    }

    private fun soundToggled(item: MenuItem) {
        unmutedButton.isVisible = item.itemId == R.id.soundOff
        mutedButton.isVisible = item.itemId == R.id.soundOn
        viewModel.isMuted = item.itemId == R.id.soundOn
    }

    private fun showExitDialog() = popUpProvider.showDialog(
        activity = requireActivity(),
        title = R.string.dialogCloseTimerTitle,
        message = R.string.dialogCloseTimerMessage,
        positiveMessage = R.string.exit,
        negativeMessage = R.string.cancel,
        clicks = this@TimerFragment
    )
}

data class TimerScreen(
    val timerState: ObservableField<TimerState> = ObservableField(TimerState.STOPPED),
    val timeRemaining: ObservableString = ObservableString(""),
    val progress: ObservableInt = ObservableInt(0),
    var intervals: Flow<@JvmSuppressWildcards List<IntervalItemScreenBinding>> = emptyFlow(),
) : BaseScreen() {

    fun navToHome() = TimerFragmentDirections.actionTimerFragmentToHomeFragment()
}