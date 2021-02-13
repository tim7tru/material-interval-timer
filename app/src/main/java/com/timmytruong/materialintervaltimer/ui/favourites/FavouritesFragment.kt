package com.timmytruong.materialintervaltimer.ui.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.base.BaseFragment
import com.timmytruong.materialintervaltimer.base.BaseViewModel
import com.timmytruong.materialintervaltimer.databinding.FragmentFavouritesBinding
import com.timmytruong.materialintervaltimer.model.Timer
import com.timmytruong.materialintervaltimer.utils.events.Error
import com.timmytruong.materialintervaltimer.utils.events.Event
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavouritesFragment : BaseFragment() {

    @Inject
    lateinit var favouritesListAdapter: FavouritesListAdapter

    @Inject
    lateinit var favouritesViewModel: FavouritesViewModel

    private lateinit var binding: FragmentFavouritesBinding

    private val favouriteTimersObserver = Observer<List<Timer>> {
        if (it.isEmpty()) favouritesViewModel.onEmptyList()
        else favouritesListAdapter.addList(it)
    }

    override val baseViewModel: BaseViewModel
        get() = favouritesViewModel

    override fun bindView() {
        binding.fragmentFavouritesRecycler.adapter = favouritesListAdapter
    }

    override val eventObserver: Observer<Event<Any>>
        get() = Observer { event ->
            event.getContentIfNotHandled()?.let {
                when (it) {
                    is Error.QualifierError -> handleQualifierError(it.qualifier)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        bindView()
        favouritesViewModel.fetchFavourites()
    }

    override fun subscribeObservers() {
        super.subscribeObservers()
        favouritesViewModel.favourites.observe(viewLifecycleOwner, favouriteTimersObserver)
    }

    private fun handleQualifierError(qualifier: String) {
        when (qualifier) {
            EMPTY_LIST_ERROR -> toggleEmptyListError(show = true)
        }
    }

    private fun toggleEmptyListError(show: Boolean) {

    }
}