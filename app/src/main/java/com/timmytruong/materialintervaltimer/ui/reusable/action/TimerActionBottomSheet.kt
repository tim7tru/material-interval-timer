package com.timmytruong.materialintervaltimer.ui.reusable.action

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerActionBottomSheetBinding
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.Open
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerActionBottomSheet :
    BaseBottomSheet<TimerActionBottomSheetScreen, TimerActionViewModel, FragmentTimerActionBottomSheetBinding>() {

    @Inject
    override lateinit var screen: TimerActionBottomSheetScreen

    override val layoutId: Int = R.layout.fragment_timer_action_bottom_sheet

    override val viewModel: TimerActionViewModel by viewModels()

    private val args: TimerActionBottomSheetArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchTimer(args.timerId)
    }

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.screen = screen
    }

    override fun eventHandler(event: Event) {
        when(event) {
            is Event.BottomSheet.TimerAction.ToastMessage -> toastAndDismiss(event.message)
            else -> { /** noop **/ }
        }
    }

    private fun toastAndDismiss(message: Int) {
        popUpProvider.showToast(message)
        close()
    }
}

@Open
data class TimerActionBottomSheetScreen(
    val timerId: ObservableInt = ObservableInt(-1),
    val isFavourite: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToTimer(id: Int) =
        TimerActionBottomSheetDirections.actionTimerActionBottomSheetToTimerFragment(id)
}