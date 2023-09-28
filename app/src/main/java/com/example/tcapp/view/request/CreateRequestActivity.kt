package com.example.tcapp.view.request

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
import com.example.tcapp.viewmodel.request.CreateRequestViewModel
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel

const val MESSAGE_HAS_NOT_ANY_TEAM = "Oh no! You are not leader of any teams. Let create your team before create request.";
const val MESSAGE_LET_SELECT_A_TEAM = "Let select a team, which you want to add member ";

class CreateRequestActivity : CoreActivity() {
	private lateinit var objectViewModel: CreateRequestViewModel;
	private var teamId:String?=null;
	private var teamName:String?=null;
	private var teamAvatar:String?=null;
	private var userId:String?=null;
	private var userName:String?=null;
	private var userAvatar:String?=null;
	
	private lateinit var creator: RequestModels.RequestViewer;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var  title:View? = null;
	private var  content:View? = null;
	
	private var teamsContainer:RecyclerView?= null;
	private var messageSelectTeamView:TextView? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = CreateRequestViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.create_request)
		setContentView(R.layout.activity_create_request)
		
		teamsContainer =  findViewById<RecyclerView>(R.id.createRequestActivitySelectTeamRecyclerView);
		
		messageSelectTeamView = findViewById<TextView>(R.id.createRequestActivitySelectTeamMessage);
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
	}
	
	
	private fun loadData() {
		val creatorString = intent.getStringExtra("creator").toString()
		creator = if(creatorString=="leader"){
			RequestModels.RequestViewer.LEADER;
		}else{
			RequestModels.RequestViewer.USER;
		}
		teamId = intent.getStringExtra("teamId")?.toString()
		teamName = intent.getStringExtra("teamName").toString()
		teamAvatar = intent.getStringExtra("teamAvatar").toString()
		
		if(creatorString=="leader"){
			userId = intent.getStringExtra("userId")?.toString()
			userName = intent.getStringExtra("userName").toString()
			userAvatar = intent.getStringExtra("userAvatar").toString()
			setToObjectViews(userName,userAvatar)
			findViewById<ViewGroup>(R.id.createRequestActivityTeamContainer).visibility=View.VISIBLE
			objectViewModel.loadTeamsOfLeader()
		}else{
			setToObjectViews(teamName,teamAvatar)
			findViewById<ViewGroup>(R.id.createRequestActivityTeamContainer).visibility=View.GONE;
		}
		println(teamId+teamName+teamAvatar)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.createRequestActivity)
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
//		set team
		objectViewModel.myTeams.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setSelectTeamContainer(it)
				}
			}
		})
	}
	
	private fun setSelectTeamContainer(teams: ArrayList<TeamProfileModels.TeamsOfLeaderItem>?){
		if(teams!=null){
			var teamsAdapter = TeamsOfLeaderSelectRecyclerAdapter(applicationContext,
				teams
			)
			teamsContainer!!.setHasFixedSize(true)
			teamsContainer!!.layoutManager = LinearLayoutManager(this)
			teamsAdapter.setCallback(::changeTeamView)
			teamsContainer!!.adapter = teamsAdapter;
			
			when (teams.size) {
				0 -> {
					messageSelectTeamView!!.text = MESSAGE_HAS_NOT_ANY_TEAM
				}
				1 -> {
					//only one team
					val onlyTeam = teams[0]
					teamId = onlyTeam.teamId
					teamName = onlyTeam.teamName
					teamAvatar = onlyTeam.teamAvatar
					setTeamView()
					messageSelectTeamView!!.visibility = View.GONE
					findViewById<TextView>(R.id.createRequestActivityChooseTeam).visibility = View.GONE
				}
				else -> {
					messageSelectTeamView!!.text = MESSAGE_LET_SELECT_A_TEAM
				}
			}
		}
	}
	private fun changeTeamView(id:String,name:String?,avatar:String?,){
		if(creator==RequestModels.RequestViewer.LEADER){
			messageSelectTeamView!!.visibility = View.GONE
			closeSelection(View(this))
			teamId = id;
			teamName=name;
			teamAvatar = avatar;
			setTeamView()
		}
	}
	
	private fun setTeamView(){
		val avatarView = findViewById<ImageView>(R.id.createRequestActivityTeamAvatar)
		val nameView = findViewById<TextView>(R.id.createRequestActivityTeamName)
		Genaral.setTeamAvatarImage(this , teamAvatar , avatarView)
		nameView.text = teamName
	}
	private fun setToObjectViews(name:String?,avatar:String?){
		val avatarView = findViewById<ImageView>(R.id.createRequestActivityToAvatar)
		val nameView = findViewById<TextView>(R.id.createRequestActivityToName)
		if(creator==RequestModels.RequestViewer.LEADER){
			Genaral.setUserAvatarImage(this , avatar , avatarView)
		}else{
			Genaral.setTeamAvatarImage(this , avatar , avatarView)
		}
		nameView.text = name
	}
	
	
	fun askCreateRequest(view: View){
		this.showAskDialog(
			"Important!",
			"Do you really want create this request.",
			fun(dialogInterface: DialogInterface , i:Int){
				createRequest()
			}
		)
	}
	private fun createRequest(){
		val title = findViewById<EditText>(R.id.createRequestActivityTitle).text.toString()
		val content = findViewById<EditText>(R.id.createRequestActivityContent).text.toString()
		val creatorString = if(creator==RequestModels.RequestViewer.LEADER)"leader" else "user";
		
		objectViewModel.createRequest(
			creatorString,
			teamId,
			userId,
			title,
			content,
			::createRequestOkCallback
		)
	}
	private fun createRequestOkCallback(){
		this.finish()
	}
	fun openSelection(view:View){
		findViewById<ViewGroup>(R.id.createRequestActivitySelectTeamContainer).visibility = View.VISIBLE
	}
	fun closeSelection(view:View){
		findViewById<ViewGroup>(R.id.createRequestActivitySelectTeamContainer).visibility = View.GONE
	}
	
}