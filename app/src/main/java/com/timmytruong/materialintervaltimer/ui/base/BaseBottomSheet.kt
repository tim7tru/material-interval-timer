package com.timmytruong.materialintervaltimer.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.extensions.Inflater
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

abstract class BaseBottomSheet<ViewModel : BaseViewModel, Binding : ViewBinding>(
    private val bindingInflater: Inflater<Binding>
) : BottomSheetDialogFragment(), BaseObserver<ViewModel> {

    protected var binding: Binding? = null

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    @Inject lateinit var popUpProvider: PopUpProvider

    @Inject lateinit var resources: ResourceProvider

    abstract fun bindView(): Binding?

    abstract suspend fun bindState(scope: CoroutineScope): Binding?

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = bindingInflater.invoke(inflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        bindView()
        bindState()
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
            is Event.Navigate -> navigationHandler(event.directions)
            is Event.Error.Unknown ->  popUpProvider.showErrorSnackbar(v, R.string.something_went_wrong)
            else -> {}
        }
    }

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    protected fun close() = findNavController().popBackStack()

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(lifecycleScope.launchWhenStarted(block))

    private fun bindState() = uiStateJobs.add(viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.eventFlow.onEach(::eventHandler).launchIn(this)
        bindState(this)
    })
}