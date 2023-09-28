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
import com.example.tcapp.viewmodel.account.SettingAccountViewModel

class SettingAccountActivity : CoreActivity() {
	private lateinit var objectViewModel: SettingAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = SettingAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.setting_account)
		setContentView(R.layout.activity_setting_account)
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		//objectViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.settingAccountActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		objectViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set alert notification
		objectViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,fun(dia: DialogInterface , i:Int){this@SettingAccountActivity.finish()})
				}
			}
		})
		//set loading
		objectViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//go back
		objectViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					this@SettingAccountActivity.finish()
				}
			}
		})
	}
	

	fun logout(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want to logout.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.logout();
			}
		)
	}
	fun logoutAll(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want to logout all device.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.logoutAll();
			}
		)
	}
	
	fun changePasswordNavigation(view: View){
		val intent = Intent(applicationContext , ChangePasswordAccountActivity::class.java)
		startActivity(intent)
	}

}