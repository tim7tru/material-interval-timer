package com.timmytruong.materialintervaltimer.ui.createtimer.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseObserver
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentIntervalSoundsBottomSheetBinding
import com.timmytruong.materialintervaltimer.ui.createtimer.CLOSE_SOUNDS
import com.timmytruong.materialintervaltimer.ui.createtimer.CreateTimerViewModel
import com.timmytruong.materialintervaltimer.ui.createtimer.adapters.IntervalSoundsAdapter
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IntervalSoundsBottomSheet : BottomSheetDialogFragment(), BaseObserver {

    @Inject
    lateinit var intervalSoundsAdapter: IntervalSoundsAdapter

    @Inject
    lateinit var createTimerViewModel: CreateTimerViewModel

    private lateinit var binding: FragmentIntervalSoundsBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        subscribeObservers()
        binding.fragmentIntervalsSoundsBottomSheetRecycler.adapter = intervalSoundsAdapter
        binding.viewModel = createTimerViewModel
    }

    override val baseViewModel: BaseViewModel
        get() = createTimerViewModel

    override val eventHandler: (Pair<String, Any>) -> Unit = {
        when (it.first) {
            CLOSE_SOUNDS -> dismiss()
        }
    }

    override val eventObserver: Observer<Event<Pair<String, Any>>> by lazy {
        Observer { event ->
            handleEvent(event, eventHandler)
        }
    }

    override fun subscribeObservers() {
        baseViewModel.event.observe(viewLifecycleOwner, eventObserver)
    }
}