package com.timmytruong.materialintervaltimer.ui.createTimer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.button.MaterialButton
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentCreateIntervalTimeBinding
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.createTimer.CreateTimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreateIntervalTimeFragment : Fragment(), OnClickListeners.CreateIntervalTimeFrag {

    private lateinit var binding: FragmentCreateIntervalTimeBinding

    @Inject lateinit var createTimerViewModel: CreateTimerViewModel

    private val timeObserver = Observer<String> {
        binding.time = DesignUtils.formatInputtedTime(time = it, format = getString(R.string.timeFormat))
        binding.length = it.length
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_interval_time, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.onClick = this
        binding.time = DesignUtils.formatInputtedTime(time = "", format = getString(R.string.timeFormat))

        subscribeObservers()

        updateProgressBar()
    }

    private fun subscribeObservers() {
        createTimerViewModel.intervalTime.observe(viewLifecycleOwner, timeObserver)
    }

    private fun updateProgressBar() {
        try {
            (activity as MainActivity).updateProgressBar(100)
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    override fun onNumberClicked(view: View) {
        try {
            createTimerViewModel.addToTime(addition = (view as MaterialButton).text.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackClicked(view: View) {
        createTimerViewModel.removeFromTime()
    }

    override fun onAddClicked(view: View) {
        createTimerViewModel.addInterval()
        val action =
            CreateIntervalTimeFragmentDirections.actionCreateIntervalTimeFragmentToCreateTimerFragment()
        action.clearViewModel = false
        Navigation.findNavController(view).navigate(action)
    }
}