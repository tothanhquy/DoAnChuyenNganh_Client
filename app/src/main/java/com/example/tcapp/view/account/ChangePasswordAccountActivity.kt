package com.example.tcapp.view.account

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.viewmodel.account.ChangePasswordAccountViewModel

class ChangePasswordAccountActivity : CoreActivity() {
	private lateinit var objectViewModel: ChangePasswordAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ChangePasswordAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.change_password)
		setContentView(R.layout.activity_change_password_account)
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		//objectViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.changePasswordAccountActivity)
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
					super.showNotificationDialog(it.title,it.contents,it.listener)
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
		
	}
	

	fun changePassword(view: View){
		val oldPass = findViewById<EditText>(R.id.changePasswordAccountActivityOldPassword).text.toString()
		val newPass = findViewById<EditText>(R.id.changePasswordAccountActivityNewPassword).text.toString()
		val confirmNewPass = findViewById<EditText>(R.id.changePasswordAccountActivityConfirmNewPassword).text.toString()
		
		objectViewModel.changePassword(oldPass,newPass,confirmNewPass,::changePassOkCallback);
	}
	private fun changePassOkCallback(){
		runOnUiThread {
			finish()
		}
	}

}