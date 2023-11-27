package com.example.tcapp.view.team_profile

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.view.adapter_view.MyTeamsRecyclerAdapter
import com.example.tcapp.view.adapter_view.TeamProfileViewMembersListRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewMembersListViewModel

class TeamProfileViewMembersListActivity : CoreActivity() {
	private lateinit var objectViewModel: TeamProfileViewMembersListViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var membersContainer:RecyclerView?= null;
	private var membersContainerAdapter:TeamProfileViewMembersListRecyclerAdapter?= null;
	
	private var teamId:String? = null;
	private var memberFocusId:String? = null;
	private var memberFocusName:String? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = TeamProfileViewMembersListViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.members)
		setContentView(R.layout.activity_team_profile_members_list)
		
		membersContainer =  findViewById<RecyclerView>(R.id.teamProfileMembersListActivityRecyclerView);
		
		membersContainerAdapter = TeamProfileViewMembersListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		membersContainerAdapter!!.setCallbackOfViewUser(::openMemberProfile);
		membersContainerAdapter!!.setCallbackOfOpenOptions(::openOptions);
		membersContainer!!.setHasFixedSize(true)
		membersContainer!!.layoutManager = LinearLayoutManager(this)
		
		initViews()
		setRender()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		teamId = intent.getStringExtra("teamId").toString();
		println(teamId)
		objectViewModel.loadData(teamId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.teamProfileMembersListActivity)
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
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//handle delete member
		objectViewModel.memberDeletedId.observe(this, Observer {
			runOnUiThread {
				if(it!=null)
					membersContainerAdapter!!.deleteMember(it)
			}
		})
		//set user data
		objectViewModel.membersView.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					membersContainerAdapter!!.setActionAble(it.isLeader)
					setMembersContainer(it.members)
				}
			}
		})
	}
	
	private fun setMembersContainer(members: ArrayList<TeamProfileModels.Member>?){
		if(members!=null){
			membersContainerAdapter!!.setInitList(members)
			membersContainer!!.adapter = membersContainerAdapter;
		}
	}
	
	private fun openMemberProfile(idUser:String){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idUser)
		startActivity(intent)
	}
	private fun openOptions(id:String , name:String?){
		memberFocusId = id;
		memberFocusName = name;
		findViewById<LinearLayout>(R.id.teamProfileMembersListActivityOptions).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		findViewById<LinearLayout>(R.id.teamProfileMembersListActivityOptions).visibility = View.GONE;
	}
	fun deleteMember(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to remove $memberFocusName out you team.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.deleteMember(memberFocusId,teamId)
			}
		)
	}
}