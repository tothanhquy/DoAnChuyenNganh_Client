package com.example.tcapp.view.project

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
import com.example.tcapp.core.Genaral.Static.setUserAvatarImage
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.chanel_chat.ChanelChatDetailsActivity
import com.example.tcapp.view.post.CreatePostActivity
import com.example.tcapp.view.post.PostsListActivity
import com.example.tcapp.view.request.CreateRequestActivity
import com.example.tcapp.view.request.RequestsListActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.project.ProjectDetailsViewModel

const val CHOOSE_AVATAR_REQUEST_CODE = 112
class ProjectDetailsActivity : CoreActivity() {
	private lateinit var objectViewModel: ProjectDetailsViewModel;
	private var projectId:String?=null;
	private var projectName:String?=null;
	private var leaderId:String?=null;
	private var isLeader:Boolean=false;
	private var isFollow:Boolean=false;

	private var projectAvatar:String?=null;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var categoryKeywordsContainer:RecyclerView?= null;
	private var membersContainer:RecyclerView?= null;
	private var tagsContainer:RecyclerView?= null;
	private var newLeaderSelectContainer:RecyclerView?= null;
	
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ProjectDetailsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.project_details)
		setContentView(R.layout.activity_project_details)

		categoryKeywordsContainer =  findViewById<RecyclerView>(R.id.projectDetailsActivityProjectCategoryKeywordsRecyclerView);
		membersContainer =  findViewById<RecyclerView>(R.id.projectDetailsActivityProjectMembersRecyclerView);
		tagsContainer =  findViewById<RecyclerView>(R.id.projectDetailsActivityProjectTagsRecyclerView);
		newLeaderSelectContainer =  findViewById<RecyclerView>(R.id.projectDetailsActivityChooseNewLeaderRecyclerView);
		
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
			objectViewModel.changeAvatar(selectedPath, projectId)
		}
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString()
		println(projectId)
		objectViewModel.loadData(projectId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectDetailsActivity)
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
		objectViewModel.projectAvatar.observe(this, Observer {
			runOnUiThread {
				setProjectAvatar(it)
			}
		})
		//set user data
		objectViewModel.projectDetails.observe(this, Observer {
			runOnUiThread {
				setInfo(it)
				setCategoryKeywordsContainer(it.categoryKeywords)
				setMembersContainer(it.members)
				setTagsContainer(it.tags)
				setSelectNewLeaderContainer(it.members)
			}
		})
	}
	private fun setProjectAvatar(avatarPath:String?){
		try{
			if(avatarPath!=null){
				projectAvatar = avatarPath
				val avatar = findViewById<ImageView>(R.id.projectDetailsActivityProjectAvatar);
				Genaral.setProjectImageWithPlaceholder(this,avatarPath,avatar)
			}
		}catch(e:Exception){}
	}
	private fun setInfo(info: ProjectModels.ProjectDetails?){
		if(info!=null) {
			leaderId = info.leaderId;
			projectName = info.projectName;
			isFollow = info.isFollow;
			isLeader = info.relationship!=null&&info.relationship!!.contains("leader");
			//set leader avatar
			val leaderAvatar = findViewById<ImageView>(R.id.projectDetailsActivityLeaderAvatar);
			setUserAvatarImage(this,info.leaderAvatar,leaderAvatar)
			setProjectAvatar(info.projectAvatar)
			
			findViewById<TextView>(R.id.projectDetailsActivityProjectName).text =
				info.projectName;
			findViewById<TextView>(R.id.projectDetailsActivityProjectSlogan).text =
				info.slogan;
			findViewById<TextView>(R.id.projectDetailsActivityProjectDescription).text =
				info.description;
			findViewById<TextView>(R.id.projectDetailsActivityProjectFollowNumber).text =
				if(info.followsNumber!=1)info.followsNumber.toString()+" follows" else "1 follow";
			findViewById<TextView>(R.id.projectDetailsActivityProjectVoteStarNumber).text =
				if(info.voteStar==0F)"" else info.voteStar.toString();
			findViewById<TextView>(R.id.projectDetailsActivityProjectImagesButton).text =
				if(info.imagesNumber!=1)info.imagesNumber.toString()+" Images" else "1 Image";
			findViewById<TextView>(R.id.projectDetailsActivityProjectVideosButton).text =
				if(info.videosNumber!=1)info.videosNumber.toString()+" Videos" else "1 Video";
			findViewById<TextView>(R.id.projectDetailsActivityProjectReportsButton).text =
				if(info.reportsNumber!=1)info.reportsNumber.toString()+" Reports" else "1 Report";

			setOptionsAndButton(info.relationship?:"",info.isFollow);
		}
	}
	private fun setOptionsAndButton(relationship:String,isFollow:Boolean=false){
		val editAvatar = findViewById<Button>(R.id.projectDetailsActivityOptionEditAvatar);
		editAvatar.visibility = View.GONE;
		val editInfo = findViewById<Button>(R.id.projectDetailsActivityOptionEditInfo);
		editInfo.visibility = View.GONE;
		val exitProject = findViewById<Button>(R.id.projectDetailsActivityOptionExitProject);
		exitProject.visibility = View.GONE;
		val invitingMembers = findViewById<Button>(R.id.projectDetailsActivityOptionOpenInvitingMembers);
		invitingMembers.visibility = View.GONE;
		val changeCategoryKeywords = findViewById<Button>(R.id.projectDetailsActivityOptionChangeCategoryKeywords);
		changeCategoryKeywords.visibility = View.GONE;

		val follow = findViewById<Button>(R.id.projectDetailsActivityProjectFollowButton);
		follow.visibility = View.GONE;
		val vote = findViewById<Button>(R.id.projectDetailsActivityProjectVoteStar);
		vote.visibility = View.GONE;
		val tagEdit = findViewById<Button>(R.id.projectDetailsActivityProjectEditTagsButton);
		tagEdit.visibility = View.GONE;
		
		if(relationship.contains("leader")){
			editAvatar.visibility=View.VISIBLE;
			editInfo.visibility=View.VISIBLE;
			tagEdit.visibility=View.VISIBLE;
			invitingMembers.visibility = View.VISIBLE;
			changeCategoryKeywords.visibility = View.VISIBLE;
		}
		if(relationship.contains("userlogin")){
			//request join
			vote.visibility = View.VISIBLE;
			follow.visibility = View.VISIBLE;
			if(isFollow){
				follow.text = "Unfollow";
				follow.setTextAppearance(applicationContext,R.style.ActionBtn2);
			}else{
				follow.text = "Follow";
				follow.setTextAppearance(applicationContext,R.style.ActionBtn);
			}
		}
		if(relationship.contains("guest")){
		
		}
		if(relationship.contains("member")){
			exitProject.visibility=View.VISIBLE;
		}
	}
	private fun setCategoryKeywordsContainer(keywords: ArrayList<ProjectModels.CategoryKeyword>?){
		if(keywords!=null){
			//insert new item
			val setList = ArrayList(keywords.map{it.name}.toList())
			var keywordsAdapter = StringListRecyclerAdapter(applicationContext, setList);
			categoryKeywordsContainer!!.setHasFixedSize(true)
			categoryKeywordsContainer!!.layoutManager =
				Genaral.Static.HorizontalLayoutAutoWrapper(this)
			categoryKeywordsContainer!!.adapter = keywordsAdapter;
		}
	}
	private fun setTagsContainer(tags: ArrayList<String>?){
		if(tags!=null){
			val setList = ArrayList<String?>(tags)
			var keywordsAdapter = StringListRecyclerAdapter(applicationContext, setList);
			categoryKeywordsContainer!!.setHasFixedSize(true)
			categoryKeywordsContainer!!.layoutManager =
				Genaral.Static.HorizontalLayoutAutoWrapper(this)
			categoryKeywordsContainer!!.adapter = keywordsAdapter;
		}
	}
	private fun setMembersContainer(members: ArrayList<ProjectModels.MemberNow>?){
		if(members!=null){
			var subMembers:ArrayList<ProjectModels.MemberNow>?=null;
			if(members.size>ProjectDetailsMembersRecyclerAdapter.MAXIMUM_SHOW_MEMBER){
				//cut
				subMembers = ArrayList(members.subList(0,ProjectDetailsMembersRecyclerAdapter.MAXIMUM_SHOW_MEMBER+1));
				subMembers[subMembers.size].name = (members.size-ProjectDetailsMembersRecyclerAdapter.MAXIMUM_SHOW_MEMBER).toString();
			}else{
				subMembers = members;
			}
			var membersAdapter = ProjectDetailsMembersRecyclerAdapter(applicationContext,subMembers)
			membersContainer!!.setHasFixedSize(true)
			membersContainer!!.layoutManager = Genaral.Static.HorizontalLayoutAutoWrapper(this)
			membersContainer!!.adapter = membersAdapter;
		}
	}
	private fun setSelectNewLeaderContainer(members: ArrayList<ProjectModels.MemberNow>?){
		if(members!=null){
			val sunMember = ArrayList(members.filter { !it.isLeader })
			var membersAdapter = ProjectDetailsSelectNewLeaderRecyclerAdapter(applicationContext,
				sunMember
			)
			newLeaderSelectContainer!!.setHasFixedSize(true)
			newLeaderSelectContainer!!.layoutManager = LinearLayoutManager(this)
			membersAdapter.setCallback(::newLeaderSelectItemChoose)
			newLeaderSelectContainer!!.adapter = membersAdapter;
		}
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
		val intent = Intent(applicationContext , ProjectViewMembersListActivity::class.java)
		intent.putExtra("projectId", projectId)
		startActivity(intent)
	}
	fun openLeaderProfileNavigation(view: View){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", leaderId)
		startActivity(intent)
	}
	fun voteStarNavigation(view: View){
		val intent = Intent(applicationContext , ProjectVoteStarActivity::class.java)
		intent.putExtra("projectId", projectId);
		intent.putExtra("name", projectName);
		intent.putExtra("avatar", projectAvatar);
		startActivity(intent);
	}
	fun openImageResourcesNavigation(view: View){
		val intent = Intent(applicationContext , ProjectResourcesActivity::class.java)
		intent.putExtra("projectId", projectId);
		intent.putExtra("type", "image");
		startActivity(intent);
	}
	fun openVideoResourcesNavigation(view: View){
		val intent = Intent(applicationContext , ProjectResourcesActivity::class.java)
		intent.putExtra("projectId", projectId);
		intent.putExtra("type", "video");
		startActivity(intent);
	}
	fun openReportsNavigation(view: View){
		val intent = Intent(applicationContext , ProjectNegativeReportsActivity::class.java)
		intent.putExtra("projectId", projectId);
		startActivity(intent);
	}
	fun viewPostsNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", "project");
		intent.putExtra("projectId", projectId);
		startActivity(intent)
	}
	fun openEditCategoryKeywordsNavigation(view: View){
		val intent = Intent(applicationContext , ProjectEditCategoryKeywordsActivity::class.java)
		intent.putExtra("projectId", projectId);
		startActivity(intent);
	}
	fun openInvitingMembersNavigation(view: View){
		val intent = Intent(applicationContext , ProjectInvitingMembersActivity::class.java)
		intent.putExtra("projectId", projectId);
		intent.putExtra("viewer", "leader");
		startActivity(intent);
	}
	fun openEditTagsNavigation(view: View){
		val intent = Intent(applicationContext , ProjectEditTagsActivity::class.java)
		intent.putExtra("projectId", projectId);
		startActivity(intent)
	}
	fun openEditInfoNavigation(view: View){
		val intent = Intent(applicationContext , ProjectEditInfoActivity::class.java)
		intent.putExtra("projectId", projectId);
		startActivity(intent)
	}

	fun exitProjectNavigation(view: View){
		if(isLeader&&objectViewModel.projectDetails.value?.members?.size!=1){
			openNewLeaderSelect()
		}else{
			this.showAskDialog(
				"Important!",
				"Do you really want to exit this project.",
				fun(dialogInterface: DialogInterface , i:Int){
					objectViewModel.exitProject(projectId,null,::exitProjectSuccessCallback)
				}
			)
		}
	}
	private fun exitProjectSuccessCallback(){
		runOnUiThread {
			closeOptions(View(applicationContext))
			closeNewLeaderSelect(View(applicationContext))
			objectViewModel.loadData(projectId)
		}
	}

	fun toggleFollow(view: View){
		objectViewModel.toggleFollow(projectId,::toggleFollowSuccessCallback)
	}
	private fun toggleFollowSuccessCallback(totalFollows:Int,isFollow:Boolean){
		runOnUiThread {
			findViewById<TextView>(R.id.projectDetailsActivityProjectFollowNumber).text =
				if(totalFollows!=1) "$totalFollows follows" else "1 follow";
			val follow = findViewById<Button>(R.id.projectDetailsActivityProjectFollowButton);

			if(isFollow){
				follow.text = "Unfollow";
				follow.setTextAppearance(applicationContext,R.style.ActionBtn2);
			}else{
				follow.text = "Follow";
				follow.setTextAppearance(applicationContext,R.style.ActionBtn);
			}
		}
	}

	fun openOptions(view:View){
		findViewById<ViewGroup>(R.id.projectDetailsActivityOptions).visibility = View.VISIBLE
	}
	fun closeOptions(view:View){
		findViewById<ViewGroup>(R.id.projectDetailsActivityOptions).visibility = View.GONE
	}
	private fun newLeaderSelectItemChoose(idMember:String? , name:String?){
		this.showAskDialog(
			"Important!",
			"Do you really want to exit this project and trade leader position for $name.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.exitProject(projectId,idMember,::exitProjectSuccessCallback)
			}
		)
	}
	private fun openNewLeaderSelect(){
		findViewById<ViewGroup>(R.id.projectDetailsActivityChooseNewLeader).visibility = View.VISIBLE
	}
	fun closeNewLeaderSelect(view:View){
		findViewById<ViewGroup>(R.id.projectDetailsActivityChooseNewLeader).visibility = View.GONE
	}
	
	
}