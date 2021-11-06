package com.timmytruong.materialintervaltimer.ui.create.timer.sounds

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.utils.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
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
        startSuspending { screen.list.collect(intervalSoundsAdapter::addList) }
    }

    override fun bindView() {
        binding?.close?.setOnClickListener { viewModel.dismissSoundsBottomSheet() }
        binding?.recycler?.adapter = intervalSoundsAdapter
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
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
    var list: Flow<@JvmSuppressWildcards List<IntervalSoundItem>> = emptyFlow()
) : BaseScreen()