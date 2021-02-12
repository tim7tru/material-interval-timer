package com.timmytruong.materialintervaltimer.ui.home.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerActionBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.home.HomeClicks
import com.timmytruong.materialintervaltimer.ui.home.HomeViewModel
import com.timmytruong.materialintervaltimer.ui.home.fragments.HomeFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TimerActionBottomSheet : BottomSheetDialogFragment(), HomeClicks.BottomSheet {

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private lateinit var binding: FragmentTimerActionBottomSheetBinding

    private var timerId: Int = -1

    private val args: TimerActionBottomSheetArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_timer_action_bottom_sheet,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkArguments()
        bindView()
    }

    override fun onStartClicked(view: View) {
        val action = TimerActionBottomSheetDirections.actionTimerActionBottomSheetToTimerFragment()
        action.timerId = timerId
        findNavController().navigate(action)
        dismiss()
    }

    override fun onFavouriteClicked(view: View) {
        val save = binding.isFavourited
        save?.let { homeViewModel.onFavouritedClicked(id = timerId, isFavourited = !it) }
        dismiss()
    }

    override fun onDeleteClicked(view: View) {
        homeViewModel.onDeleteClicked(id = timerId)
        dismiss()
    }

    private fun bindView() {
        binding.onClick = this
    }

    private fun checkArguments() {
        timerId = args.timerId
        binding.isFavourited = args.isFavourited
    }
}