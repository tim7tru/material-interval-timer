package com.timmytruong.materialintervaltimer.ui.create.interval.time

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_FULL
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalTimeFragment :
    BaseFragment<CreateIntervalTimeScreen, CreateIntervalTimeViewModel, FragmentCreateIntervalTimeBinding>() {

    @Inject
    override lateinit var screen: CreateIntervalTimeScreen

    override val name: String = this.name()

    override val layoutId: Int = R.layout.fragment_create_interval_time

    override val viewModel: CreateIntervalTimeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    override fun onResume() {
        super.onResume()
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
): BaseScreen() {

    fun navBackToCreateTimer() = CreateIntervalTimeFragmentDirections
        .actionCreateIntervalTimeFragmentToCreateTimerFragment(false)
}