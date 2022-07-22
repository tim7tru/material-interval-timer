package com.timmytruong.materialintervaltimer.ui.create.interval

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.create.interval.grid.IconGridAdapter
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.extensions.HIDE
import com.timmytruong.materialintervaltimer.utils.extensions.SHOW
import com.timmytruong.materialintervaltimer.utils.extensions.onTextChanged
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

private const val PROGRESS_ZERO = 0
private const val PROGRESS_HALF = 50

@AndroidEntryPoint
class CreateIntervalFragment : BaseFragment<CreateIntervalViewModel, FragmentCreateIntervalBinding>(
    FragmentCreateIntervalBinding::inflate
) {

    @Inject
    lateinit var gridAdapter: IconGridAdapter

    override val viewModel: CreateIntervalViewModel by viewModels()

    override val hasOptionsMenu: Boolean = false

    override val hasBackPress: Boolean = false

    override val fragmentTitle: Int = R.string.create_interval

    private val args: CreateIntervalFragmentArgs by navArgs()

    override fun onStart() {
        super.onStart()
        viewModel.fetchInterval(args.clearStores)
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar(PROGRESS_HALF)
    }

    override fun onPause() {
        updateProgressBar(PROGRESS_ZERO)
        super.onPause()
    }

    override fun bindView() = binding?.apply {
        titleInput.onTextChanged { viewModel.onTitleChanged(it) }
        enableContainer.setOnClickListener { viewModel.onEnabledToggled(!enableSwitch.isChecked) }
        next.setOnClickListener { viewModel.onNextClicked() }
        viewModel.onTitleChanged(titleInput.text.toString())
        bindNextButton(text = titleInput.text.toString())
        grid.layoutManager = GridLayoutManager(requireContext(), 4)
        grid.adapter = gridAdapter
    }

    override fun onDestroyView() {
        binding?.grid?.adapter = null
        super.onDestroyView()
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.enableIcon.onEach { bindEnabled(it) }.launchIn(scope)
        viewModel.title.onEach { bindNextButton(it) }.launchIn(scope)
        viewModel.icons.onEach { gridAdapter.addList(it) }.launchIn(scope)
    }

    private fun bindNextButton(text: String) = binding?.apply {
        next.isEnabled = text.isNotBlank()
        next.setBackgroundColor(
            if (text.isNotBlank()) resources.color(R.color.color_secondary_dark)
            else resources.color(R.color.color_gray)
        )
    }

    private fun bindEnabled(isEnabled: Boolean) = binding?.apply {
        grid.visibility = if (isEnabled) SHOW else HIDE
        enableSwitch.isChecked = isEnabled
    }
}

@Open
@ActivityRetainedScoped
class CreateIntervalDirections @Inject constructor() {
    fun navToTime() = CreateIntervalFragmentDirections.toTime()
}