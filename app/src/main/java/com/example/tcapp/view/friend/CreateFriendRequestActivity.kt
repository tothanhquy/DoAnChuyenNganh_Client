package com.example.tcapp.view.friend

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.core.Genaral.Static.setTeamAvatarImage
import com.example.tcapp.core.Genaral.Static.setUserAvatarImage
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.friend.CreateFriendRequestViewModel
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel
import com.google.android.material.textfield.TextInputEditText

class CreateFriendRequestActivity : CoreActivity() {
	private lateinit var objectViewModel: CreateFriendRequestViewModel;
	private var userId:String?=null;
	private var userName:String?=null;
	private var userAvatar:String?=null;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var  content:View? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = CreateFriendRequestViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.create_request)
		setContentView(R.layout.activity_create_friend_request)

		content = findViewById<TextInputEditText>(R.id.createFriendRequestActivityContent);
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
	}
	
	
	private fun loadData() {
		userId = intent.getStringExtra("userId")?.toString()
		userName = intent.getStringExtra("userName").toString()
		userAvatar = intent.getStringExtra("userAvatar").toString()
		setToObjectViews(userName,userAvatar)

		println(userId+userName+userAvatar)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.createFriendRequestActivity)
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
		//set alert notification
		objectViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})

	}

	private fun setToObjectViews(name:String?,avatar:String?){
		val avatarView = findViewById<ImageView>(R.id.createFriendRequestActivityAvatar)
		val nameView = findViewById<TextView>(R.id.createFriendRequestActivityName)
		Genaral.setUserAvatarImage(this , avatar , avatarView)
		nameView.text = name
	}
	
	
	fun askCreateRequest(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want create this request.",
			fun(dialogInterface: DialogInterface , i:Int){
				createFriendRequest()
			}
		)
	}
	private fun createFriendRequest(){
		val content = findViewById<EditText>(R.id.createFriendRequestActivityContent).text.toString()

		objectViewModel.createFriendRequest(
			userId,
			content,
			::createRequestOkCallback
		)
	}
	private fun createRequestOkCallback(){
		this.finish()
	}
	
}