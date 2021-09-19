package com.timmytruong.materialintervaltimer.ui.create.interval

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.utils.name
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val PROGRESS_ZERO = 0
private const val PROGRESS_HALF = 50

@AndroidEntryPoint
class CreateIntervalFragment :
    BaseFragment<CreateIntervalScreen, CreateIntervalViewModel, FragmentCreateIntervalBinding>() {

    @Inject
    override lateinit var screen: CreateIntervalScreen

    override val name: String = this.name()

    override val viewModel: CreateIntervalViewModel by viewModels()

    override val layoutId: Int = R.layout.fragment_create_interval

    private val args: CreateIntervalFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        if (args.clearStores) { viewModel.clearStore() }
        viewModel.fetchInterval()
        updateProgressBar(PROGRESS_HALF)
    }

    override fun onPause() {
        updateProgressBar(PROGRESS_ZERO)
        super.onPause()
    }

    override fun bindView() {
        binding?.screen = screen
        binding?.viewModel = viewModel
    }
}

data class CreateIntervalScreen(
    val enableIcon: ObservableBoolean = ObservableBoolean(false),
    val intervalIconTag: ObservableField<String> = ObservableField(""),
    val intervalTitle: ObservableField<String> = ObservableField("")
) : BaseScreen() {

    fun nextAction() =
        CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
}