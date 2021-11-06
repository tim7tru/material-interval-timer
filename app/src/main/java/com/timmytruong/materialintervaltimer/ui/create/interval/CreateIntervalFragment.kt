package com.timmytruong.materialintervaltimer.ui.create.interval

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.utils.ObservableString
import com.timmytruong.materialintervaltimer.utils.Open
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observe()
        if (args.clearStores) { viewModel.clearStore() }
    }

    override fun onStart() {
        super.onStart()
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

@Open
data class CreateIntervalScreen(
    val enableIcon: ObservableBoolean = ObservableBoolean(false),
    val intervalIconTag: ObservableString = ObservableString(""),
    val intervalTitle: ObservableString = ObservableString("")
) : BaseScreen() {

    fun nextAction() =
        CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
}