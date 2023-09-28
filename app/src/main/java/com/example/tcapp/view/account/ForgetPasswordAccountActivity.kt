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
import com.example.tcapp.viewmodel.account.ForgetPasswordAccountViewModel

class ForgetPasswordAccountActivity : CoreActivity() {
	private lateinit var forgetPasswordAccountViewModel: ForgetPasswordAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		forgetPasswordAccountViewModel = ForgetPasswordAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.forget_password)
		setContentView(R.layout.activity_forget_password_account)
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		//forgetPasswordAccountViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.forgetPasswordAccountActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		forgetPasswordAccountViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set alert notification
		forgetPasswordAccountViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//set loading
		forgetPasswordAccountViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set text submit button
		forgetPasswordAccountViewModel.wasSent.observe(this, Observer {
			runOnUiThread {
				if(it){
					findViewById<Button>(R.id.forgetPasswordAccountSubmit).text = "Gửi lại Mail"
				}else{
					findViewById<Button>(R.id.forgetPasswordAccountSubmit).text = "Gửi mail cấp lại mật khẩu"
				}
			}
		})
		
	}
	

	fun request(view: View){
		val email = findViewById<EditText>(R.id.forgetPasswordAccountEmail).text.toString()
		forgetPasswordAccountViewModel.request(email);
	}

}