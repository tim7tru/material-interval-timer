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

    private val favoriteColor by lazy { resources.color(R.color.colorGolden) }

    private val unfavoriteColor by lazy { resources.color(R.color.colorSecondaryDark) }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimer(args.timerId)
    }

    override fun bindView() = binding?.apply {
        start.setOnClickListener { viewModel.onStartClicked() }
        favorite.setOnClickListener { viewModel.onFavoriteClicked() }
        delete.setOnClickListener { viewModel.onDeleteClicked() }
    }

    override fun eventHandler(event: Event) {
        when(event) {
            is Event.BottomSheet.TimerAction.ToastMessage -> toastAndDismiss(event.message)
            else -> { /** No op **/ }
        }
    }

    private fun toastAndDismiss(message: Int) {
        popUpProvider.showToast(message)
        close()
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.favorite.onEach {
            favorite.setTextColor(if (it) favoriteColor else unfavoriteColor)
            favorite.text = resources.string(if (it) R.string.unfavorite else R.string.favorite)
            favorite.setRippleColorResource(if (it) R.color.colorGoldenAccent else R.color.colorSecondaryAccent)
            favorite.setIconTintResource(if (it) R.color.colorGolden else R.color.colorSecondaryDark)
        }.launchIn(scope)
    }
}

@Open
@ActivityRetainedScoped
class TimerActionDirections @Inject constructor() {
    fun toTimer(id: Int) = TimerActionBottomSheetDirections.toTimer(id)
}