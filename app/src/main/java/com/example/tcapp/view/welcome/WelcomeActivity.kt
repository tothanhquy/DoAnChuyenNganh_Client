package com.example.tcapp.view.welcome

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.LocalData
import com.example.tcapp.view.account.LoginAccountActivity
import com.example.tcapp.view.home.HomeActivity



class WelcomeActivity : CoreActivity() {
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		super.hideTitle()
		setContentView(R.layout.activity_welcome)
	}
	
	fun start(view: View){
		this@WelcomeActivity.finish()
//		goToHomeActivity()
	}
//	private fun goToHomeActivity(){
//		val intent = Intent(applicationContext , HomeActivity::class.java)
//		startActivity(intent)
//		finish()
//	}

	
}