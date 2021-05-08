package com.timmytruong.materialintervaltimer.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.MITSnackbar
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

abstract class BaseFragment<Screen : BaseScreen, ViewModel : BaseViewModel, Binding : ViewDataBinding> :
    Fragment(), BaseObserver<ViewModel>, ProgressBar {

    protected val ctx: Context by lazy { requireContext() }

    protected val v: View by lazy { requireView() }

    protected lateinit var binding: Binding

    abstract val screen: Screen

    @Inject
    lateinit var resources: ResourceProvider

    @Inject
    lateinit var snackbar: MITSnackbar

    abstract val name: String

    abstract val layoutId: Int

    abstract fun bindView()

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screen.screenName = name
    }

    override fun onStart() {
        super.onStart()
        bindView()
        startSuspending {
            viewModel.navigateFlow.onEach(::navigationHandler).launchIn(it)
            viewModel.eventFlow.onEach(::eventHandler).launchIn(it)
        }
    }

    override fun onPause() {
        super.onPause()
        val imm = ContextCompat.getSystemService(v.context, InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun onStop() {
        uiStateJobs.forEach { it.cancel() }
        super.onStop()
    }

    override fun onDestroyView() {
        binding.unbind()
        super.onDestroyView()
    }

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    override fun eventHandler(event: Pair<String, Any>) {
        when (event.first) {
            UNKNOWN_ERROR -> snackbar.showError(view = v, messageId = R.string.somethingWentWrong)
        }
    }

    override fun updateProgressBar(progress: Int, show: Boolean) = (activity as MainActivity)
        .updateProgressBar(progress = progress, show = show)

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(lifecycleScope.launchWhenStarted(block))
}