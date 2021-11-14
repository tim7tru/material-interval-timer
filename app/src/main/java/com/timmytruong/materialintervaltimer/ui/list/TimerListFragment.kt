package com.timmytruong.materialintervaltimer.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentTimerListBinding
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.VerticalTimerAdapter
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.extensions.HIDE
import com.timmytruong.materialintervaltimer.utils.extensions.SHOW
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class TimerListFragment : BaseFragment<TimerListViewModel, FragmentTimerListBinding>(FragmentTimerListBinding::inflate) {

    @Inject
    lateinit var verticalTimerAdapter: VerticalTimerAdapter

    override val viewModel: TimerListViewModel by viewModels()

    override val hasBackPress: Boolean = false

    override val hasOptionsMenu: Boolean = false

    override val fragmentTitle: Int get() = when (args.type) {
        TimerType.RECENTS -> R.string.recentTimers
        TimerType.FAVORITES -> R.string.favoriteTimers
    }

    private val args: TimerListFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchTimers(args.type)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchTimers(args.type)
    }

    override fun bindView() = binding?.apply {
        recycler.adapter = verticalTimerAdapter
        when (args.type) {
            TimerType.RECENTS -> header.text = resources.string(R.string.recentTimers)
            TimerType.FAVORITES -> header.text = resources.string(R.string.favoriteTimers)
        }
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
        super.onDestroyView()
    }

    private fun checkEmptyState(isEmpty: Boolean) = binding?.apply {
        header.visibility = if (isEmpty) HIDE else SHOW
        recycler.visibility = if (isEmpty) HIDE else SHOW
        emptyState.visibility = if (isEmpty) SHOW else HIDE
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.timers.onEach {
            checkEmptyState(it.isEmpty())
            verticalTimerAdapter.addList(it)
        }.launchIn(scope)
    }
}

@Open
@ActivityRetainedScoped
class TimerListDirctions @Inject constructor() {
    fun navToBottomSheet(id: Int, favorited: Boolean) =
        TimerListFragmentDirections.toActionBottomSheet(timerId = id, favorited = favorited)
}

enum class TimerType {
    RECENTS,
    FAVORITES
}