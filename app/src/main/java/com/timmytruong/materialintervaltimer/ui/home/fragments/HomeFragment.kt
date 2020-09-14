package com.timmytruong.materialintervaltimer.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.FragmentHomeBinding
import com.timmytruong.materialintervaltimer.ui.MainActivity
import com.timmytruong.materialintervaltimer.ui.home.HorizontalTimerItemAdapter
import com.timmytruong.materialintervaltimer.ui.interfaces.OnClickListeners
import java.lang.Exception

class HomeFragment : Fragment(), OnClickListeners.HomeFrag {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var recentsAdapter: HorizontalTimerItemAdapter

    private lateinit var favouritesAdapter: HorizontalTimerItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        setupAdapters()
    }

    private fun setupAppBar() {
        try {
            val act = activity as MainActivity
            act.appBar.findItem(R.id.createTimerFragment).isVisible = true
            act.appBar.findItem(R.id.createTimerFragment).setOnMenuItemClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToCreateTimerFragment()
                this.view?.let { it1 -> Navigation.findNavController(it1).navigate(action) }
                return@setOnMenuItemClickListener true
            }
        } catch (err: Exception) {
            err.printStackTrace()
        }
    }

    private fun setupAdapters() {
        recentsAdapter = HorizontalTimerItemAdapter(this)
        binding.fragmentHomeRecentFrag.horizontalRecycler.adapter = recentsAdapter

        favouritesAdapter = HorizontalTimerItemAdapter(this)
        binding.fragmentHomeFavouritesFrag.horizontalRecycler.adapter = favouritesAdapter
    }

    override fun onTimerClicked(view: View) {
        val bottomSheetFragment = TimerActionBottomSheet()
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
    }
}