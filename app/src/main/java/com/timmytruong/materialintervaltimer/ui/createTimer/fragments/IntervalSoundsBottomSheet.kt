package com.timmytruong.materialintervaltimer.ui.createTimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.createTimer.adapters.IntervalSoundsAdapter
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.createTimer.CreateTimerViewModel
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IntervalSoundsBottomSheet @Inject constructor(
    private val createTimerViewModel: CreateTimerViewModel,
    private val intervalSoundsAdapter: IntervalSoundsAdapter
) : BottomSheetDialogFragment(), OnClickListeners.SoundsBottomSheet {

    private lateinit var binding: FragmentIntervalSoundsBottomSheetBinding

    private val cancelledSound = createTimerViewModel.getSelectedSound()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_interval_sounds_bottom_sheet,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSoundList()
        binding.onClick = this
    }

    private fun setSoundList() {
        binding.fragmentIntervalsSoundsBottomSheetRecycler.adapter = intervalSoundsAdapter
    }

    override fun onSoundClicked(view: View) {
        when ((view as MaterialButton).text.toString()) {
            getString(R.string.save) -> dismiss()
            else -> {
                createTimerViewModel.setSelectedSound(intervalSound = cancelledSound)
                dismiss()
            }
        }
    }
}