package com.timmytruong.materialintervaltimer.ui.reusable.action

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerActionBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.Open
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TimerActionBottomSheet : BaseBottomSheet<TimerActionViewModel, FragmentTimerActionBottomSheetBinding>(
    FragmentTimerActionBottomSheetBinding::inflate
) {

    override val viewModel: TimerActionViewModel by viewModels()

    private val args: TimerActionBottomSheetArgs by navArgs()

    private val favoriteColor by lazy { resources.color(R.color.color_golden) }

    private val unfavoriteColor by lazy { resources.color(R.color.color_secondary_dark) }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimer(args.timerId)
    }

    override fun bindView() = binding?.apply {
        start.setOnClickListener { viewModel.onStartClicked() }
        favorite.setOnClickListener { viewModel.onFavoriteClicked() }
        delete.setOnClickListener { viewModel.onDeleteClicked() }
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.favorite.onEach {
            favorite.setTextColor(if (it) favoriteColor else unfavoriteColor)
            favorite.text = resources.string(if (it) R.string.unfavorite else R.string.favorite)
            favorite.setRippleColorResource(if (it) R.color.color_golden_accent else R.color.color_secondary_accent)
            favorite.setIconTintResource(if (it) R.color.color_golden else R.color.color_secondary_dark)
        }.launchIn(scope)
    }
}

@Open
@ActivityRetainedScoped
class TimerActionDirections @Inject constructor() {
    fun toTimer(id: Int) = TimerActionBottomSheetDirections.toTimer(id)
}