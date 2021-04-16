package com.timmytruong.materialintervaltimer.ui.timer

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.view.animation.LinearInterpolator
import androidx.activity.addCallback
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.CreateTimerFragment
import com.timmytruong.materialintervaltimer.ui.create.timer.CreateTimerScreen
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_BAR_PROPERTY
import com.timmytruong.materialintervaltimer.utils.*
import com.timmytruong.materialintervaltimer.utils.constants.MILLI_IN_SECS_L
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped
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
    lateinit var linearInterpolator: LinearInterpolator

    override val name: String = this.name()

    override val viewModel: TimerViewModel by viewModels()

    override val layoutId: Int = R.layout.fragment_timer

    override val eventFlowHandler: suspend (Pair<String, Any>) -> Unit by lazy {
        {
            when (it.first) {
                CANCEL_ANIMATION -> cancelProgressAnimation()
                START_ANIMATION -> startProgressAnimation(it.second as Long)
                IS_SAVED -> favouriteButton.isChecked = true
            }
        }
    }

    private lateinit var menu: Menu

    private val mutedButton: MenuItem by lazy { menu.findItem(R.id.soundOff) }

    private val unmutedButton: MenuItem by lazy { menu.findItem(R.id.soundOn) }

    private val favouriteButton: MenuItem by lazy { menu.findItem(R.id.favorite) }

    private var progressAnimation: ObjectAnimator? = null

    private val args: TimerFragmentArgs by navArgs()

    private val onSoundIconClicked: MenuItem.OnMenuItemClickListener by lazy {
        MenuItem.OnMenuItemClickListener {
            unmutedButton.isVisible = it.itemId != R.id.soundOn
            mutedButton.isVisible = it.itemId == R.id.soundOn
            viewModel.isMuted = it.itemId == R.id.soundOff
            return@OnMenuItemClickListener true
        }
    }

    private val onFavouriteClicked: MenuItem.OnMenuItemClickListener by lazy {
        MenuItem.OnMenuItemClickListener {
            val event = !favouriteButton.isChecked
            favouriteButton.isChecked = event
            viewModel.setShouldSave(save = event)
            return@OnMenuItemClickListener true
        }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            viewModel.handlePause()
            showExitDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchTimer(id = args.timerId)
        bindView()
    }

    override fun onResume() {
        super.onResume()
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

    private fun cancelProgressAnimation() {
        if (progressAnimation?.isStarted == true) {
            progressAnimation?.cancel()
            progressAnimation = null
        }
    }

    private fun showFavouriteMenuIcon() {
        favouriteButton.apply {
            iconTintList = ctx.colorList(R.color.favourite_button_color)
            setOnMenuItemClickListener(onFavouriteClicked)
        }
    }

    private fun showSoundMenuIcon() {
        unmutedButton.isVisible = true
        unmutedButton.setOnMenuItemClickListener(onSoundIconClicked)
        mutedButton.setOnMenuItemClickListener(onSoundIconClicked)
    }

    private fun showExitDialog() = showDialog(
        context = ctx,
        title = string(R.string.dialogCloseTimerTitle),
        message = string(R.string.dialogCloseTimerMessage),
        positiveMessage = string(R.string.exit),
        negativeMessage = string(R.string.cancel),
        clicks = this@TimerFragment
    )

    private fun startProgressAnimation(duration: Long = MILLI_IN_SECS_L) {
        progressAnimation = ObjectAnimator.ofInt(
            binding?.fragmentTimerProgressCircle,
            PROGRESS_BAR_PROPERTY,
            screen.progress.get(),
            0
        )
        progressAnimation?.interpolator = linearInterpolator
        progressAnimation?.duration = duration
        progressAnimation?.start()
    }

    override fun onClick(dialog: DialogInterface?, button: Int) {
        if (button == DialogInterface.BUTTON_POSITIVE) viewModel.exit()
        dialog?.dismiss()
    }
}

data class TimerScreen(
    val timerState: ObservableField<TimerState> = ObservableField(TimerState.STOPPED),
    val timeRemaining: ObservableField<String> = ObservableField(""),
    val progress: ObservableInt = ObservableInt(0),
    var intervals: Flow<@JvmSuppressWildcards List<IntervalItemScreenBinding>> = emptyFlow(),
): BaseScreen() {

    fun navToHome() = TimerFragmentDirections.actionTimerFragmentToHomeFragment()
}

@InstallIn(FragmentComponent::class)
@Module
class TimerModule {

    @FragmentScoped
    @Provides
    fun provideInterpolator() = LinearInterpolator()
}