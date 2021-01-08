package com.timmytruong.materialintervaltimer.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ActivityMainBinding
import com.timmytruong.materialintervaltimer.ui.interfaces.ProgressBarInterface
import com.timmytruong.materialintervaltimer.utils.constants.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ProgressBarInterface {

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
                AppConstants.PROGRESS_BAR_PROPERTY,
                binding.activityMainProgressBar.progress,
                progress
            )
        animation.duration = AppConstants.PROGRESS_BAR_ANIMATION_DURATION_MS
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}