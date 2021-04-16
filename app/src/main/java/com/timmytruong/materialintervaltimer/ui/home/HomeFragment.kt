package com.timmytruong.materialintervaltimer.ui.home

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.screen.BaseScreen
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favourites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.ui.reusable.HorizontalTimerItemAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.TimerActionBottomSheetDirections
import com.timmytruong.materialintervaltimer.ui.reusable.TimerListScreenBinding
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
    lateinit var recentsAdapter: HorizontalTimerItemAdapter

    @Inject
    @Favourites
    lateinit var favouritesAdapter: HorizontalTimerItemAdapter

    override val name: String = this::class.java.simpleName

    override val layoutId: Int = R.layout.fragment_home

    override val viewModel: HomeViewModel by viewModels()

    override fun bindView() {
        binding?.screen = screen
        binding?.viewModel = viewModel
        binding?.fragmentHomeRecentFrag?.horizontalRecycler?.adapter = recentsAdapter
        binding?.fragmentHomeFavouritesFrag?.horizontalRecycler?.adapter = favouritesAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFavouriteTimers()
        viewModel.fetchRecentTimers()

        startSuspending {
            screen.recents.onEach { list ->
                screen.isRecentsEmpty.set(list.isEmpty())
                recentsAdapter.addList(list)
            }.launchIn(it)

            screen.favourites.onEach { list ->
                screen.isFavouritesEmpty.set(list.isEmpty())
                favouritesAdapter.addList(list)
            }.launchIn(it)
        }
    }

    override fun onDestroyView() {
        binding?.fragmentHomeRecentFrag?.horizontalRecycler?.adapter = null
        binding?.fragmentHomeFavouritesFrag?.horizontalRecycler?.adapter = null
        super.onDestroyView()
    }
}

data class HomeScreen(
    var recents: Flow<@JvmSuppressWildcards List<TimerListScreenBinding>> = emptyFlow(),
    var favourites: Flow<@JvmSuppressWildcards List<TimerListScreenBinding>> = emptyFlow(),
    val isRecentsEmpty: ObservableBoolean = ObservableBoolean(false),
    val isFavouritesEmpty: ObservableBoolean = ObservableBoolean(false)
) : BaseScreen() {

    fun navToBottomSheet() = HomeFragmentDirections.actionHomeFragmentToTimerActionBottomSheet()
    fun navToCreateTimer() = HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment()
    fun navToTimer(id: Int) =
        TimerActionBottomSheetDirections.actionTimerActionBottomSheetToTimerFragment(id)
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

