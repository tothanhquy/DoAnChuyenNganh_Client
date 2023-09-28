package com.example.tcapp.view.account

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.viewmodel.account.RegisterAccountViewModel

class RegisterAccountActivity : CoreActivity() {
	private lateinit var registerAccountViewModel: RegisterAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		registerAccountViewModel = RegisterAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.register)
		setContentView(R.layout.activity_register_account)
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		//registerAccountViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.registerAccountActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		registerAccountViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set alert notification
		registerAccountViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,fun(dia: DialogInterface , i:Int){this@RegisterAccountActivity.finish()})
				}
			}
		})
		//set loading
		registerAccountViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		
	}
	

	fun register(view: View){
		val email = findViewById<EditText>(R.id.registerAccountEmail).text.toString()
		val password = findViewById<EditText>(R.id.registerAccountPassword).text.toString()
		val confirmPassword = findViewById<EditText>(R.id.registerAccountConfirmPassword).text.toString()
		val name = findViewById<EditText>(R.id.registerAccountName).text.toString()
		registerAccountViewModel.register(email,password,confirmPassword,name);
	}

}