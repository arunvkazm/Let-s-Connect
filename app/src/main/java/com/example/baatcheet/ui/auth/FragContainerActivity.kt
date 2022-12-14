package com.example.baatcheet.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.baatcheet.R
import com.example.baatcheet.databinding.ActivityFragContainerBinding

class FragContainerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFragContainerBinding
    private  lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFragContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val  navHostFragment = supportFragmentManager.findFragmentById(R.id.fragcontainer) as NavHostFragment
        navController = navHostFragment.navController

    }
}