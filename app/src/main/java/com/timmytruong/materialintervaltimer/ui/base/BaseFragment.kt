package com.timmytruong.materialintervaltimer.ui.base

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
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.BackPress
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

abstract class BaseFragment<
        Screen : BaseScreen,
        ViewModel : BaseViewModel,
        Binding : ViewDataBinding
> : Fragment(), BaseObserver<ViewModel>, ProgressBar, BackPress {

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    protected var binding: Binding? = null

    abstract val screen: Screen

    abstract val name: String

    abstract val layoutId: Int

    abstract val hasBackPress: Boolean

    @Inject lateinit var popUpProvider: PopUpProvider

    @Inject lateinit var resources: ResourceProvider

    abstract fun bindView()

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

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
        screen.name = name
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (hasBackPress) {
            requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed() }
        }
    }

    override fun onStart() {
        super.onStart()
        startSuspending {
            viewModel.navigateFlow.onEach(::navigationHandler).launchIn(it)
            viewModel.eventFlow.onEach(::eventHandler).launchIn(it)
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

    override fun onBackPressed() { /** No op **/ }

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.Error.Unknown ->  popUpProvider.showErrorSnackbar(v, R.string.somethingWentWrong)
            else -> { /** noop **/ }
        }
    }

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    override fun updateProgressBar(progress: Int, show: Boolean) =
        (activity as MainActivity).updateProgressBar(progress = progress, show = show)

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(viewLifecycleOwner.lifecycleScope.launchWhenStarted(block))
}