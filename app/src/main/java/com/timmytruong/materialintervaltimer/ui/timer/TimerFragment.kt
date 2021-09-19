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
        showFavouriteMenuIcon()
        showSoundMenuIcon()
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
            IS_SAVED -> favouriteButton.isChecked = true
        }
    }

    private fun showFavouriteMenuIcon() {
        favouriteButton.apply {
            iconTintList = resources.colorList(R.color.favourite_button_color)
            setOnMenuItemClickListener {
                val event = !favouriteButton.isChecked
                favouriteButton.isChecked = event
                viewModel.setShouldSave(favourite = event)
                true
            }
        }
    }

    private fun showSoundMenuIcon() {
        unmutedButton.isVisible = true
        MenuItem.OnMenuItemClickListener {
            unmutedButton.isVisible = it.itemId != R.id.soundOn
            mutedButton.isVisible = it.itemId == R.id.soundOn
            viewModel.isMuted = it.itemId == R.id.soundOff
            return@OnMenuItemClickListener true
        }.apply {
            unmutedButton.setOnMenuItemClickListener(this)
            mutedButton.setOnMenuItemClickListener(this)
        }
    }

    private fun showExitDialog() = popUpProvider.showDialog(
        title = R.string.dialogCloseTimerTitle,
        message = R.string.dialogCloseTimerMessage,
        positiveMessage = R.string.exit,
        neutralMessage = -1,
        negativeMessage = R.string.cancel,
        clicks = this@TimerFragment
    )
}

data class TimerScreen(
    val timerState: ObservableField<TimerState> = ObservableField(TimerState.STOPPED),
    val timeRemaining: ObservableField<String> = ObservableField(""),
    val progress: ObservableInt = ObservableInt(0),
    var intervals: Flow<@JvmSuppressWildcards List<IntervalItemScreenBinding>> = emptyFlow(),
) : BaseScreen() {

    fun navToHome() = TimerFragmentDirections.actionTimerFragmentToHomeFragment()
}