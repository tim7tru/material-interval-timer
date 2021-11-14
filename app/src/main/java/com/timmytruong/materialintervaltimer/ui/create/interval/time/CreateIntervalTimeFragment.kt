package com.timmytruong.materialintervaltimer.ui.create.interval.time

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.create.interval.time.grid.NumberGridAdapter
import com.timmytruong.materialintervaltimer.utils.Open
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

    @Inject
    lateinit var numbersAdapter: NumberGridAdapter

    override val viewModel: CreateIntervalTimeViewModel by viewModels()

    override val hasBackPress: Boolean = false

    override val hasOptionsMenu: Boolean = false

    override val fragmentTitle: Int = R.string.create_interval

    override fun onBackPressed() = viewModel.backPressed()

    override fun onStart() {
        super.onStart()
        updateProgressBar(PROGRESS_FULL)
        viewModel.createNumbers()
    }

    override fun bindView() = binding?.apply {
        delete.setOnClickListener { viewModel.removeFromTime() }
        next.setOnClickListener { viewModel.addInterval() }
        grid.adapter = numbersAdapter
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.time.onEach { bindTime(it) }.launchIn(scope)
        viewModel.numbers.onEach { numbersAdapter.addList(it) }.launchIn(scope)
    }

    private fun bindTime(input: String) = binding?.apply {
        time.text = input.toInputTime(resources)

        time.setTextColor(
            if (input.isNotEmpty()) resources.color(R.color.colorSecondary)
            else resources.color(R.color.colorPrimaryDark)
        )
        next.setBackgroundColor(
            if (input.isNotEmpty()) resources.color(R.color.colorSecondaryDark)
            else resources.color(R.color.colorGray)
        )

        next.isClickable = input.isNotEmpty()
        delete.isEnabled = input.isNotEmpty()
        delete.imageTintList = resources.colorList(R.color.enabled_color_list)
    }
}

@Open
@ActivityRetainedScoped
class CreateIntervalTimeDirections @Inject constructor() {
    fun navToCreateTimer() = CreateIntervalTimeFragmentDirections.toCreateTimer(clearViewModel = false)
    fun navToCreateInterval() = CreateIntervalTimeFragmentDirections.toCreateInterval(clearStores = false)
}