package com.timmytruong.materialintervaltimer.ui.create.timer

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalItemScreenBinding
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment : BaseFragment<CreateTimerScreen, CreateTimerViewModel, FragmentCreateTimerBinding>() {

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    @Inject
    override lateinit var screen: CreateTimerScreen

    override val layoutId: Int = R.layout.fragment_create_timer

    override val name: String = this.name()

    override val viewModel: CreateTimerViewModel by viewModels()

    private val args: CreateTimerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.clearViewModel) viewModel.clearTimer()
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchCurrentTimer()
        updateProgressBar(progress = 0, show = false)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setTimerTitle(binding?.fragmentCreateTimerTitleInput?.text.toString())
    }

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.screen = screen
        binding?.fragmentCreateTimerTaskList?.adapter = intervalAdapter
        intervalAdapter.addList(screen.intervals)
    }

    override fun onDestroyView() {
        binding?.fragmentCreateTimerTaskList?.adapter = null
        super.onDestroyView()
    }
}

@Open
data class CreateTimerScreen(
    var intervals: List<IntervalItemScreenBinding> = listOf(),
    val timerTitle: ObservableField<String> = ObservableField(""),
    val timerIntervalCount: ObservableInt = ObservableInt(0),
    val timerSelectedSound: ObservableField<String> = ObservableField(""),
    val timerIsSaved: ObservableBoolean = ObservableBoolean(false),
    val timerIsRepeated: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToTimer(id: Int) =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToTimerFragment(id)
    fun navToAddInterval() =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToCreateIntervalFragment()
    fun navToSoundBottomSheet() =
        CreateTimerFragmentDirections.actionCreateTimerFragmentToIntervalSoundsBottomSheet()
}