package com.example.tcapp.view.chanel_chat

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.view.adapter_view.ChanelChatInsertMembersRecyclerAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.chanel_chat.ChanelChatInsertMembersViewModel

class ChanelChatInsertMembersActivity : CoreActivity() {
	private lateinit var objectViewModel: ChanelChatInsertMembersViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var membersContainer:RecyclerView?= null;
	private var membersContainerAdapter: ChanelChatInsertMembersRecyclerAdapter?= null;
	
	private var chanelChatId:String? = null;
	private var selectedUserId:ArrayList<String> = ArrayList();

	private var  selectedUserBtn: Button? = null;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ChanelChatInsertMembersViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.insert_members)
		setContentView(R.layout.activity_chanel_chat_insert_members)
		
		membersContainer =  findViewById<RecyclerView>(R.id.chanelChatInsertMembersActivityRecyclerView);
		selectedUserBtn =  findViewById<Button>(R.id.chanelChatInsertMembersActivityInsertBtn);

		membersContainerAdapter = ChanelChatInsertMembersRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		membersContainerAdapter!!.setCallbackOfViewUser(::openMemberProfile);
		membersContainerAdapter!!.setCallbackOfSelectUser(::selectUser);
		membersContainer!!.setHasFixedSize(true)
		membersContainer!!.layoutManager = LinearLayoutManager(this)
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
	}
	
	private fun loadData() {
		chanelChatId = intent.getStringExtra("chanelChatId").toString();
		println(chanelChatId)
		objectViewModel.loadData(chanelChatId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.chanelChatInsertMembersActivity)
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
		objectViewModel.insertMembersView.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setMembersContainer(it)
				}
			}
		})
	}
	
	private fun setMembersContainer(members: ArrayList<ChanelChatModels.InsertMemberAdapterItem>?){
		if(members!=null){
			membersContainerAdapter!!.setInitList(members)
			membersContainer!!.adapter = membersContainerAdapter;
		}
		updateSelectedGroup()
	}
	
	private fun openMemberProfile(idUser:String){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idUser)
		startActivity(intent)
	}
	private fun selectUser(idUser:String){
		if(selectedUserId.contains(idUser)){
			selectedUserId.remove(idUser)
		}else{
			selectedUserId.add(idUser)
		}
		updateSelectedGroup()
	}
	private fun updateSelectedGroup(){
		if(selectedUserId.size==0){
			selectedUserBtn?.visibility=View.GONE
		}else{
			selectedUserBtn?.visibility=View.VISIBLE
			selectedUserBtn?.text = "Thêm (" + selectedUserId.size +") Thành viên đã chọn."
		}
	}
	public fun insertMembersClick(view:View){
		objectViewModel.insert(chanelChatId,selectedUserId,::insertOkCallback)
	}
	private fun insertOkCallback(membersId:ArrayList<String>?){
		runOnUiThread {
			membersContainerAdapter?.updateInsertedMembers(membersId)
			selectedUserId = ArrayList();
			updateSelectedGroup()
		}
	}
}