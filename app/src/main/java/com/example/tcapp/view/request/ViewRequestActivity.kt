package com.example.tcapp.view.request

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
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.request.ViewRequestViewModel

class ViewRequestActivity : CoreActivity() {
	private lateinit var objectViewModel: ViewRequestViewModel;
	private var teamId:String?=null;
	private var userId:String?=null;
	private var leaderId:String?=null;
	private var requestId:String?=null;
	private lateinit var viewer:RequestModels.RequestViewer;
	private var isImportant:Boolean = false;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ViewRequestViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.details_request)
		setContentView(R.layout.activity_view_request)
		
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		teamId = intent.getStringExtra("teamId").toString()
		requestId = intent.getStringExtra("requestId").toString()
		val viewerString = intent.getStringExtra("viewer").toString()
		
		objectViewModel.loadRequest(viewerString,requestId,teamId);
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.viewRequestActivity)
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
	
	private fun setRequestViews(request: RequestModels.Request){
		viewer = request.requestViewer;
		isImportant = request.isImportant;
		
		val requestTime = findViewById<TextView>(R.id.viewRequestActivityRequestTime);
		val status = findViewById<TextView>(R.id.viewRequestActivityStatus);
		val responseTime = findViewById<TextView>(R.id.viewRequestActivityResponseTime);
		val title = findViewById<TextView>(R.id.viewRequestActivityTitle);
		val teamAvatar = findViewById<ImageView>(R.id.viewRequestActivityTeamAvatar);
		val teamName = findViewById<TextView>(R.id.viewRequestActivityTeamName);
		val leaderContainer = findViewById<ViewGroup>(R.id.viewRequestActivityLeaderContainer);
		val leaderAvatar = findViewById<ImageView>(R.id.viewRequestActivityLeaderAvatar);
		val leaderName = findViewById<TextView>(R.id.viewRequestActivityLeaderName);
		val arrow = findViewById<ImageView>(R.id.viewRequestActivityArrow);
		val userAvatar = findViewById<ImageView>(R.id.viewRequestActivityUserAvatar);
		val userName = findViewById<TextView>(R.id.viewRequestActivityUserName);
		val content = findViewById<TextView>(R.id.viewRequestActivityContent);
		
		val btnAgree = findViewById<Button>(R.id.viewRequestActivityOptionAgree);
		val btnDisagree = findViewById<Button>(R.id.viewRequestActivityOptionDisagree);
		val btnCancel = findViewById<Button>(R.id.viewRequestActivityOptionCancel);
		
		requestTime.text = "Thời gian gửi: "+Genaral.getDateTimeByUTC(request.requestTime);
		if(request.isWaiting){
			status.text = "Trạng thái: Đang chờ phản hồi";
		}else if(!request.wasResponsed){
			status.text = "Trạng thái: Đã hủy";
		}else if(request.isAgree){
			status.text = "Trạng thái: Chấp nhận";
		}else{
			status.text = "Trạng thái: Từ chối";
		}
		
		if(request.wasResponsed){
			responseTime.text = "Thời gian trả lời: "+Genaral.getDateTimeByUTC(request.responseTime);
			responseTime.visibility = View.VISIBLE
		}else{
			responseTime.visibility = View.GONE
		}
		title.text = request.title;
		//team
		setTeamAvatarImage(this,request.teamAvatar,teamAvatar)
		teamName.text = request.teamName;
		teamId = request.teamId
		//leader
		if(request.teamLeaderId==null){
			leaderContainer.visibility = View.GONE;
		}else{
			leaderId = request.teamLeaderId
			leaderContainer.visibility = View.VISIBLE;
			setUserAvatarImage(this,request.teamLeaderAvatar,leaderAvatar)
			leaderName.text = request.teamLeaderName;
		}
		//arrow
		if(
			(
				viewer==RequestModels.RequestViewer.LEADER
					&&request.requestMethod==RequestModels.RequestMethod.SEND
				)||(
				viewer==RequestModels.RequestViewer.USER
					&&request.requestMethod==RequestModels.RequestMethod.RECEIVE
				)
		){
			arrow.rotation = 180f
		}
		//user
		setUserAvatarImage(this,request.userAvatar,userAvatar)
		userName.text = request.userName;
		userId = request.userId
		
		
		content.text = request.content;
		
		//set btn
		btnAgree.visibility = View.GONE;
		btnDisagree.visibility = View.GONE;
		btnCancel.visibility = View.GONE;
		if(request.isWaiting){
			if(request.requestMethod==RequestModels.RequestMethod.SEND){
				btnCancel.visibility = View.VISIBLE;
			}else{
				btnAgree.visibility = View.VISIBLE;
				btnDisagree.visibility = View.VISIBLE;
			}
		}
		
		setImportantView(request.isImportant,request.requestMethod)
	}
	private fun setImportantView(isImportant:Boolean,request:RequestModels.RequestMethod=RequestModels.RequestMethod.RECEIVE){
		val btnSetImportant = findViewById<Button>(R.id.viewRequestActivityOptionSetImportant);
		val btnCancelImportant = findViewById<Button>(R.id.viewRequestActivityOptionCancelImportant);
		val important = findViewById<TextView>(R.id.viewRequestActivityImportant);
		
		important.visibility = View.GONE;
		btnCancelImportant.visibility = View.GONE;
		btnSetImportant.visibility = View.GONE;
		
		if(request==RequestModels.RequestMethod.SEND)return;
		if(isImportant){
			important.visibility = View.VISIBLE;
			btnCancelImportant.visibility = View.VISIBLE;
		}else{
			btnSetImportant.visibility = View.VISIBLE;
		}
	}
	
	
	fun cancelRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to cancel this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				updateRequest("cancel");
			}
		)
	}
	
	fun agreeRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to agree this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				updateRequest("agree");
			}
		)
	}
	fun disagreeRequest(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to disagree this request?",
			fun(dialogInterface: DialogInterface , i:Int){
				updateRequest("disagree");
			}
		)
	}
	fun setImportantStatusOfRequest(view:View){
		val string = if(isImportant)"not" else "";
		this.showAskDialog(
			"Important!",
			"Do you really want to set this request is $string important?",
			fun(dialogInterface: DialogInterface , i:Int){
				updateRequest("important");
			}
		)
	}
	private fun updateRequest(status:String){
		val viewerString  = if(viewer==RequestModels.RequestViewer.LEADER)"leader" else "user";
		objectViewModel.updateRequest(viewerString,teamId,requestId,status,::updateOkCallback)
	}
	
	private fun updateOkCallback(status:String){
		if(status=="important"){
			isImportant = !isImportant;
			setImportantView(isImportant)
		}else{
			loadData()
		}
	}
	
	fun openTeam(view:View){
		val intent = Intent(applicationContext ,TeamProfileActivity::class.java)
		intent.putExtra("teamId", teamId);
		startActivity(intent)
	}
	fun openUser(view:View){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", userId);
		startActivity(intent)
	}
	fun openLeader(view:View){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", leaderId);
		startActivity(intent)
	}
}