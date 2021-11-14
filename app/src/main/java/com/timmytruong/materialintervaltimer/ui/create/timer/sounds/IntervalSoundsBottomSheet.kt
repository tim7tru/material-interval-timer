package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import android.media.MediaPlayer
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class IntervalSoundsBottomSheet : BaseBottomSheet<IntervalSoundsViewModel, FragmentIntervalSoundsBottomSheetBinding>(
    FragmentIntervalSoundsBottomSheetBinding::inflate
) {

    @Inject
    lateinit var intervalSoundsAdapter: IntervalSoundsAdapter

    override val viewModel: IntervalSoundsViewModel by viewModels()

    private val args: IntervalSoundsBottomSheetArgs by navArgs()

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.PlaySound -> MediaPlayer.create(ctx, event.id).start()
            else -> super.eventHandler(event)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchSounds(args.soundId)
    }

    override fun bindView() = binding?.apply {
        close.setOnClickListener { viewModel.dismissSoundsBottomSheet() }
        recycler.adapter = intervalSoundsAdapter
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
        super.onDestroyView()
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.soundsFlow.onEach { intervalSoundsAdapter.addList(it) }.launchIn(scope)
    }
}