package com.timmytruong.materialintervaltimer.ui.create.timer.views

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundsAdapter
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntervalSoundsBottomSheet :
    BaseBottomSheet<IntervalSoundsBottomSheetScreen, IntervalSoundsViewModel, FragmentIntervalSoundsBottomSheetBinding>() {

    @Inject
    lateinit var intervalSoundsAdapter: IntervalSoundsAdapter

    @Inject
    override lateinit var screen: IntervalSoundsBottomSheetScreen

    override val viewModel: IntervalSoundsViewModel by viewModels()

    override val layoutId: Int get() = R.layout.fragment_interval_sounds_bottom_sheet

    private val args: IntervalSoundsBottomSheetArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchSounds(args.soundId)
        bindView()
    }

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.fragmentIntervalsSoundsBottomSheetRecycler?.adapter = intervalSoundsAdapter
        intervalSoundsAdapter.addList(screen.list)
    }

    override fun onDestroyView() {
        binding?.fragmentIntervalsSoundsBottomSheetRecycler?.adapter = null
        super.onDestroyView()
    }

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.PlaySound -> MediaPlayer.create(requireContext(), event.id).start()
            else -> super.eventHandler(event)
        }
    }
}

data class IntervalSoundsBottomSheetScreen(
    var list: List<IntervalSoundScreenBinding> = emptyList()
) : BaseScreen()