package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.inflate
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.utils.events.ErrorHandler
import com.timmytruong.materialintervaltimer.utils.showSnackbarError
import com.timmytruong.materialintervaltimer.utils.string
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

const val DISMISS = "dismiss"

abstract class BaseBottomSheet<Screen : BaseScreen, ViewModel : BaseViewModel, Binding : ViewDataBinding> :
    BottomSheetDialogFragment(),
    BaseObserver<ViewModel>,
    ErrorHandler {

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    protected var binding: Binding? = null

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    abstract val screen: Screen

    abstract val name: String

    abstract val layoutId: Int

    abstract fun bindView()

    override val eventFlowHandler: suspend (Pair<String, Any>) -> Unit = {
        when (it.first) {
            DISMISS -> close()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, layoutId, container, false)
        return binding!!.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        screen.screenName = name
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSuspending {
            viewModel.navigateFlow.onEach(::navigationHandler).launchIn(it)
            viewModel.eventFlow.onEach(eventFlowHandler).launchIn(it)
        }
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    override fun onPause() {
        super.onPause()
        uiStateJobs.forEach { it.cancel() }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun handleUnknownError() = showSnackbarError(v, string(R.string.somethingWentWrong))

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    protected fun close() = findNavController().popBackStack()

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(lifecycleScope.launchWhenStarted(block))
}