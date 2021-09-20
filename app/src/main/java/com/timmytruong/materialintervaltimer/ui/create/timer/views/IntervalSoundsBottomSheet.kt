package com.timmytruong.materialintervaltimer.ui.create.timer.views

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundScreenBinding
import com.timmytruong.materialintervaltimer.ui.create.timer.adapters.IntervalSoundsAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        intervalSoundsAdapter.addList(screen.list)
    }

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.fragmentIntervalsSoundsBottomSheetRecycler?.adapter = intervalSoundsAdapter
    }

    override fun onDestroyView() {
        binding?.fragmentIntervalsSoundsBottomSheetRecycler?.adapter = null
        super.onDestroyView()
    }
}

data class IntervalSoundsBottomSheetScreen(
    var list: List<IntervalSoundScreenBinding> = emptyList()
) : BaseScreen()