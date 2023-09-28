package com.example.tcapp.view.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.viewmodel.account.LoginAccountViewModel

class LoginAccountActivity : CoreActivity() {
	private lateinit var loginAccountViewModel: LoginAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		loginAccountViewModel = LoginAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.login)
		setContentView(R.layout.activity_login_account)
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		//loginAccountViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.loginAccountActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		loginAccountViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set alert notification
		loginAccountViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,fun(dia: DialogInterface , i:Int){this@LoginAccountActivity.finish()})
				}
			}
		})
		//set loading
		loginAccountViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		
	}
	

	fun login(view: View){
		val email = findViewById<EditText>(R.id.loginAccountEmail).text.toString()
		val password = findViewById<EditText>(R.id.loginAccountPassword).text.toString()
		loginAccountViewModel.login(email,password);
	}
	
	fun forgetPasswordNavigation(view: View){
		val intent = Intent(applicationContext , ForgetPasswordAccountActivity::class.java)
		startActivity(intent)
	}

}