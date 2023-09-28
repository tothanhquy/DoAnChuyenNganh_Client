package com.example.tcapp.view.team_profile

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
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.post.CreatePostActivity
import com.example.tcapp.view.post.PostsListActivity
import com.example.tcapp.view.request.CreateRequestActivity
import com.example.tcapp.view.request.RequestsListActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel

const val CHOOSE_AVATAR_REQUEST_CODE = 110
class TeamProfileActivity : CoreActivity() {
	private lateinit var objectViewModel: TeamProfileViewModel;
	private var teamId:String?=null;
	private var leaderId:String?=null;
	private var isLeader:Boolean=false;
	
	private var teamMaxim:String?=null;
	private var teamDescription:String?=null;
	private var teamName:String?=null;
	private var teamAvatar:String?=null;
	private var teamInternalInfo:String?=null;
	
	private var isLoadFullMember:Boolean=false;
	
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var skillsContainer:RecyclerView?= null;
	private var membersContainer:RecyclerView?= null;
	private var newLeaderSelectContainer:RecyclerView?= null;
	
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = TeamProfileViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.team_profile)
		setContentView(R.layout.activity_team_profile)
		
		skillsContainer =  findViewById<RecyclerView>(R.id.teamProfileActivitySkills);
		membersContainer =  findViewById<RecyclerView>(R.id.teamProfileActivityMembers);
		newLeaderSelectContainer =  findViewById<RecyclerView>(R.id.teamProfileActivityChooseNewLeaderRecyclerView);
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == RESULT_OK) {
			val selectedUri : Uri = data!!.data
				?: return //The uri with the location of the file
//
			val selectedPath = Genaral.URIPathHelper().getPath(applicationContext, selectedUri!!)
			objectViewModel.changeAvatar(selectedPath, teamId)
		}
	}
	
	private fun loadData() {
		teamId = intent.getStringExtra("teamId").toString()
		println(teamId)
		objectViewModel.loadData(teamId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.teamProfileActivity)
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
		//set avatar
		objectViewModel.teamAvatar.observe(this, Observer {
			runOnUiThread {
				setTeamAvatar(it)
			}
		})
		//set user data
		objectViewModel.teamProfile.observe(this, Observer {
			runOnUiThread {
				setInfo(it)
				setSkillsContainer(it.skills)
				setMembersContainer(it.members)
			}
		})
		//set full member
//		objectViewModel.fullMembers.observe(this, Observer {
//			runOnUiThread {
//				if(it!=null){
//					setSelectNewLeaderContainer(it)
//				}
//			}
//		})
	}
	private fun setTeamAvatar(avatarPath:String?){
		try{
			if(avatarPath!=null){
				teamAvatar = avatarPath
				val avatar = findViewById<ImageView>(R.id.teamProfileActivityTeamAvatar);
				setTeamAvatarImage(this,avatarPath,avatar)
				
			}
		}catch(e:Exception){}
	}
	private fun setInfo(info: TeamProfileModels.TeamProfile?){
		if(info!=null) {
			leaderId = info.leaderId;
			isLeader = info.relationship!=null&&info.relationship!!.contains("leader");
			//set leader avatar
			val leaderAvatar = findViewById<ImageView>(R.id.teamProfileActivityLeaderAvatar);
//			if(info.leaderAvatar!=null&&info.leaderAvatar!="")
//				Glide.with(this)
//					.load(info.leaderAvatar)
//					.error(R.drawable.default_user_avatar)
//					.into(leaderAvatar)
			setUserAvatarImage(this,info.leaderAvatar,leaderAvatar)
			//set team avatar
			val teamAvatar = findViewById<ImageView>(R.id.teamProfileActivityTeamAvatar);
//			if(info.teamAvatar!=null&&info.teamAvatar!="")
//				Glide.with(this)
//					.load(info.teamAvatar)
//					.error(R.drawable.default_user_avatar)
//					.into(teamAvatar)
//			setTeamAvatarImage(this,info.teamAvatar,teamAvatar)
			setTeamAvatar(info.teamAvatar)
			
			findViewById<TextView>(R.id.teamProfileActivityTeamName).text =
				info.teamName;
			teamName = info.teamName;
			findViewById<TextView>(R.id.teamProfileActivityTeamMaxim).text =
				info.maxim;
			teamMaxim = info.maxim;
			findViewById<TextView>(R.id.teamProfileActivityTeamDescription).text =
				info.description;
			teamDescription = info.description;
			val internalInfo = findViewById<TextView>(R.id.teamProfileActivityTeamInternalInfo);
			internalInfo.text =
				info.internalInfo;
			teamInternalInfo = info.internalInfo;
			internalInfo.visibility = if(info.relationship!=null&&info.relationship!!.contains("member"))View.VISIBLE else View.GONE;
			setOptions(info.relationship?:"");
		}
	}
	private fun setOptions(relationship:String){
		val editAvatar = findViewById<Button>(R.id.teamProfileActivityOptionEditAvatar);
		editAvatar.visibility = View.GONE;
		val viewMembers = findViewById<Button>(R.id.teamProfileActivityOptionViewMember);
		viewMembers.visibility = View.VISIBLE;
		val editInfo = findViewById<Button>(R.id.teamProfileActivityOptionEditInfo);
		editInfo.visibility = View.GONE;
		val exitTeam = findViewById<Button>(R.id.teamProfileActivityOptionExitTeam);
		exitTeam.visibility = View.GONE;
		val createJoinRequest = findViewById<Button>(R.id.teamProfileActivityOptionCreateJoinRequest);
		createJoinRequest.visibility = View.GONE;
		val createPost = findViewById<Button>(R.id.teamProfileActivityOptionCreatePost);
		createPost.visibility = View.GONE;
		val viewRequestOfTeam = findViewById<Button>(R.id.teamProfileActivityOptionViewRequestOfTeam);
		viewRequestOfTeam.visibility = View.GONE;
		
		
		if(relationship.contains("leader")){
			editAvatar.visibility=View.VISIBLE;
			editInfo.visibility=View.VISIBLE;
			viewRequestOfTeam.visibility=View.VISIBLE;
			createPost.visibility = View.VISIBLE;
		}
		if(relationship.contains("userlogin")){
			//request join
			
			if(!relationship.contains("leader")&&!relationship.contains("member")){
				createJoinRequest.visibility = View.VISIBLE;
			}
		}
		if(relationship.contains("guest")){
		
		}
		if(relationship.contains("member")){
			exitTeam.visibility=View.VISIBLE;
		}
	}
	private fun setSkillsContainer(skills: ArrayList<TeamProfileModels.TeamProfileSkills>?){
		if(skills!=null){
			//insert new item
			var skillsAdapter = TeamProfileSkillsRecyclerAdapter(applicationContext,skills)
			skillsContainer!!.setHasFixedSize(true)
			skillsContainer!!.layoutManager =
				Genaral.Static.HorizontalLayoutAutoWrapper(this)
			skillsContainer!!.adapter = skillsAdapter;
		}
	}
	private fun setMembersContainer(members: ArrayList<TeamProfileModels.TeamProfileMembers>?){
		if(members!=null){
			var membersAdapter = TeamProfileMembersRecyclerAdapter(applicationContext,members)
			membersContainer!!.setHasFixedSize(true)
			membersContainer!!.layoutManager = Genaral.Static.HorizontalLayoutAutoWrapper(this)
			membersContainer!!.adapter = membersAdapter;
		}
	}
	private fun setSelectNewLeaderContainer(members: ArrayList<TeamProfileModels.Member>?){
		if(members!=null){
			var membersAdapter = TeamProfileSelectNewLeaderRecyclerAdapter(applicationContext,
				members
			)
			newLeaderSelectContainer!!.setHasFixedSize(true)
			newLeaderSelectContainer!!.layoutManager = LinearLayoutManager(this)
			membersAdapter.setCallback(::newLeaderSelectItemChoose)
			newLeaderSelectContainer!!.adapter = membersAdapter;
		}
		isLoadFullMember=true;
	}
	fun changeAvatar(view: View){
		if(checkPermissionsReadFile()){
			var intent:Intent =  Intent()
				.setType("image/*")
				.setAction(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(Intent.createChooser(intent, "Select  avatar"), CHOOSE_AVATAR_REQUEST_CODE);
		}else{
			requestPermissionsReadFile()
		}
	}
	
	fun viewMembersNavigation(view: View){
		val intent = Intent(applicationContext , TeamProfileViewMembersListActivity::class.java)
		intent.putExtra("teamId", teamId)
		
		startActivity(intent)
	}
	fun openLeaderProfileNavigation(view: View){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", leaderId)
		startActivity(intent)
	}
	fun viewPostsNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", "team");
		intent.putExtra("teamId", teamId);
		startActivity(intent)
	}
	fun createPostNavigation(view: View){
		val intent = Intent(applicationContext , CreatePostActivity::class.java)
		intent.putExtra("creator", "leader");
		intent.putExtra("teamId", teamId);
		intent.putExtra("name", teamName?:"");
		intent.putExtra("avatar", teamAvatar?:"");
		startActivity(intent);
	}
	fun openEditInfoNavigation(view: View){
		val intent = Intent(applicationContext , TeamProfileEditInfoActivity::class.java)
		intent.putExtra("teamId", teamId);
		intent.putExtra("name", teamName);
		intent.putExtra("maxim", teamMaxim);
		intent.putExtra("description", teamDescription);
		intent.putExtra("internalInfo", teamInternalInfo);
		startActivity(intent)
	}
	fun exitTeamNavigation(view: View){
		if(isLeader&&objectViewModel.fullMembers!!.size!=0){
			if(! isLoadFullMember){
				setSelectNewLeaderContainer(objectViewModel.fullMembers)
			}
			openNewLeaderSelect()
		}else{
			this.showAskDialog(
				"Important!",
				"Do you really want to exit this team.",
				fun(dialogInterface: DialogInterface , i:Int){
					objectViewModel.exitTeam(teamId,null,::exitTeamSuccessCallback)
				}
			)
		}
	}
	private fun exitTeamSuccessCallback(){
		println("callback")
		closeOptions(View(applicationContext))
		closeNewLeaderSelect(View(applicationContext))
		objectViewModel.loadData(teamId)
	}
	fun openOptions(view:View){
		findViewById<ViewGroup>(R.id.teamProfileActivityOptions).visibility = View.VISIBLE
	}
	fun closeOptions(view:View){
		findViewById<ViewGroup>(R.id.teamProfileActivityOptions).visibility = View.GONE
	}
	fun createJoinRequestNavigation(view:View){
		val intent = Intent(applicationContext , CreateRequestActivity::class.java)
		intent.putExtra("creator", "user");
		intent.putExtra("teamId", teamId);
		intent.putExtra("teamName", teamName);
		intent.putExtra("teamAvatar", teamAvatar);
		startActivity(intent)
	}
	fun viewRequestsOfTeamNavigation(view:View){
		val intent = Intent(applicationContext , RequestsListActivity::class.java)
		intent.putExtra("viewer", "leader");
		intent.putExtra("teamId", teamId);
		startActivity(intent)
	}
	
	private fun newLeaderSelectItemChoose(idMember:String? , name:String?){
		this.showAskDialog(
			"Important!",
			"Do you really want to exit this team and trade leader position for $name.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.exitTeam(teamId,idMember,::exitTeamSuccessCallback)
			}
		)
	}
	private fun openNewLeaderSelect(){
		findViewById<ViewGroup>(R.id.teamProfileActivityChooseNewLeader).visibility = View.VISIBLE
	}
	fun closeNewLeaderSelect(view:View){
		findViewById<ViewGroup>(R.id.teamProfileActivityChooseNewLeader).visibility = View.GONE
	}
	
	
}