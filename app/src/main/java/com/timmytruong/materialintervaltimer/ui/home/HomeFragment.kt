package com.timmytruong.materialintervaltimer.ui.home

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.fragment.app.viewModels
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.di.Favorites
import com.timmytruong.materialintervaltimer.di.Recents
import com.timmytruong.materialintervaltimer.ui.base.BaseFragment
import com.timmytruong.materialintervaltimer.ui.reusable.adapter.HorizontalTimerAdapter
import com.timmytruong.materialintervaltimer.ui.reusable.type.TimerType
import com.timmytruong.materialintervaltimer.utils.Open
import com.timmytruong.materialintervaltimer.utils.extensions.show
import com.timmytruong.materialintervaltimer.utils.extensions.showIf
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel, FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject @Recents lateinit var recentsAdapter: HorizontalTimerAdapter

    @Inject @Favorites lateinit var favoritesAdapter: HorizontalTimerAdapter

    override val viewModel: HomeViewModel by viewModels()

    override val hasOptionsMenu: Boolean = true

    override val hasBackPress: Boolean = false

    override val fragmentTitle: Int = R.string.home

    private var isRecentsEmpty = true

    private var isFavoritesEmpty = true

    private val start: MenuItem? get() = menu?.findItem(R.id.start)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        start.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item == start) viewModel.onAddClicked()
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchRecents()
        viewModel.fetchFavorites()
    }

    private fun checkEmptyStates() = binding?.apply {
        favoritesContainer.showIf(!isFavoritesEmpty)
        recentsContainer.showIf(!isRecentsEmpty)
    }

    override fun bindView() = binding?.apply {
        addFab.setOnClickListener { viewModel.onAddClicked() }
        favoritesSeeAll.setOnClickListener { viewModel.onFavoritesSeeAllClicked() }
        recentsSeeAll.setOnClickListener { viewModel.onRecentsSeeAllClicked() }
        recents.recycler.adapter = recentsAdapter
        favorites.recycler.adapter = favoritesAdapter
    }

    override fun onDestroyView() {
        binding?.recents?.recycler?.adapter = null
        binding?.favorites?.recycler?.adapter = null
        super.onDestroyView()
    }

    override suspend fun bindState(scope: CoroutineScope) = binding?.apply {
        viewModel.recents.onEach { list ->
            isRecentsEmpty = list.isEmpty()
            checkEmptyStates()
            recentsAdapter.addList(list)
        }.launchIn(scope)

        viewModel.favorites.onEach { list ->
            isFavoritesEmpty = list.isEmpty()
            checkEmptyStates()
            favoritesAdapter.addList(list)
        }.launchIn(scope)
    }
}

@Open
@ActivityRetainedScoped
class HomeDirections @Inject constructor() {
    fun toBottomSheet(id: Int, favorited: Boolean) =
        HomeFragmentDirections.toActionBottomSheet(timerId = id, favorited = favorited)

    fun toTimerList(type: TimerType) = HomeFragmentDirections.toTimerList(type = type)

    fun toCreateTimer() = HomeFragmentDirections.toCreateTimer(clearViewModel = true)

}

@InstallIn(FragmentComponent::class)
@Module
class HomeFragmentModule {

    @Recents
    @Provides
    fun provideRecentsAdapter(resources: ResourceProvider) = HorizontalTimerAdapter(resources)

    @Favorites
    @Provides
    fun providefavoritesAdapter(resources: ResourceProvider) = HorizontalTimerAdapter(resources)
}

