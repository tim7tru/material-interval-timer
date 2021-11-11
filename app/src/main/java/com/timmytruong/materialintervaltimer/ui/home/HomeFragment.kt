package com.timmytruong.materialintervaltimer.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.ui.list.TimerType
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.HorizontalTimerAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.TimerItem
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.hide
import com.timmytruong.materialintervaltimer.utils.name
import com.timmytruong.materialintervaltimer.utils.show
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeScreen, HomeViewModel, FragmentHomeBinding>() {

    @Inject
    override lateinit var screen: HomeScreen

    @Inject
    @Recents
    lateinit var recentsAdapter: HorizontalTimerAdapter

    @Inject
    @Favourites
    lateinit var favouritesAdapter: HorizontalTimerAdapter

    override val name: String = name()

    override val layoutId: Int = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModels()

    override val hasBackPress: Boolean = false

    private var isRecentsEmpty = true

    private var isFavouritesEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_bar, menu)
        menu.findItem(R.id.start).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.start) viewModel.onAddClicked()
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchRecentTimers()
        viewModel.fetchFavouriteTimers()

        startSuspending {
            screen.recents.onEach { list ->
                isRecentsEmpty = list.isEmpty()
                checkEmptyStates()
                recentsAdapter.addList(list)
            }.launchIn(it)

            screen.favourites.onEach { list ->
                isFavouritesEmpty = list.isEmpty()
                checkEmptyStates()
                favouritesAdapter.addList(list)
            }.launchIn(it)
        }
    }

    private fun checkEmptyStates() {
        if (isFavouritesEmpty) binding?.favouritesContainer?.hide() else binding?.favouritesContainer?.show()
        if (isRecentsEmpty) binding?.recentsContainer?.hide() else binding?.recentsContainer?.show()
        if (isFavouritesEmpty && isRecentsEmpty) binding?.emptyState?.show() else binding?.emptyState?.hide()
    }

    override fun bindView() {
        binding?.addFab?.setOnClickListener { viewModel.onAddClicked() }
        binding?.favouritesSeeAll?.setOnClickListener { viewModel.onFavouritesSeeAllClicked() }
        binding?.recentsSeeAll?.setOnClickListener { viewModel.onRecentsSeeAllClicked() }
        binding?.recents?.horizontalRecycler?.adapter = recentsAdapter
        binding?.favourites?.horizontalRecycler?.adapter = favouritesAdapter
    }

    override fun onDestroyView() {
        binding?.recents?.horizontalRecycler?.adapter = null
        binding?.favourites?.horizontalRecycler?.adapter = null
        super.onDestroyView()
    }
}

@Open
data class HomeScreen(
    var recents: Flow<@JvmSuppressWildcards List<TimerItem>> = emptyFlow(),
    var favourites: Flow<@JvmSuppressWildcards List<TimerItem>> = emptyFlow()
) : BaseScreen() {

    fun navToBottomSheet(id: Int, favourited: Boolean) =
        HomeFragmentDirections.toActionBottomSheet(timerId = id, favourited = favourited)

    fun navToTimerList(type: TimerType) = HomeFragmentDirections.toTimerList(type = type)

    fun navToCreateTimer() = HomeFragmentDirections.toCreateTimer(clearViewModel = true)
}

@InstallIn(FragmentComponent::class)
@Module
class HomeFragmentModule {

    @Recents
    @Provides
    fun provideRecentsAdapter() = HorizontalTimerAdapter()

    @Favourites
    @Provides
    fun provideFavouritesAdapter() = HorizontalTimerAdapter()
}

