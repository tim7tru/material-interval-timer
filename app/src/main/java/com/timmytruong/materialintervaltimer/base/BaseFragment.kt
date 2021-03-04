package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.ErrorHandler
import com.timmytruong.materialintervaltimer.utils.events.Event

abstract class BaseFragment<B : ViewDataBinding> : Fragment(), ErrorHandler, ProgressBar,
    BaseObserver {

    abstract override val eventHandler: (Pair<String, Any>) -> Unit

    abstract val layoutId: Int

    abstract fun bindView()

    override val eventObserver: Observer<Event<Pair<String, Any>>> by lazy {
        Observer {
            handleEvent(it, eventHandler)
        }
    }

    protected lateinit var binding: B

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    protected fun navigate(action: NavDirections) = findNavController().navigate(action)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun handleUnknownError() {
        DesignUtils.showSnackbarError(
            contextView = v,
            message = getString(R.string.somethingWentWrong)
        )
    }

    override fun handleEmptyError() {}

    override fun handleInputError() {}

    override fun toggleProgressBarVisibility(show: Boolean) {
        (activity as? MainActivity)?.toggleProgressBarVisibility(show = false)
    }

    override fun subscribeObservers() {
        baseViewModel.event.observe(viewLifecycleOwner, eventObserver)
    }

    override fun updateProgressBar(progress: Int) {
        toggleProgressBarVisibility(show = true)
        (activity as? MainActivity)?.updateProgressBar(progress)
    }
}