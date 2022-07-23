package com.timmytruong.materialintervaltimer.ui.reusable.action

import android.content.Context
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerActionBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseBottomSheet
import com.timmytruong.data.util.Open
import com.timmytruong.materialintervaltimer.utils.extensions.color
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
            val textColor = root.context.getTextColor(isFavorite = it)
            favorite.setTextColor(textColor)
            favorite.text = root.context.getText(isFavorite = it)
            favorite.setRippleColorResource(if (it) R.color.color_golden_accent else R.color.color_secondary_accent)
            favorite.setIconTintResource(if (it) R.color.color_golden else R.color.color_secondary_dark)
        }.launchIn(scope)
    }

    private fun Context.getTextColor(isFavorite: Boolean) = if (isFavorite) {
        color(R.color.color_golden)
    } else {
        color(R.color.color_secondary_dark)
    }

    private fun Context.getText(isFavorite: Boolean) = if (isFavorite) {
        getString(R.string.unfavorite)
    } else {
        getString(R.string.favorite)
    }
}

@Open
@ActivityRetainedScoped
class TimerActionDirections @Inject constructor() {
    fun toTimer(id: Int) = TimerActionBottomSheetDirections.toTimer(id)
}