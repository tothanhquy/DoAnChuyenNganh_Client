package com.example.tcapp.view.friend

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.view.adapter_view.FriendRequestsListRecyclerAdapter
import com.example.tcapp.view.adapter_view.MyFriendsListRecyclerAdapter
import com.example.tcapp.view.adapter_view.MyTeamsRecyclerAdapter
import com.example.tcapp.view.adapter_view.RequestsListRecyclerAdapter
import com.example.tcapp.view.adapter_view.testAdapter
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.friend.MyFriendAndRequestsListViewModel
import com.example.tcapp.viewmodel.request.RequestsListViewModel
import com.example.tcapp.viewmodel.team_profile.MyTeamsViewModel

class MyFriendAndRequestsListActivity : CoreActivity() {
	private lateinit var objectViewModel: MyFriendAndRequestsListViewModel;
	
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var  requestsSendLoadMore:Button? = null;
	private var  requestsReceiveLoadMore:Button? = null;
	
	private var  navReceiveView:TextView? = null;
	private var  navSendView:TextView? = null;
	private var  navMyFriendsView:TextView? = null;
	
	private var friendRequestsListSendContainer:RecyclerView?= null;
	private var  friendRequestsListSendContainerAdapter:FriendRequestsListRecyclerAdapter?= null;
	private var friendRequestsListReceiveContainer:RecyclerView?= null;
	private var  friendRequestsListReceiveContainerAdapter:FriendRequestsListRecyclerAdapter?= null;
	private var myFriendsContainer:RecyclerView?= null;
	private var  myFriendsContainerAdapter:MyFriendsListRecyclerAdapter?= null;

