package com.timmytruong.materialintervaltimer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.ui.interfaces.ProgressBarInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ProgressBarInterface {
    private lateinit var navController: NavController

    private lateinit var appBarConfig: AppBarConfiguration

    lateinit var appBar: Menu

    private val onDestinationChangedListener = NavController.OnDestinationChangedListener { _, _, _ ->
        hideAppBarItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        appBar = nav_tool_bar.menu

        setupNavController()

        setupAppBar()

        setupNavDrawer()
    }

    private fun hideAppBarItems() {
        for (item in 0 until appBar.size())
            appBar.getItem(item).isVisible = false
    }

    private fun setupNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener(onDestinationChangedListener)
    }

    private fun setupNavDrawer() {
        nav_drawer?.setupWithNavController(navController)
    }

    private fun setupAppBar() {
        appBarConfig = AppBarConfiguration(navController.graph, drawer_layout)
        nav_tool_bar?.setupWithNavController(navController, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun toggleProgressBarVisibility(show: Boolean) {
        determinateBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateProgressBar(progress: Int) {
        DesignUtils.updateProgressBar(view = determinateBar, progress = progress)
    }
}