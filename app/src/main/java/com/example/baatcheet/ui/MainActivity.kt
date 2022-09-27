package com.example.baatcheet.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.baatcheet.R
import com.example.baatcheet.ui.auth.FragContainerActivity
import com.example.baatcheet.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

	private lateinit var binding : ActivityMainBinding
	private lateinit var navController : NavController


	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		val navHostFragment = supportFragmentManager.findFragmentById(R.id.maincontainer) as NavHostFragment
		navController = navHostFragment.navController
		val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
		setupWithNavController(bottomNavigationView , navController)

		navController.addOnDestinationChangedListener { _ , nd : NavDestination , _ ->
			if (nd.id == R.id.chatFragment|| nd.id==R.id.userProfileFragment) {
				binding.bottomNavigation.visibility = View.GONE
				binding.toolbar.visibility = View.GONE
			} else {
				binding.bottomNavigation.visibility = View.VISIBLE
				binding.toolbar.visibility = View.VISIBLE
			}


		}

		binding.toolbar.setOnMenuItemClickListener {
			when (it.itemId) {
				R.id.logout -> {
					var sharedPreferences : SharedPreferences = getSharedPreferences("login" , MODE_PRIVATE)
					var editor : SharedPreferences.Editor = sharedPreferences.edit()
					editor.putBoolean("flag" , false)
					editor.apply()
					val intent = Intent(this@MainActivity , FragContainerActivity::class.java)
					startActivity(intent)
					finishAffinity()
					true
				}else ->{
				false
				}

			}

		}
	}

}