package com.timmytruong.materialintervaltimer.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ActivityMainBinding
import com.timmytruong.materialintervaltimer.di.HorizontalProgress
import com.timmytruong.materialintervaltimer.ui.home.HomeFragment
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ProgressBar {

    @Inject
    @HorizontalProgress
    lateinit var progressAnimation: ProgressAnimation

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.activityMainNavHostFragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(binding.activityMainNavToolBar)
        supportActionBar?.title = getString(R.string.home)
        setupNavDrawer()
        setupAppBar()
    }

    private fun setupNavDrawer() {
        binding.activityMainNavDrawer.setupWithNavController(navController)
    }

    private fun setupAppBar() {
        appBarConfig =
            AppBarConfiguration(setOf(R.id.homeFragment), binding.activityMainDrawerLayout)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfig)
        NavigationUI.setupWithNavController(binding.activityMainNavDrawer, navController)
    }

    private fun getForegorundFragment(): Fragment? {
        val frag = supportFragmentManager.findFragmentById(binding.activityMainNavHostFragment.id)
        return if (frag == null) null else frag.childFragmentManager.fragments[0]
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            getForegorundFragment()?.let {
                when (it) {
                    is HomeFragment -> return@let
                    else -> onBackPressedDispatcher.onBackPressed()
                }
                return true
            }
        }

        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun updateProgressBar(progress: Int, show: Boolean) {
        binding.activityMainProgressBar.visibility = if (show) View.VISIBLE else View.GONE
        progressAnimation.startAnimation(
            target = binding.activityMainProgressBar,
            start = binding.activityMainProgressBar.progress,
            end = progress
        )
    }
}