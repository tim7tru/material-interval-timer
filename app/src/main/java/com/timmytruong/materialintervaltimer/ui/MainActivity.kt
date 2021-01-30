package com.timmytruong.materialintervaltimer.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ActivityMainBinding
import com.timmytruong.materialintervaltimer.ui.reusable.BackButton
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_BAR_ANIMATION_DURATION_MS
import com.timmytruong.materialintervaltimer.ui.reusable.PROGRESS_BAR_PROPERTY
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ProgressBar {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.activityMainNavToolBar)

        supportActionBar?.title = getString(R.string.home)

        setupNavDrawer()

        setupAppBar()
    }

    private fun setupNavDrawer() {
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragment) as NavHostFragment
        val controller = navHostFrag.navController
        binding.activityMainNavDrawer.setupWithNavController(controller)
    }

    private fun setupAppBar() {
        val navHostFrag =
            supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragment) as NavHostFragment
        val controller = navHostFrag.navController
        val appBarConfig = AppBarConfiguration(controller.graph, binding.activityMainDrawerLayout)
        binding.activityMainNavToolBar.setupWithNavController(controller, appBarConfig)
    }

    private fun getForegorundFragment(): Fragment? {
        val frag = supportFragmentManager.findFragmentById(binding.activityMainNavHostFragment.id)
        return if (frag == null) null else frag.childFragmentManager.fragments[0]

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.activityMainNavHostFragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun toggleProgressBarVisibility(show: Boolean) {
        binding.activityMainProgressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateProgressBar(progress: Int) {
        val animation =
            ObjectAnimator.ofInt(
                binding.activityMainProgressBar,
                PROGRESS_BAR_PROPERTY,
                binding.activityMainProgressBar.progress,
                progress
            )
        animation.duration = PROGRESS_BAR_ANIMATION_DURATION_MS
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    override fun onBackPressed() {
        try {
            if ((getForegorundFragment() as? BackButton)?.onBackPressed() == true) {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}