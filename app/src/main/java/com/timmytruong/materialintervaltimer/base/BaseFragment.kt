package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.BackPress
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.events.ErrorHandler
import com.timmytruong.materialintervaltimer.utils.events.UNKNOWN_ERROR
import com.timmytruong.materialintervaltimer.utils.showSnackbarError
import com.timmytruong.materialintervaltimer.utils.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseFragment<Screen : BaseScreen, ViewModel : BaseViewModel, Binding : ViewDataBinding> :
    Fragment(), BaseObserver<ViewModel>, ErrorHandler, ProgressBar, BackPress {

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    protected var binding: Binding? = null

    abstract val screen: Screen

    abstract val name: String

    abstract val layoutId: Int

    abstract fun bindView()

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    override val eventFlowHandler: suspend (Pair<String, Any>) -> Unit = {
        when (it.first) {
            UNKNOWN_ERROR -> handleUnknownError()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSuspending {
            viewModel.navigateFlow.onEach(::navigationHandler).launchIn(it)
            viewModel.eventFlow.onEach(eventFlowHandler).launchIn(it)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        screen.screenName = name
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    override fun onStop() {
        uiStateJobs.forEach { it.cancel() }
        super.onStop()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun backPress() {}

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    override fun handleUnknownError() = showSnackbarError(v, string(R.string.somethingWentWrong))

    override fun updateProgressBar(progress: Int, show: Boolean) = (activity as? MainActivity)
        ?.updateProgressBar(progress = progress, show = show) ?: Unit

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(lifecycleScope.launchWhenStarted(block))
}