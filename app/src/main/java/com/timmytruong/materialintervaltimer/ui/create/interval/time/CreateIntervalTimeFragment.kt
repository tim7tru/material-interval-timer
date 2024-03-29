package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.data.util.Open
import com.timmytruong.materialintervaltimer.utils.extensions.color
import com.timmytruong.materialintervaltimer.utils.extensions.toInputTime
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val PROGRESS_FULL = 100

@AndroidEntryPoint
class CreateIntervalTimeFragment : BaseFragment<CreateIntervalTimeViewModel, FragmentCreateIntervalTimeBinding>(
    FragmentCreateIntervalTimeBinding::inflate
) {

    override val viewModel: CreateIntervalTimeViewModel by viewModels()

    override val hasBackPress: Boolean = false

    override val hasOptionsMenu: Boolean = false

    override val fragmentTitle: Int = R.string.create_interval

    override fun onBackPressed() = viewModel.backPressed()

    override fun onStart() {
        super.onStart()
        updateProgressBar(PROGRESS_FULL)
    }

    override fun bindView() = binding?.apply {
        delete.setOnClickListener { viewModel.removeFromTime() }
        next.setOnClickListener { viewModel.addInterval() }
        one.setOnClickListener { viewModel.addToTime(1) }
        two.setOnClickListener { viewModel.addToTime(2) }
        three.setOnClickListener { viewModel.addToTime(3) }
        four.setOnClickListener { viewModel.addToTime(4) }
        five.setOnClickListener { viewModel.addToTime(5) }
        six.setOnClickListener { viewModel.addToTime(6) }
        seven.setOnClickListener { viewModel.addToTime(7) }
        eight.setOnClickListener { viewModel.addToTime(8) }
        nine.setOnClickListener { viewModel.addToTime(9) }
        zero.setOnClickListener { viewModel.addToTime(0) }
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.time.onEach { bindTime(it) }.launchIn(scope)
    }

    private fun bindTime(input: String) = binding?.apply {
        time.text = input.toInputTime(root.context.getString(R.string.input_time_format))

        time.setTextColor(
            if (input.isNotEmpty()) root.context.color(R.color.color_secondary)
            else root.context.color(R.color.color_primary_dark)
        )
        next.setBackgroundColor(
            if (input.isNotEmpty()) root.context.color(R.color.color_secondary_dark)
            else root.context.color(R.color.color_gray)
        )

        next.isClickable = input.isNotEmpty()
        delete.isEnabled = input.isNotEmpty()
        delete.imageTintList = ContextCompat.getColorStateList(root.context, R.color.enabled_color_list)
    }
}

@Open
@ActivityRetainedScoped
class CreateIntervalTimeDirections @Inject constructor() {
    fun navToCreateTimer() = CreateIntervalTimeFragmentDirections.toCreateTimer(clearViewModel = false)
    fun navToCreateInterval() = CreateIntervalTimeFragmentDirections.toCreateInterval(clearStores = false)
}