	private lateinit var layoutStatus: FriendModels.ListLayoutStatus;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = MyFriendAndRequestsListViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.friends)
		setContentView(R.layout.activity_my_friend_and_requests_list)
		//send requests
		friendRequestsListSendContainer =  findViewById<RecyclerView>(R.id.myFriendAndRequestsListActivitySendContainerRecyclerView);
		friendRequestsListSendContainerAdapter = FriendRequestsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		friendRequestsListSendContainerAdapter !!.setMethod(FriendModels.RequestMethod.SEND);
		friendRequestsListSendContainerAdapter !!.setCallback(::openRequest);
		friendRequestsListSendContainer !!.setHasFixedSize(true)
		friendRequestsListSendContainer !!.layoutManager = LinearLayoutManager(this)
		friendRequestsListSendContainer!!.adapter =
			friendRequestsListSendContainerAdapter
		//receive requests
		friendRequestsListReceiveContainer =  findViewById<RecyclerView>(R.id.myFriendAndRequestsListActivityReceiveContainerRecyclerView);
		friendRequestsListReceiveContainerAdapter = FriendRequestsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		friendRequestsListReceiveContainerAdapter !!.setMethod(FriendModels.RequestMethod.RECEIVE);
		friendRequestsListReceiveContainerAdapter !!.setCallback(::openRequest);
		friendRequestsListReceiveContainer !!.setHasFixedSize(true)
		friendRequestsListReceiveContainer !!.layoutManager =
			LinearLayoutManager(this)
		friendRequestsListReceiveContainer!!.adapter =
			friendRequestsListReceiveContainerAdapter
		//my friends
		myFriendsContainer =  findViewById<RecyclerView>(R.id.myFriendAndRequestsListActivityMyFriendsContainerRecyclerView);
		myFriendsContainerAdapter = MyFriendsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		myFriendsContainerAdapter !!.setCallbackOfViewUser(::openUser);
		myFriendsContainer !!.setHasFixedSize(true)
		myFriendsContainer !!.layoutManager =
			LinearLayoutManager(this)
		myFriendsContainer!!.adapter =
			myFriendsContainerAdapter

		requestsSendLoadMore = findViewById<Button>(R.id.myFriendAndRequestsListActivitySendContainerBtnLoadMore)
		requestsReceiveLoadMore = findViewById<Button>(R.id.myFriendAndRequestsListActivityReceiveContainerBtnLoadMore)
		
		navSendView = findViewById<TextView>(R.id.myFriendAndRequestsListActivityNavSend);
		navReceiveView = findViewById<TextView>(R.id.myFriendAndRequestsListActivityNavReceive);
		navMyFriendsView = findViewById<TextView>(R.id.myFriendAndRequestsListActivityNavMyFriends);

		initViews()
		setRender()
		initDataViews()
		
		//load my friends default
		if(!myFriendsContainerAdapter!!.wasLoaded)
			loadMyFriends()
		swapLayout(FriendModels.ListLayoutStatus.MY_FRIENDS)
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.list_menu_refresh , menu)
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.refresh -> {
				refreshList()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun initDataViews() {
//		friendRequestsListSendContainerAdapter !!.setViewer(viewer);
//
//		friendRequestsListReceiveContainerAdapter !!.setViewer(viewer);
		
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		
		//init menu
		
	}
	private fun setRender(){
		//set alert error
		objectViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,null)
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
		//set requests
		objectViewModel.resRequestsListStatus.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setFriendRequestsContainer(it)
			}
		})
		//set my friends
		objectViewModel.resMyFriendsStatus.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setMyFriendsContainer(objectViewModel.myFriends)
			}
		})
	}
	
	private fun setFriendRequestsContainer(requestListStatus: FriendModels.RequestsListStatus){
//		println(objectViewModel.requests!!.size)
		if(requestListStatus.method==FriendModels.RequestMethod.SEND){
			friendRequestsListSendContainerAdapter!!.addList(objectViewModel.requests!!)
			friendRequestsListSendContainerAdapter!!.timePrevious = requestListStatus.timePrevious;
			friendRequestsListSendContainerAdapter!!.wasLoaded = true;
			requestsSendLoadMore!!.visibility = if(requestListStatus.isFinish)View.GONE else View.VISIBLE;
		}else{
			friendRequestsListReceiveContainerAdapter!!.addList(objectViewModel.requests!!)
			friendRequestsListReceiveContainerAdapter!!.timePrevious = requestListStatus.timePrevious;
			friendRequestsListReceiveContainerAdapter!!.wasLoaded = true;
			requestsReceiveLoadMore!!.visibility = if(requestListStatus.isFinish)View.GONE else View.VISIBLE;
		}
	}

	private fun setMyFriendsContainer(myFriends: ArrayList<FriendModels.MyFriendsListItem>?){
		if(myFriends!=null)
			myFriendsContainerAdapter!!.setInitList(myFriends)
		myFriendsContainerAdapter!!.wasLoaded = true;
	}
	
	private fun loadRequests(method:FriendModels.ListLayoutStatus){
		if(method==FriendModels.ListLayoutStatus.REQUESTS_SEND){
			objectViewModel.loadRequests("send",friendRequestsListSendContainerAdapter!!.timePrevious);
		}else{
			objectViewModel.loadRequests("receive",friendRequestsListReceiveContainerAdapter!!.timePrevious);
		}
	}

	private fun loadMyFriends(){
		objectViewModel.loadMyFriends();
	}

	private fun openRequest(requestId:String){
		val intent = Intent(applicationContext ,ViewFriendRequestActivity::class.java)
		val method = if(layoutStatus==FriendModels.ListLayoutStatus.REQUESTS_SEND)"send" else "receive";
		intent.putExtra("method", method);
		intent.putExtra("requestId", requestId);
		startActivity(intent)
	}

	private fun openUser(userId:String){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", userId)
		startActivity(intent)
	}
	
	fun navSendMethod(view:View){
		if(!friendRequestsListSendContainerAdapter!!.wasLoaded)
			loadRequests(FriendModels.ListLayoutStatus.REQUESTS_SEND)
		swapLayout(FriendModels.ListLayoutStatus.REQUESTS_SEND)
	}
	fun navReceiveMethod(view:View){
		if(!friendRequestsListReceiveContainerAdapter!!.wasLoaded)
			loadRequests(FriendModels.ListLayoutStatus.REQUESTS_RECEIVE)
		swapLayout(FriendModels.ListLayoutStatus.REQUESTS_RECEIVE)
	}
	fun navMyFriends(view:View){
		if(!myFriendsContainerAdapter!!.wasLoaded)
			loadMyFriends()
		swapLayout(FriendModels.ListLayoutStatus.MY_FRIENDS)
	}
	fun loadMoreRequests(view:View){
		loadRequests(layoutStatus)
	}
	private fun swapLayout(layout:FriendModels.ListLayoutStatus){
		layoutStatus = layout;
		when (layout) {
			FriendModels.ListLayoutStatus.REQUESTS_SEND -> {
				navSendView!!.paintFlags = navSendView!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
				navReceiveView!!.paintFlags =  navReceiveView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				navMyFriendsView!!.paintFlags =  navMyFriendsView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivitySendContainer).visibility = View.VISIBLE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityReceiveContainer).visibility = View.GONE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityMyFriendsContainer).visibility = View.GONE;
			}
			FriendModels.ListLayoutStatus.REQUESTS_RECEIVE -> {
				navSendView!!.paintFlags = navSendView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				navReceiveView!!.paintFlags =  navReceiveView!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
				navMyFriendsView!!.paintFlags =  navMyFriendsView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivitySendContainer).visibility = View.GONE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityReceiveContainer).visibility = View.VISIBLE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityMyFriendsContainer).visibility = View.GONE;
			}
			else -> {//FriendModels.ListLayoutStatus.MY_FRIENDS
				navSendView!!.paintFlags = navSendView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				navReceiveView!!.paintFlags =  navReceiveView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
				navMyFriendsView!!.paintFlags =  navMyFriendsView!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivitySendContainer).visibility = View.GONE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityReceiveContainer).visibility = View.GONE;
				findViewById<ViewGroup>(R.id.myFriendAndRequestsListActivityMyFriendsContainer).visibility = View.VISIBLE;
			}
		}
	}
	private fun refreshList(){
		when (layoutStatus) {
			FriendModels.ListLayoutStatus.REQUESTS_SEND -> {
				friendRequestsListSendContainerAdapter!!.setInitList(arrayListOf())
				friendRequestsListSendContainerAdapter!!.timePrevious = 0;
				loadRequests(layoutStatus)
			}
			FriendModels.ListLayoutStatus.REQUESTS_RECEIVE -> {
				friendRequestsListReceiveContainerAdapter!!.setInitList(arrayListOf())
				friendRequestsListReceiveContainerAdapter!!.timePrevious = 0;
				loadRequests(layoutStatus)
			}
			else -> {
				myFriendsContainerAdapter!!.setInitList(arrayListOf())
				loadMyFriends()
			}
		}

	}
}