package com.example.tcapp.view.account

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.account.SettingAccountModels
import com.example.tcapp.viewmodel.account.LoginAccountViewModel
import com.example.tcapp.viewmodel.account.SettingAccountViewModel

class SettingAccountActivity : CoreActivity() {
	private lateinit var objectViewModel: SettingAccountViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout: View? = null;

	private var isLoadRegisterReceiveEmail:Boolean=false;

	private var registerReceiveEmailBefore: SettingAccountModels.RegisterReceiveEmailResponse?=null;
	private var registerReceiveEmailNow: SettingAccountModels.RegisterReceiveEmailResponse?=null;

	private var registerReceiveEmailContainer:LinearLayout?=null;
	private var registerReceiveEmailContainerUpdateButton: Button?=null;
	private var addFriendRequestCheckBox:CheckBox?=null;
	private var teamRecruitRequestCheckBox:CheckBox?=null;
	private var teamJoinRequestCheckBox:CheckBox?=null;
	private var projectInviteRequestCheckBox:CheckBox?=null;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = SettingAccountViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.setting_account)
		setContentView(R.layout.activity_setting_account)

		registerReceiveEmailContainer = findViewById(R.id.settingAccountActivityRegisterReceiveEmailContainer);
		registerReceiveEmailContainerUpdateButton = findViewById(R.id.settingAccountActivityRegisterReceiveEmailContainerUpdateButton);

		addFriendRequestCheckBox = findViewById<CheckBox>(R.id.settingAccountActivityRegisterReceiveEmailContainerAddFriendRequestCheckbox);
		teamRecruitRequestCheckBox = findViewById<CheckBox>(R.id.settingAccountActivityRegisterReceiveEmailContainerTeamRecruitRequestCheckbox);
		teamJoinRequestCheckBox = findViewById<CheckBox>(R.id.settingAccountActivityRegisterReceiveEmailContainerTeamJoinRequestCheckbox);
		projectInviteRequestCheckBox = findViewById<CheckBox>(R.id.settingAccountActivityRegisterReceiveEmailContainerProjectInviteRequestCheckbox);

		initViews()
		setRender()
		loadData()
		setEvents();
	}

	private fun setEvents() {
		addFriendRequestCheckBox!!.setOnClickListener {
			registerReceiveEmailNow!!.addFriendRequest = addFriendRequestCheckBox!!.isChecked
			toggleRegisterReceiveEmailContainerUpdateButton()
		}
		teamJoinRequestCheckBox!!.setOnClickListener {
			registerReceiveEmailNow!!.teamJoinRequest = teamJoinRequestCheckBox!!.isChecked
			toggleRegisterReceiveEmailContainerUpdateButton()
		}
		teamRecruitRequestCheckBox!!.setOnClickListener {
			registerReceiveEmailNow!!.teamRecruitRequest = teamRecruitRequestCheckBox!!.isChecked
			toggleRegisterReceiveEmailContainerUpdateButton()
		}
		projectInviteRequestCheckBox!!.setOnClickListener {
			registerReceiveEmailNow!!.projectInviteRequest = projectInviteRequestCheckBox!!.isChecked
			toggleRegisterReceiveEmailContainerUpdateButton()
		}
	}

	private fun toggleRegisterReceiveEmailContainerUpdateButton(){
		var isSame = true;
		if(registerReceiveEmailNow!!.addFriendRequest!=registerReceiveEmailBefore!!.addFriendRequest)isSame=false;
		if(registerReceiveEmailNow!!.teamRecruitRequest!=registerReceiveEmailBefore!!.teamRecruitRequest)isSame=false;
		if(registerReceiveEmailNow!!.teamJoinRequest!=registerReceiveEmailBefore!!.teamJoinRequest)isSame=false;
		if(registerReceiveEmailNow!!.projectInviteRequest!=registerReceiveEmailBefore!!.projectInviteRequest)isSame=false;

		registerReceiveEmailContainerUpdateButton!!.visibility = if(isSame)View.GONE else View.VISIBLE;
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

		objectViewModel.registerReceiveEmailResponse.observe(this) {
			runOnUiThread {
				if(it!=null)handleRegisterReceiveEmailResponse(it)
			}
		};
	}

	private fun handleRegisterReceiveEmailResponse(response:SettingAccountModels.RegisterReceiveEmailResponse){
		if(!response.isVerifyEmail){
			super.showNotificationDialog("Lưu ý","Bạn chưa xác thực email nên không thể thực hiện chức năng này.",null)
		}else{
			registerReceiveEmailBefore = SettingAccountModels.RegisterReceiveEmailResponse(response);
			registerReceiveEmailNow = response;

			addFriendRequestCheckBox!!.isChecked = response.addFriendRequest
			teamJoinRequestCheckBox!!.isChecked = response.teamJoinRequest
			teamRecruitRequestCheckBox!!.isChecked = response.teamRecruitRequest
			projectInviteRequestCheckBox!!.isChecked = response.projectInviteRequest

			registerReceiveEmailContainer!!.visibility=View.VISIBLE;
			isLoadRegisterReceiveEmail = true;
		}
	}

	fun logout(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want to logout.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.logout(::logoutOkCallBack);
			}
		)
	}
	fun logoutAll(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want to logout all device.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.logoutAll(::logoutOkCallBack);
			}
		)
	}
	private fun logoutOkCallBack(){
		runOnUiThread {
			this@SettingAccountActivity.finish()
		}
	}
	
	fun changePasswordNavigation(view: View){
		val intent = Intent(applicationContext , ChangePasswordAccountActivity::class.java)
		startActivity(intent)
	}

	fun openRegisterReceiveEmailContainer(view: View){
		if(registerReceiveEmailContainer!!.visibility==View.VISIBLE){
			registerReceiveEmailContainer!!.visibility=View.GONE;
		}else{
			if(!isLoadRegisterReceiveEmail){
				objectViewModel.loadRegisterReceiveEmail();
			}else{
				registerReceiveEmailContainer!!.visibility=View.VISIBLE;
			}
		}
	}

	private fun getStatusRequest():ArrayList<String>{
		var arr:ArrayList<String> = ArrayList()
		registerReceiveEmailNow!!.let {
			if(it.addFriendRequest)arr.add(SettingAccountModels.EditRegisterReceiveEmailStatusRequest.AddFriendRequest)
			if(it.teamRecruitRequest)arr.add(SettingAccountModels.EditRegisterReceiveEmailStatusRequest.TeamRecruitRequest)
			if(it.teamJoinRequest)arr.add(SettingAccountModels.EditRegisterReceiveEmailStatusRequest.TeamJoinRequest)
			if(it.projectInviteRequest)arr.add(SettingAccountModels.EditRegisterReceiveEmailStatusRequest.ProjectInviteRequest)
		}
		return arr;
	}
	fun updateRegisterReceiveEmail(view: View){
		val status = getStatusRequest();
		objectViewModel.updateRegisterReceiveEmail(status,::updateRegisterReceiveEmailOkCallBack);
	}
	private fun updateRegisterReceiveEmailOkCallBack(){
		runOnUiThread {
			registerReceiveEmailBefore = SettingAccountModels.RegisterReceiveEmailResponse(registerReceiveEmailNow!!);
			toggleRegisterReceiveEmailContainerUpdateButton()
		}

	}

}