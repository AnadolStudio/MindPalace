package com.anadol.mindpalace.view.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anadol.mindpalace.R
import com.anadol.mindpalace.domain.utils.IOnBackPressed
import com.anadol.mindpalace.domain.utils.isDuplicate
import com.anadol.mindpalace.view.screens.main.grouplist.GroupListFragment
import com.anadol.mindpalace.view.screens.main.lessons.InformationFragment
import com.anadol.mindpalace.view.screens.main.statistic.StatisticFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CURRENT_ID = "id"

        fun startActivity(context: Context) {
            context.startActivity(
                Intent(context, MainActivity::class.java)
            )
        }
    }

    private var currentId = -1

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putInt(CURRENT_ID, currentId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            if (!currentId.isDuplicate(menuItem.itemId)) {

                currentId = menuItem.itemId
                when (currentId) {
                    R.id.navigation_statistic -> replaceFragment(StatisticFragment.newInstance())
                    R.id.navigation_home -> replaceFragment(GroupListFragment.newInstance())
                    R.id.navigation_settings -> replaceFragment(InformationFragment.newInstance())
                }

                return@setOnItemSelectedListener true
            }

            return@setOnItemSelectedListener false
        }

        savedInstanceState
            ?.run { currentId = getInt(CURRENT_ID) }
            ?: run { bottomNavigationView.selectedItemId = R.id.navigation_home }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container) ?: return

        if (fragment !is IOnBackPressed || !fragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    private fun replaceFragment(f: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, f)
            .commit()
    }

}