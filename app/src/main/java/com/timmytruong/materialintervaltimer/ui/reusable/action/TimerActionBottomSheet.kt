package com.timmytruong.materialintervaltimer.ui.reusable.action

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseBottomSheet
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerActionBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.home.HomeFragment
import com.timmytruong.materialintervaltimer.ui.home.HomeFragmentDirections
import com.timmytruong.materialintervaltimer.ui.list.FavouritesFragment
import com.timmytruong.materialintervaltimer.ui.list.FavouritesFragmentDirections
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.name
import com.timmytruong.materialintervaltimer.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

internal const val FAVOURITE = "favourite"
internal const val DELETE = "delete"

@AndroidEntryPoint
class TimerActionBottomSheet :
    BaseBottomSheet<TimerActionBottomSheetScreen, TimerActionViewModel, FragmentTimerActionBottomSheetBinding>() {

    @Inject
    override lateinit var screen: TimerActionBottomSheetScreen

    override val layoutId: Int = R.layout.fragment_timer_action_bottom_sheet

    override val viewModel: TimerActionViewModel by viewModels()

    override fun bindView() {
        binding?.viewModel = viewModel
        binding?.screen = screen
    }

    override fun eventHandler(event: Pair<String, Any>) {
        when (event.first) {
            FAVOURITE -> toastAndDismiss(event.second as String)
            DELETE -> toastAndDismiss(event.second as String)
        }
    }

    private fun toastAndDismiss(message: String) {
        showToast(ctx, message)
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