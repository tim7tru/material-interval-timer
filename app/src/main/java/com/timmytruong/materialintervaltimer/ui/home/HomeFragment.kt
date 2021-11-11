package com.timmytruong.materialintervaltimer.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.HorizontalTimerItemAdapter
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.name
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeScreen, HomeViewModel, FragmentHomeBinding>() {

    @Inject
    override lateinit var screen: HomeScreen

    @Inject
    @Recents
    lateinit var recentsAdapter: HorizontalTimerItemAdapter

    @Inject
    @Favourites
    lateinit var favouritesAdapter: HorizontalTimerItemAdapter

    override val name: String = name()

    override val layoutId: Int = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModels()

    override val hasBackPress: Boolean = false

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
        viewModel.fetchFavouriteTimers()
        viewModel.fetchRecentTimers()

        startSuspending {
            viewModel.recents.onEach { list ->
                screen.isRecentsEmpty.set(list.isEmpty())
                recentsAdapter.addList(list)
            }.launchIn(it)

            viewModel.favourites.onEach { list ->
                screen.isFavouritesEmpty.set(list.isEmpty())
                favouritesAdapter.addList(list)
            }.launchIn(it)
        }
    }

    override fun bindView() {
        binding?.screen = screen
        binding?.viewModel = viewModel
        binding?.fragmentHomeRecentFrag?.horizontalRecycler?.adapter = recentsAdapter
        binding?.fragmentHomeFavouritesFrag?.horizontalRecycler?.adapter = favouritesAdapter
    }

    override fun onDestroyView() {
        binding?.fragmentHomeRecentFrag?.horizontalRecycler?.adapter = null
        binding?.fragmentHomeFavouritesFrag?.horizontalRecycler?.adapter = null
        super.onDestroyView()
    }
}

@Open
data class HomeScreen(
    val isRecentsEmpty: ObservableBoolean = ObservableBoolean(false),
    val isFavouritesEmpty: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToBottomSheet(id: Int, isFavourited: Boolean) =
        HomeFragmentDirections.actionHomeFragmentToTimerActionBottomSheet(timerId = id)

    fun navToRecentTimers() =
        HomeFragmentDirections.actionHomeFragmentToRecentsFragment()

    fun navToFavouriteTimers() =
        HomeFragmentDirections.actionHomeFragmentToFavouritesFragment()

    fun navToCreateTimer() =
        HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment(clearViewModel = true)
}

@InstallIn(FragmentComponent::class)
@Module
class HomeFragmentModule {

    @Recents
    @Provides
    fun provideRecentsAdapter() = HorizontalTimerItemAdapter()

    @Favourites
    @Provides
    fun provideFavouritesAdapter() = HorizontalTimerItemAdapter()
}

