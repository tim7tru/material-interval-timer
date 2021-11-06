package com.timmytruong.materialintervaltimer.ui.base

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
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

abstract class BaseBottomSheet<Screen : BaseScreen, ViewModel : BaseViewModel, Binding : ViewDataBinding> :
    BottomSheetDialogFragment(),
    BaseObserver<ViewModel> {

    abstract val screen: Screen

    abstract val layoutId: Int

    protected var binding: Binding? = null

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    @Inject lateinit var popUpProvider: PopUpProvider

    abstract fun bindView()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflate(inflater, layoutId, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSuspending {
            viewModel.navigateFlow.onEach(::navigationHandler).launchIn(it)
            viewModel.eventFlow.onEach(::eventHandler).launchIn(it)
        }
    }

    override fun onResume() {
        super.onResume()
        bindView()
    }

    override fun onPause() {
        uiStateJobs.forEach { it.cancel() }
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun eventHandler(event: Event) {
        when (event) {
            Event.BottomSheet.Dismiss -> close()
            is Event.Error.Unknown ->  popUpProvider.showErrorSnackbar(v, R.string.somethingWentWrong)
            else -> {}
        }
    }

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    protected fun close() = findNavController().popBackStack()

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(viewLifecycleOwner.lifecycleScope.launchWhenStarted(block))
}