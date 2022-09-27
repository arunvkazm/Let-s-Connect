package com.example.baatcheet.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.baatcheet.R
import com.example.baatcheet.ui.auth.FragContainerActivity

class SplashActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)
		window.setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN ,
			WindowManager.LayoutParams.FLAG_FULLSCREEN
		)

		Handler().postDelayed({
			val sharedPreferences : SharedPreferences = getSharedPreferences("login" , MODE_PRIVATE)
			val check : Boolean = sharedPreferences.getBoolean("flag" , false)
			if (check) {
				startActivity(Intent(this , MainActivity::class.java))
			} else {
				startActivity(Intent(this , FragContainerActivity::class.java))
			}
			finish()
		} , 3000)
	}
}