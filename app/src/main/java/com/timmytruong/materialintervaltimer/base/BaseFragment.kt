package com.timmytruong.materialintervaltimer.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import com.timmytruong.materialintervaltimer.ui.interfaces.ProgressBarInterface
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.Event

abstract class BaseFragment : Fragment(), ProgressBarInterface, OnClickListeners.IOBackPressed {
    abstract val baseViewModel: BaseViewModel

    abstract val eventObserver: Observer<Event<Any>>

    abstract fun bindView()

    protected open fun subscribeObservers() {
        baseViewModel.event.observe(viewLifecycleOwner, eventObserver)
    }

    protected fun handleUnknownError() {
        DesignUtils.showSnackbarError(
            contextView = requireView(),
            message = getString(R.string.somethingWentWrong)
        )
    }

    override fun toggleProgressBarVisibility(show: Boolean) {
        (activity as? MainActivity)?.toggleProgressBarVisibility(show = false)
    }

    override fun updateProgressBar(progress: Int) {
        try {
            val act = activity as? MainActivity
            act?.toggleProgressBarVisibility(show = true)
            act?.updateProgressBar(progress)
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    override fun onBackPressed(): Boolean = true
}