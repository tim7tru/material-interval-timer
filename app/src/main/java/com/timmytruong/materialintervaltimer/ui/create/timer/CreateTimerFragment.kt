package com.timmytruong.materialintervaltimer.ui.create.timer

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateTimerBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItem
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.IntervalItemAdapter
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.extensions.hide
import com.timmytruong.materialintervaltimer.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CreateTimerFragment : BaseFragment<CreateTimerViewModel, FragmentCreateTimerBinding>(
    FragmentCreateTimerBinding::inflate
) {

    @Inject
    lateinit var intervalAdapter: IntervalItemAdapter

    override val viewModel: CreateTimerViewModel by viewModels()

    override val hasBackPress: Boolean = false

    override val hasOptionsMenu: Boolean = false

    override val fragmentTitle: Int = R.string.create_timer

    private val args: CreateTimerFragmentArgs by navArgs()

    override fun onResume() {
        super.onResume()
        viewModel.fetchCurrentTimer(args.clearViewModel)
    }

    override fun onPause() {
        super.onPause()
        viewModel.setTimerTitle(binding?.title?.text.toString())
    }

    override fun bindView() = binding?.apply {
        updateProgressBar(progress = 0, show = false)
        bindClicks()
        recycler.adapter = intervalAdapter
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.intervals.onEach { bindAdapter(it) }.launchIn(scope)
        viewModel.title.onEach { title.setText(it) }.launchIn(scope)
        viewModel.intervals.onEach { bindAdapter(it) }.launchIn(scope)
        viewModel.shouldRepeat.onEach { repeatSwitch.isChecked = it }.launchIn(scope)
        viewModel.sound.onEach { sound.text = it }.launchIn(scope)
        viewModel.isSaved.onEach { saveSwitch.isChecked = it }.launchIn(scope)
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
        super.onDestroyView()
    }

    private fun bindClicks() = binding?.apply {
        addTask.setOnClickListener { viewModel.onGoToAddIntervalClicked() }
        repeatContainer.setOnClickListener { viewModel.setRepeat(!repeatSwitch.isChecked) }
        soundContainer.setOnClickListener { viewModel.onGoToSoundsBottomSheet() }
        saveContainer.setOnClickListener { viewModel.setFavorite(!saveSwitch.isChecked) }
        next.setOnClickListener { viewModel.validateTimer(title.text.toString()) }
    }

    private fun bindAdapter(list: List<IntervalItem>) {
        checkEmptyState(list.isEmpty())
        intervalAdapter.addList(list)
    }

    private fun checkEmptyState(isEmpty: Boolean) = binding?.apply {
        if (isEmpty) emptyState.show() else emptyState.hide()
        if (isEmpty) recycler.hide() else recycler.show()
        next.isEnabled = !isEmpty
        next.setBackgroundColor(
            if (isEmpty) resources.color(R.color.colorGray)
            else resources.color(R.color.colorSecondaryDark)
        )
    }
}

@Open
@ActivityRetainedScoped
class CreateTimerDirections @Inject constructor() {
    fun toTimer(id: Int) = CreateTimerFragmentDirections.toTimer(timerId = id)
    fun toSounds(id: Int) = CreateTimerFragmentDirections.toSounds(soundId = id)
    fun toCreateInterval() = CreateTimerFragmentDirections.toCreateInterval(clearStores = true)
}