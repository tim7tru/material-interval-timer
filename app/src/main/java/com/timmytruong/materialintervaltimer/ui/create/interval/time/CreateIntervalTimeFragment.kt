package com.timmytruong.materialintervaltimer.ui.create.interval.time

import android.content.Context
import androidx.activity.addCallback
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import com.timmytruong.materialintervaltimer.utils.Open
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

    override val hasBackPress: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            viewModel.backPressed()
        }
    }

    override fun onStart() {
        super.onStart()

        updateProgressBar(PROGRESS_FULL)
    }

    override fun bindView() {
        binding?.screen = screen
        binding?.viewModel = viewModel
    }
}

@Open
data class CreateIntervalTimeScreen(
    val intervalDisplayTime: ObservableString = ObservableString("00h 00m 00s"),
    val intervalTimeLengthValidity: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToCreateTimer() = CreateIntervalTimeFragmentDirections.toCreateTimer(clearViewModel = false)

    fun navToCreateInterval() = CreateIntervalTimeFragmentDirections.toCreateInterval(clearStores = false)
}