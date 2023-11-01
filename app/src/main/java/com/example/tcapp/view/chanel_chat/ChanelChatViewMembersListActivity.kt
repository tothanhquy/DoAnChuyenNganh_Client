package com.example.tcapp.view.chanel_chat

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.view.adapter_view.ChanelChatViewMembersListRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.chanel_chat.ChanelChatViewMembersListViewModel

class ChanelChatViewMembersListActivity : CoreActivity() {
	private lateinit var objectViewModel: ChanelChatViewMembersListViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var membersContainer:RecyclerView?= null;
	private var membersContainerAdapter:ChanelChatViewMembersListRecyclerAdapter?= null;
	
	private var chanelChatId:String? = null;
	private var accountId:String? = null;
	private var chanelChatType:ChanelChatModels.Type? = null;
	private var memberFocusId:String? = null;
	private var memberFocusName:String? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ChanelChatViewMembersListViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.members)
		setContentView(R.layout.activity_chanel_chat_members_list)
		
		membersContainer =  findViewById<RecyclerView>(R.id.chanelChatMembersListActivityRecyclerView);
		
		membersContainerAdapter = ChanelChatViewMembersListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		membersContainerAdapter!!.setCallbackOfViewUser(::openMemberProfile);
		membersContainerAdapter!!.setCallbackOfOpenOptions(::openOptions);
		membersContainer!!.setHasFixedSize(true)
		membersContainer!!.layoutManager = LinearLayoutManager(this)
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}

	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.create_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.create -> {
				if(chanelChatType==ChanelChatModels.Type.Group)
				openInsertMemberActivity()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun loadData() {
		chanelChatId = intent.getStringExtra("chanelChatId").toString();
		println(chanelChatId)
		objectViewModel.loadData(chanelChatId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.chanelChatMembersListActivity)
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
					chanelChatType = it.type
					accountId = it.accountId
					membersContainerAdapter!!.setActionAble(chanelChatType==ChanelChatModels.Type.Group&&it.isGroupOwner)
					membersContainerAdapter!!.setAccountId(accountId)
					setMembersContainer(it.members)
				}
			}
		})
	}
	
	private fun setMembersContainer(members: ArrayList<ChanelChatModels.Member>?){
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
		findViewById<LinearLayout>(R.id.chanelChatMembersListActivityOptions).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		findViewById<LinearLayout>(R.id.chanelChatMembersListActivityOptions).visibility = View.GONE;
	}
	fun deleteMember(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to remove $memberFocusName out your group chat.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.deleteMember(memberFocusId,chanelChatId)
			}
		)
	}
	private fun openInsertMemberActivity(){
		val intent = Intent(applicationContext , ChanelChatInsertMembersActivity::class.java)
		intent.putExtra("chanelChatId", chanelChatId)
		startActivity(intent)
	}
}