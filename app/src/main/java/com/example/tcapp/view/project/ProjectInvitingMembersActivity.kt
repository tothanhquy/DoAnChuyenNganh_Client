package com.example.tcapp.view.project

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.ProjectInvitingMembersRecyclerAdapter
import com.example.tcapp.view.adapter_view.ProjectInvitingProjectsRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.project.ProjectInvitingMembersViewModel

class ProjectInvitingMembersActivity : CoreActivity() {
	private lateinit var objectViewModel: ProjectInvitingMembersViewModel;
	
	private var projectId:String?=null;
	private var viewer:String?=null;

	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var viewContainer:RecyclerView?= null;
	private var invitingProjectsContainerAdapter: ProjectInvitingProjectsRecyclerAdapter?= null;
	private var invitingMembersContainerAdapter: ProjectInvitingMembersRecyclerAdapter?= null;

	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ProjectInvitingMembersViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.project_inviting_members)
		setContentView(R.layout.activity_project_inviting_members)

		viewContainer =  findViewById<RecyclerView>(R.id.projectInvitingMembersActivityRecyclerView);

		initViews()
		setRender()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString()
		viewer = intent.getStringExtra("viewer").toString()
		if(viewer.equals("leader")){
			objectViewModel.loadInvitingMembers(projectId)
		}else{
			objectViewModel.loadInvitingProjects()
		}
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectInvitingMembersActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
//	@SuppressLint("SuspiciousIndentation")
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
		objectViewModel.invitingMembers.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setMembersContainer(it)
			}
		})
		//set user data
		objectViewModel.invitingProjects.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setProjectsContainer(it)
			}
		})
	}

	private fun setProjectsContainer(projects: ArrayList<ProjectModels.InvitingProject>){
		projects.sortByDescending{ it.time }
		invitingProjectsContainerAdapter = ProjectInvitingProjectsRecyclerAdapter(applicationContext,projects)
		invitingProjectsContainerAdapter!!.setOpenCallback(::openProject);
		invitingProjectsContainerAdapter!!.setUpdateCallback(::answerRequest);
		viewContainer!!.setHasFixedSize(true)
		viewContainer!!.layoutManager = LinearLayoutManager(this)
		viewContainer!!.adapter = invitingProjectsContainerAdapter;
	}
	private fun openProject(idProject:String){
		val intent = Intent(applicationContext ,ProjectDetailsActivity::class.java)
		intent.putExtra("projectId", idProject)
		startActivity(intent)
	}
	private fun answerRequest(idProject:String,isAgree:Boolean=false){
		if(isAgree){
			this.showAskDialog(
				"Important!",
				"Do you really want to agree this request.",
				fun(dialogInterface: DialogInterface , i:Int){
					objectViewModel.update(idProject,null,"agree",::deleteRequestOkCallback)
				}
			)
		}else{
			this.showAskDialog(
				"Important!",
				"Do you really want to disagree this request.",
				fun(dialogInterface: DialogInterface , i:Int){
					objectViewModel.update(idProject,null,"disagree",::deleteRequestOkCallback)
				}
			)
		}
	}
	private fun deleteRequestOkCallback(idProject:String?,idMember:String?){
		runOnUiThread {
			if(viewer==="leader"){
				invitingMembersContainerAdapter?.deleteId(idMember)
			}else{
				invitingProjectsContainerAdapter?.deleteId(idProject)
			}
		}
	}

	private fun setMembersContainer(members: ArrayList<ProjectModels.InvitingMember>){
		members.sortByDescending{ it.time }
		invitingMembersContainerAdapter = ProjectInvitingMembersRecyclerAdapter(applicationContext,members)
		invitingMembersContainerAdapter!!.setOpenCallback(::openUserProfile);
		invitingMembersContainerAdapter!!.setUpdateCallback(::cancelRequest);
		viewContainer!!.setHasFixedSize(true)
		viewContainer!!.layoutManager = LinearLayoutManager(this)
		viewContainer!!.adapter = invitingMembersContainerAdapter;
	}
	private fun openUserProfile(idUser:String){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idUser)
		startActivity(intent)
	}
	private fun cancelRequest(idMember: String?){
		this.showAskDialog(
			"Important!",
			"Do you really want to cancel this request.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.update(projectId,idMember,"cancel",::deleteRequestOkCallback)
			}
		)
	}

}