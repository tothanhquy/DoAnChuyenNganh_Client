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
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.view.adapter_view.MyTeamsRecyclerAdapter
import com.example.tcapp.view.adapter_view.ProjectViewMembersNowRecyclerAdapter
import com.example.tcapp.view.adapter_view.TeamProfileViewMembersListRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.project.ProjectViewMembersNowViewModel
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewMembersListViewModel

class ProjectViewMembersNowActivity : CoreActivity() {
	private lateinit var objectViewModel: ProjectViewMembersNowViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var membersContainer:RecyclerView?= null;
	private var membersContainerAdapter: ProjectViewMembersNowRecyclerAdapter?= null;
	
	private var projectId:String? = null;
	private var memberFocusId:String? = null;
	private var memberFocusName:String? = null;
	private var memberFocusRole:String? = null;

	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ProjectViewMembersNowViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.members)
		setContentView(R.layout.activity_project_view_members_now)
		
		membersContainer =  findViewById<RecyclerView>(R.id.projectViewMembersNowActivityRecyclerView);
		
		membersContainerAdapter = ProjectViewMembersNowRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		membersContainerAdapter!!.setCallbackOfViewUser(::openMemberProfile);
		membersContainerAdapter!!.setCallbackOfOpenOptions(::openOptions);
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
		var viewActivity = findViewById<ViewGroup>(R.id.projectViewMembersNowActivity)
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

		objectViewModel.membersNow.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					membersContainerAdapter!!.setActionAble(it.isLeader)
					setMembersContainer(it.members)
				}
			}
		})
	}
	
	private fun setMembersContainer(members: ArrayList<ProjectModels.MemberNow>?){
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
	private fun openOptions(id:String , name:String?,role:String?){
		memberFocusId = id;
		memberFocusName = name;
		memberFocusRole = role;
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityOptions).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityOptions).visibility = View.GONE;
	}
	fun deleteMember(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to remove '$memberFocusName' out you project.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.deleteMember(projectId,memberFocusId,::deleteMemberOkCallback)
			}
		)
	}
	private fun deleteMemberOkCallback(id:String?){
		runOnUiThread {
			membersContainerAdapter?.deleteMember(id)
		}
	}

	fun openChangeRoleContainer(view:View){
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityChangeRoleContainer).visibility = View.VISIBLE;
		findViewById<TextView>(R.id.projectViewMembersNowActivityChangeRoleContainerMemberName).text = memberFocusName;
		findViewById<EditText>(R.id.projectViewMembersNowActivityChangeRoleContainerMemberName).setText(memberFocusRole);
		closeOptions(View(applicationContext))
	}
	fun closeChangeRoleContainer(view:View){
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityChangeRoleContainer).visibility = View.GONE;
	}
	fun changeRole(view:View){
		val newRole = findViewById<EditText>(R.id.projectViewMembersNowActivityChangeRoleContainerMemberRole).text.toString()
		this.showAskDialog(
			"Important!",
			"Do you really want to change role of '$memberFocusName' to '$newRole'.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.updateRoleMember(projectId,memberFocusId,newRole,::updateRoleOkCallback)
			}
		)
	}
	private fun updateRoleOkCallback(memberId:String?,role:String?){
		runOnUiThread {
			membersContainerAdapter?.updateRoleById(memberId,role)
			closeChangeRoleContainer(View(applicationContext))
		}
	}

	fun openInviteMemberContainer(view:View){
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityInviteMemberContainer).visibility = View.VISIBLE;
		findViewById<EditText>(R.id.projectViewMembersNowActivityInviteMemberContainerEmail).setText("");
		findViewById<EditText>(R.id.projectViewMembersNowActivityInviteMemberContainerRole).setText("");
		closeOptions(View(applicationContext))
	}
	fun closeInviteMemberContainer(view:View){
		findViewById<LinearLayout>(R.id.projectViewMembersNowActivityInviteMemberContainer).visibility = View.GONE;
	}
	fun inviteMember(view:View){
		val role = findViewById<EditText>(R.id.projectViewMembersNowActivityInviteMemberContainerRole).text.toString()
		val email = findViewById<EditText>(R.id.projectViewMembersNowActivityInviteMemberContainerEmail).text.toString()
		this.showAskDialog(
			"Important!",
			"Do you really want to invite user with email: '$email' to this project with role: '$role'",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.inviteMember(projectId,email,role,::inviteMemberOkCallback)
			}
		)
	}
	private fun inviteMemberOkCallback(){
		runOnUiThread {
			closeInviteMemberContainer(View(applicationContext))
		}
	}

	fun openMembersHistory(view:View){
		val intent = Intent(applicationContext ,ProjectViewMembersHistoryActivity::class.java)
		intent.putExtra("projectId", projectId)
		startActivity(intent)
	}
}