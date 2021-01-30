package com.timmytruong.materialintervaltimer.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.BackButton
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.events.Event

abstract class BaseFragment : Fragment(), ProgressBar, BackButton, BaseObserver {

    abstract fun bindView()

    abstract override val eventObserver: Observer<Event<Any>>

    protected fun handleUnknownError() {
        DesignUtils.showSnackbarError(
            contextView = requireView(),
            message = getString(R.string.somethingWentWrong)
        )
    }

    override fun toggleProgressBarVisibility(show: Boolean) {
        (activity as? MainActivity)?.toggleProgressBarVisibility(show = false)
    }

    override fun subscribeObservers() {
        baseViewModel.event.observe(viewLifecycleOwner, eventObserver)
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