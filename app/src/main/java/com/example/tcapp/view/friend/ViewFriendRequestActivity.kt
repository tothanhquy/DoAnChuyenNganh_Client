package com.example.tcapp.view.friend

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.friend.ViewFriendRequestViewModel
import com.example.tcapp.viewmodel.request.ViewRequestViewModel

class ViewFriendRequestActivity : CoreActivity() {
	private lateinit var objectViewModel: ViewFriendRequestViewModel;

	private var requestId:String?=null;
	private var userId:String?=null;
	private lateinit var method: FriendModels.RequestMethod;

	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ViewFriendRequestViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.details_request)
		setContentView(R.layout.activity_view_friend_request)
		
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		requestId = intent.getStringExtra("requestId").toString()
		val methodString = intent.getStringExtra("method").toString()
		method = if(methodString.equals("send")) FriendModels.RequestMethod.SEND else FriendModels.RequestMethod.RECEIVE;
		objectViewModel.loadRequest(methodString,requestId);
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.viewFriendRequestActivity)
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
		
		//set data
		objectViewModel.request.observe(this, Observer {
			runOnUiThread {
				if(it!=null)
					setRequestViews(it)
			}
		})
		
	}
	
	private fun setRequestViews(request: FriendModels.Request){
		val requestTime = findViewById<TextView>(R.id.viewFriendRequestActivityRequestTime);
		val userAvatar = findViewById<ImageView>(R.id.viewFriendRequestActivityUserAvatar);
		val userName = findViewById<TextView>(R.id.viewFriendRequestActivityUserName);
		val content = findViewById<TextView>(R.id.viewFriendRequestActivityContent);
		
		val btnAgree = findViewById<Button>(R.id.viewFriendRequestActivityOptionAgree);
		val btnDisagree = findViewById<Button>(R.id.viewFriendRequestActivityOptionDisagree);
		val btnCancel = findViewById<Button>(R.id.viewFriendRequestActivityOptionCancel);
		
		requestTime.text = "Thời gian: "+Genaral.getDateTimeByUTC(request.time);
		//user
		setUserAvatarImage(this,request.userAvatar,userAvatar)
		userName.text = request.userName;
		userId = request.userId
		
		content.text = request.content;
		
		//set btn
		btnAgree.visibility = View.GONE;
		btnDisagree.visibility = View.GONE;
		btnCancel.visibility = View.GONE;
		if(method==FriendModels.RequestMethod.SEND) {
			btnCancel.visibility = View.VISIBLE;
		}else{
			btnAgree.visibility = View.VISIBLE;
			btnDisagree.visibility = View.VISIBLE;
		}
	}

	fun cancelRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to cancel this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				responseRequest("cancel");
			}
		)
	}
	
	fun agreeRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to agree this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				responseRequest("agree");
			}
		)
	}
	fun disagreeRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to disagree this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				responseRequest("disagree");
			}
		)
	}

	private fun responseRequest(status:String){
		val methodString  = if(method==FriendModels.RequestMethod.SEND)"send" else "receive";
		objectViewModel.responseRequest(methodString,requestId,status,::responseOkCallback)
	}
	
	private fun responseOkCallback(){
		this.finish()
	}

	fun openUser(view:View){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", userId);
		startActivity(intent)
	}
}