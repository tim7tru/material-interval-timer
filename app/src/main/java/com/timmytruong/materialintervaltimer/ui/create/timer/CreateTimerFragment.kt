package com.timmytruong.materialintervaltimer.ui.create.timer

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.utils.ObservableString
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment :
    BaseFragment<CreateTimerScreen, CreateTimerViewModel, FragmentCreateTimerBinding>() {

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    @Inject
    override lateinit var screen: CreateTimerScreen

    override val layoutId: Int = R.layout.fragment_create_timer

    override val name: String = this.name()

    override val viewModel: CreateTimerViewModel by viewModels()

    override val hasBackPress: Boolean = false

    private val args: CreateTimerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchCurrentTimer(args.clearViewModel)
    }

    override fun onStart() {
        super.onStart()
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
    var intervals: List<IntervalItem> = emptyList(),
    val timerTitle: ObservableString = ObservableString(""),
    val timerIntervalCount: ObservableInt = ObservableInt(0),
    val timerSelectedSound: ObservableString = ObservableString("None"),
    val timerIsSaved: ObservableBoolean = ObservableBoolean(false),
    val timerIsRepeated: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToTimer(id: Int) = CreateTimerFragmentDirections.toTimer(id)

    fun navToAddInterval() = CreateTimerFragmentDirections.toCreateInterval(clearStores = true)

    fun navToSoundBottomSheet(id: Int) = CreateTimerFragmentDirections.toSounds(soundId = id)
}