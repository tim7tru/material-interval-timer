package com.timmytruong.materialintervaltimer.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.timmytruong.materialintervaltimer.R
import com.timmytruong.materialintervaltimer.utils.DesignUtils
import com.timmytruong.materialintervaltimer.ui.interfaces.ProgressBarInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ProgressBarInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(nav_tool_bar)

        supportActionBar?.title = getString(R.string.home)

        setupNavDrawer()

        setupAppBar()
    }

    private fun setupNavDrawer() {
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val controller = navHostFrag.navController
        nav_drawer?.setupWithNavController(controller)
    }

    private fun setupAppBar() {
        val navHostFrag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val controller = navHostFrag.navController
        val appBarConfig = AppBarConfiguration(controller.graph, drawer_layout)
        nav_tool_bar?.setupWithNavController(controller, appBarConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun toggleProgressBarVisibility(show: Boolean) {
        determinateBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun updateProgressBar(progress: Int) {
        DesignUtils.updateProgressBar(view = determinateBar, progress = progress)
    }
}