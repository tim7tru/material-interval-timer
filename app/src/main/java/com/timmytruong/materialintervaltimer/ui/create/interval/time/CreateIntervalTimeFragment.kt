package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val PROGRESS_FULL = 100

@AndroidEntryPoint
class CreateIntervalTimeFragment :
    BaseFragment<CreateIntervalTimeScreen, CreateIntervalTimeViewModel, FragmentCreateIntervalTimeBinding>() {

    @Inject
    override lateinit var screen: CreateIntervalTimeScreen

    override val name: String = this.name()

    override val layoutId: Int = R.layout.fragment_create_interval_time

    override val viewModel: CreateIntervalTimeViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        updateProgressBar(PROGRESS_FULL)
        viewModel.fetchTime()
    }

    override fun bindView() {
        binding?.screen = screen
        binding?.viewModel = viewModel
    }
}

data class CreateIntervalTimeScreen(
    val intervalDisplayTime: ObservableField<String> = ObservableField(""),
    val intervalTimeLengthValidity: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navBackToCreateTimer() = CreateIntervalTimeFragmentDirections
        .actionCreateIntervalTimeFragmentToCreateTimerFragment(false)
}