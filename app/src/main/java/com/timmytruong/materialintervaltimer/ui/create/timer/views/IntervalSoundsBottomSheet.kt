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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
        startSuspending {
            screen.list.collectLatest { intervalSoundsAdapter.addList(it) }
        }
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
    var list: Flow<@JvmSuppressWildcards List<IntervalSoundScreenBinding>> = emptyFlow()
) : BaseScreen()