package com.timmytruong.materialintervaltimer.ui.base

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.reusable.BackPress
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
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

abstract class BaseFragment<ViewModel : BaseViewModel, Binding: ViewBinding>(
    private val bindingInflater: Inflater<Binding>
) : Fragment(), BaseObserver<ViewModel>, ProgressBar, BackPress {

    protected var menu: Menu? = null

    protected var binding: Binding? = null

    abstract val fragmentTitle: Int

    abstract val hasBackPress: Boolean

    abstract val hasOptionsMenu: Boolean

    @Inject lateinit var popUpProvider: PopUpProvider

    @Inject lateinit var resources: ResourceProvider

    abstract fun bindView(): Binding?

    abstract suspend fun bindState(scope: CoroutineScope): Binding?

    override var uiStateJobs: ArrayList<Job> = arrayListOf()

    private val appBarMenu = R.menu.app_bar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(hasOptionsMenu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (hasOptionsMenu) {
            super.onCreateOptionsMenu(menu, inflater)
            inflater.inflate(appBarMenu, menu)
            this.menu = menu
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as MainActivity).supportActionBar?.title = resources.string(fragmentTitle)
        binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(binding?.root)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (hasBackPress) {
            requireActivity().onBackPressedDispatcher.addCallback(this) { onBackPressed() }
        }
    }

    override fun onStart() {
        super.onStart()
        bindState()
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

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.ToastMessage -> popUpProvider.showToast(event.message)
            is Event.Navigate -> navigateWith(event.directions)
            is Event.Error.Unknown -> popUpProvider.showErrorSnackbar(requireView(), R.string.somethingWentWrong)
            else -> super.eventHandler(event)
        }
    }

    private fun navigateWith(directions: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(directions.actionId)?.let { navigate(directions) }
    }

    override fun navigationHandler(action: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    override fun updateProgressBar(progress: Int, show: Boolean) =
        (activity as MainActivity).updateProgressBar(progress = progress, show = show)

    protected fun startSuspending(block: suspend (CoroutineScope) -> Unit) =
        uiStateJobs.add(viewLifecycleOwner.lifecycleScope.launchWhenStarted(block))

    private fun bindState() = uiStateJobs.add(lifecycleScope.launchWhenStarted {
        viewModel.eventFlow.collect(::eventHandler)
        bindState(this)
    })
}