package com.timmytruong.materialintervaltimer.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.databinding.ActivityMainBinding
import com.timmytruong.materialintervaltimer.di.HorizontalProgress
import com.timmytruong.materialintervaltimer.ui.base.BaseObserver
import com.timmytruong.materialintervaltimer.ui.home.HomeFragment
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressAnimation
import com.timmytruong.materialintervaltimer.ui.reusable.ProgressBar
import com.timmytruong.materialintervaltimer.utils.Event
import com.timmytruong.materialintervaltimer.utils.extensions.hideKeyboard
import com.timmytruong.materialintervaltimer.utils.extensions.showIf
import com.timmytruong.materialintervaltimer.utils.providers.PopUpProvider
import com.timmytruong.materialintervaltimer.utils.providers.ResourceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity :
    AppCompatActivity(),
    BaseObserver<MainActivityViewModel>,
    NavController.OnDestinationChangedListener,
    NavigationView.OnNavigationItemSelectedListener,
    ProgressBar {

    @Inject
    @HorizontalProgress
    lateinit var progressAnimation: ProgressAnimation

    @Inject
    lateinit var resources: ResourceProvider

    @Inject lateinit var popUpProvider: PopUpProvider

    override val viewModel: MainActivityViewModel by viewModels()

    override val uiStateJobs: ArrayList<Job> = arrayListOf()

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavController()
        setupNavDrawer()
        setupAppBar()
    }

    override fun onStart() {
        super.onStart()
        uiStateJobs.add(
            lifecycleScope.launchWhenStarted { viewModel.eventFlow.collect(::eventHandler) }
        )
    }

    override fun onStop() {
        uiStateJobs.forEach { it.cancel() }
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.createTimer -> viewModel.navToCreateTimer()
            R.id.recents -> viewModel.navToRecents()
            R.id.favorites -> viewModel.navToFavorites()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun eventHandler(event: Event) {
        when (event) {
            is Event.Error.Unknown -> popUpProvider.showErrorSnackbar(binding.root, R.string.somethingWentWrong)
            else -> { /** noop **/ }
        }
    }

    override fun navigationHandler(action: NavDirections) = with(navController) {
        currentDestination?.getAction(action.actionId)?.let { navigate(action) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            getForegroundFragment()?.let {
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

    override fun updateProgressBar(progress: Int, show: Boolean) = with(binding) {
        this.progress.showIf(show)
        progressAnimation.startAnimation(
            target =  this.progress,
            start =  this.progress.progress,
            end = progress
        )
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        currentFocus?.hideKeyboard()
    }

    private fun setupNavController() {
        navController = (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController
        navController.addOnDestinationChangedListener(this)
    }

    private fun setupNavDrawer() = with(binding.drawer) {
        setupWithNavController(navController)
        setNavigationItemSelectedListener(this@MainActivity)
        menu.getItem(0).isChecked = true
    }

    private fun setupAppBar() = with(binding) {
        setSupportActionBar(toolbar)
        appBarConfig = AppBarConfiguration(setOf(R.id.homeFragment), drawerLayout)
        NavigationUI.setupActionBarWithNavController(this@MainActivity, navController, appBarConfig)
    }

    private fun getForegroundFragment(): Fragment? {
        val frag = supportFragmentManager.findFragmentById(binding.fragment.id)
        return if (frag == null) null else frag.childFragmentManager.fragments[0]
    }
}