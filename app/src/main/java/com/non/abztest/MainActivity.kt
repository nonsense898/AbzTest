package com.non.abztest

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.non.abztest.databinding.ActivityMainBinding
import com.non.abztest.viewmodel.MainViewModel
import com.non.abztest.viewmodel.NetworkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var usersTextView: TextView
    private lateinit var signUpTextView: TextView
    private lateinit var navController: NavController

    private val networkViewModel: NetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        usersTextView = binding.customBottomNav.findViewById(R.id.usersText)
        signUpTextView = binding.customBottomNav.findViewById(R.id.signUpText)

        initNavigation()
        observeNetworkState()
    }

    private fun initNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.hostContainer) as NavHostFragment
        navController = navHostFragment.navController

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        val navUsers = binding.customBottomNav.findViewById<LinearLayout>(R.id.nav_users)
        val navSignUp = binding.customBottomNav.findViewById<LinearLayout>(R.id.nav_sign_up)

        navUsers.setOnClickListener { navigateTo(R.id.nav_users, "Working with GET request") }
        navSignUp.setOnClickListener { navigateTo(R.id.nav_signup, "Working with POST request") }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.customBottomNav.visibility = when (destination.id) {
                R.id.nav_users, R.id.nav_signup -> View.VISIBLE
                else -> View.GONE
            }

            binding.topBarView.visibility = when (destination.id) {
                R.id.nav_users, R.id.nav_signup -> View.VISIBLE
                else -> View.GONE
            }
            updateNavSelection(destination.id)
        }
    }

    private fun navigateTo(destinationId: Int, title: String) {
        if (navController.currentDestination?.id != destinationId) {
            navController.navigate(destinationId)
            binding.toolbarTitle.text = title
        }
    }

    private fun updateNavSelection(destinationId: Int) {
        val navUsers = binding.customBottomNav.findViewById<LinearLayout>(R.id.nav_users)
        val navSignUp = binding.customBottomNav.findViewById<LinearLayout>(R.id.nav_sign_up)

        val isUsersSelected = destinationId == R.id.nav_users
        val isSignUpSelected = destinationId == R.id.nav_signup

        navUsers.isSelected = isUsersSelected
        navSignUp.isSelected = isSignUpSelected

        updateTextViewColor(usersTextView, isUsersSelected)
        updateTextViewColor(signUpTextView, isSignUpSelected)
    }

    private fun updateTextViewColor(textView: TextView, isSelected: Boolean) {
        val colorResId = if (isSelected) R.color.selected_nav_color else R.color.default_nav_color
        textView.setTextColor(ContextCompat.getColor(this, colorResId))
    }

    private fun observeNetworkState() {
        networkViewModel.isNetworkAvailable.observe(this) { isAvailable ->
            if (!isAvailable && navController.currentDestination?.id != R.id.nav_no_connection) {
                navController.navigate(R.id.nav_no_connection)
            }
        }
    }
}