package com.timmytruong.materialintervaltimer.ui.create.interval

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.viewModels
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearStore()
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

data class CreateIntervalScreen(
    val enableIcon: ObservableBoolean = ObservableBoolean(false),
    val intervalIconTag: ObservableField<String> = ObservableField(""),
    val intervalTitle: ObservableField<String> = ObservableField("")
) : BaseScreen() {

    fun nextAction() =
        CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
}