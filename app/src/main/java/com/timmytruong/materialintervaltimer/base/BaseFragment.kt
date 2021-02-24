package com.timmytruong.materialintervaltimer.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.BackButton
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.utils.enums.ErrorType
import com.timmytruong.materialintervaltimer.utils.events.ErrorHandler
import com.timmytruong.materialintervaltimer.utils.events.Event

abstract class BaseFragment<B : ViewDataBinding> : Fragment(), ErrorHandler, ProgressBar,
    BackButton, BaseObserver {

    abstract fun bindView()

    abstract override val eventObserver: Observer<Event<Any>>

    abstract val layoutId: Int

    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun handleUnknownError() {
        DesignUtils.showSnackbarError(
            contextView = requireView(),
            message = getString(R.string.somethingWentWrong)
        )
    }

    override fun handleQualifierError(qualifier: String) {}

    override fun handleInputError(error: ErrorType) {}

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