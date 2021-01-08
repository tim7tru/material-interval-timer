package com.timmytruong.materialintervaltimer.base

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.interfaces.ProgressBarInterface
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType

abstract class BaseFragment : Fragment(), ProgressBarInterface {
    abstract val baseViewModel: BaseViewModel

    abstract val errorObserver: Observer<Event<ErrorType>>

    abstract fun bindView()

    open fun subscribeObservers() {
        baseViewModel.error.observe(viewLifecycleOwner, errorObserver)
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

    protected fun setOnBackPressed(callback: (view: View) -> Unit) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view?.let { callback.invoke(it) }
            }
        })
    }
}