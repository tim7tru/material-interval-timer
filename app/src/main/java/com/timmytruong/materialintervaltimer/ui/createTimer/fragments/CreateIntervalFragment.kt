package com.timmytruong.materialintervaltimer.ui.createTimer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalBinding
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.createTimer.CreateTimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_interval.*
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalFragment : Fragment(), OnClickListeners.CreateIntervalFrag {

    private lateinit var binding: FragmentCreateIntervalBinding

    @Inject lateinit var createTimerViewModel: CreateTimerViewModel

    private val iconSwitchObserver = Observer<Boolean> { binding.iconChecked = it }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_interval, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        updateProgressBar()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateProgressBar()
        subscribeObservers()
        Log.d("ViewModel", "CreateViewModel $createTimerViewModel")
        binding.onClick = this
    }

    private fun subscribeObservers() {
        createTimerViewModel.intervalIconChecked.observe(viewLifecycleOwner, iconSwitchObserver)
    }

    private fun updateProgressBar() {
        try {
            val act = activity as MainActivity
            act.toggleProgressBarVisibility(show = true)
            act.updateProgressBar(50)
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    override fun onContinueClicked(view: View) {
        if (fragmentCreateIntervalTitleInput.text.toString().isBlank()) {
            fragmentCreateIntervalTitleLayout.error = getString(R.string.noTitleError)
            return
        }

        createTimerViewModel.setIntervalTitle(fragmentCreateIntervalTitleInput.text.toString())
        val action =
            CreateIntervalFragmentDirections.actionCreateIntervalFragmentToCreateIntervalTimeFragment()
        Navigation.findNavController(view).navigate(action)
    }

    override fun onIconSwitchClicked(view: View) {
        createTimerViewModel.setIntervalIconChecked(checked = !fragmentCreateIntervalIconSwitch.isChecked)
    }

    override fun onIconClicked(view: View) {
        createTimerViewModel.setIntervalIcon(
            when (view.tag) {
                getString(R.string.fitnessTag) -> { R.drawable.ic_fitness_center }
                getString(R.string.personTag) -> { R.drawable.ic_accessibility_24px }
                getString(R.string.speedTag) -> { R.drawable.ic_speed_24px }
                getString(R.string.cafeTag) -> { R.drawable.ic_local_cafe_24px }
                getString(R.string.androidTag) -> { R.drawable.ic_android_24px }
                getString(R.string.musicTag) -> { R.drawable.ic_audiotrack_24px }
                getString(R.string.worldTag) -> { R.drawable.ic_language_24px }
                getString(R.string.emailTag) -> { R.drawable.ic_email_24px }
                getString(R.string.ecoTag) -> { R.drawable.ic_eco_24px}
                getString(R.string.phoneTag) -> { R.drawable.ic_call_24px }
                getString(R.string.playTag) -> { R.drawable.ic_play}
                getString(R.string.pauseTag) -> { R.drawable.ic_pause_24px }
                else -> { 0 }
            }
        )
        binding.checkedIconIndex = when (view.tag) {
            getString(R.string.fitnessTag) -> 0
            getString(R.string.personTag) -> 1
            getString(R.string.speedTag) -> 2
            getString(R.string.cafeTag) -> 3
            getString(R.string.androidTag) -> 4
            getString(R.string.musicTag) -> 5
            getString(R.string.worldTag) -> 6
            getString(R.string.emailTag) -> 7
            getString(R.string.ecoTag) -> 8
            getString(R.string.phoneTag) -> 9
            getString(R.string.playTag) -> 10
            getString(R.string.pauseTag) -> 11
            else -> -1
        }
    }
}