package com.example.tcapp.view.project

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.ProjectViewMembersHistoryRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.project.ProjectViewMembersHistoryViewModel

class ProjectViewMembersHistoryActivity : CoreActivity() {
	private lateinit var objectViewModel: ProjectViewMembersHistoryViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var membersContainer:RecyclerView?= null;
	private var membersContainerAdapter: ProjectViewMembersHistoryRecyclerAdapter?= null;
	
	private var projectId:String? = null;

	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ProjectViewMembersHistoryViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.members)
		setContentView(R.layout.activity_project_view_members_history)
		
		membersContainer =  findViewById<RecyclerView>(R.id.projectViewMembersHistoryActivityRecyclerView);
		
		membersContainerAdapter = ProjectViewMembersHistoryRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		membersContainerAdapter!!.setCallbackOfViewUser(::openMemberProfile);
		membersContainer!!.setHasFixedSize(true)
		membersContainer!!.layoutManager = LinearLayoutManager(this)
		membersContainer!!.adapter = membersContainerAdapter;

		initViews()
		setRender()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString();
		objectViewModel.loadMember(projectId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectViewMembersHistoryActivity)
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

		objectViewModel.membersHistory.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setMembersContainer(it.members)
				}
			}
		})
	}
	
	private fun setMembersContainer(members: ArrayList<ProjectModels.MemberHistory>?){
		if(members!=null){
			membersContainer!!.setHasFixedSize(true)
			membersContainerAdapter!!.setInitList(members)
		}
	}
	
	private fun openMemberProfile(idUser:String){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idUser)
		startActivity(intent)
	}
}