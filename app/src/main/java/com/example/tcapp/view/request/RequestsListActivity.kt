package com.example.tcapp.view.request

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
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.view.adapter_view.MyTeamsRecyclerAdapter
import com.example.tcapp.view.adapter_view.RequestsListRecyclerAdapter
import com.example.tcapp.view.adapter_view.testAdapter
import com.example.tcapp.viewmodel.request.RequestsListViewModel
import com.example.tcapp.viewmodel.team_profile.MyTeamsViewModel

class RequestsListActivity : CoreActivity() {
	private lateinit var objectViewModel: RequestsListViewModel;
	
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var  requestsSendLoadMore:Button? = null;
	private var  requestsReceiveLoadMore:Button? = null;
	
	
	private var  navSendView:TextView? = null;
	private var  navReceiveView:TextView? = null;
	
	private var requestsListSendContainer:RecyclerView?= null;
	private var  requestsListSendContainerAdapter:RequestsListRecyclerAdapter?= null;
	private var requestsListReceiveContainer:RecyclerView?= null;
	private var  requestsListReceiveContainerAdapter:RequestsListRecyclerAdapter?= null;
	
	private var filterIsImportant:Boolean = true;
	private var filterNotImportant:Boolean = true;
	private var filterWasReaded:Boolean = true;
	private var filterNotRead:Boolean = true;
	
	private lateinit var methodStatus:RequestModels.RequestMethod;
	
	private lateinit var viewer: RequestModels.RequestViewer;
	private var teamId: String?=null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = RequestsListViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.requests)
		setContentView(R.layout.activity_requests_list)
		
//		val customAdapter = testAdapter(listOf("1","2","3"))
//		findViewById<RecyclerView>(R.id.requestsListActivitySendContainerRecyclerView).setHasFixedSize(true)
//		findViewById<RecyclerView>(R.id.requestsListActivitySendContainerRecyclerView).layoutManager = LinearLayoutManager(this)
//		findViewById<RecyclerView>(R.id.requestsListActivitySendContainerRecyclerView).adapter = customAdapter
//		return;
		
		requestsListSendContainer =  findViewById<RecyclerView>(R.id.requestsListActivitySendContainerRecyclerView);
		requestsListSendContainerAdapter = RequestsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		requestsListSendContainerAdapter !!.setMethod(RequestModels.RequestMethod.SEND);
		requestsListSendContainerAdapter !!.setCallback(::openRequest);
		requestsListSendContainer !!.setHasFixedSize(true)
		requestsListSendContainer !!.layoutManager = LinearLayoutManager(this)
		requestsListSendContainer!!.adapter =
			requestsListSendContainerAdapter
		
		requestsListReceiveContainer =  findViewById<RecyclerView>(R.id.requestsListActivityReceiveContainerRecyclerView);
		requestsListReceiveContainerAdapter = RequestsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		requestsListReceiveContainerAdapter !!.setMethod(RequestModels.RequestMethod.RECEIVE);
		requestsListReceiveContainerAdapter !!.setCallback(::openRequest);
		requestsListReceiveContainer !!.setHasFixedSize(true)
		requestsListReceiveContainer !!.layoutManager =
			LinearLayoutManager(this)
		requestsListReceiveContainer!!.adapter =
			requestsListReceiveContainerAdapter
		
		requestsSendLoadMore = findViewById<Button>(R.id.requestsListActivitySendContainerBtnLoadMore)
		requestsReceiveLoadMore = findViewById<Button>(R.id.requestsListActivityReceiveContainerBtnLoadMore)
		
		navSendView = findViewById<TextView>(R.id.requestsListActivityNavSend);
		navReceiveView = findViewById<TextView>(R.id.requestsListActivityNavReceive);
		
		initViews()
		setRender()
		initDataViews()
		
		//load receive default
		if(!requestsListReceiveContainerAdapter!!.wasLoaded)
			loadRequests(RequestModels.RequestMethod.RECEIVE)
		swapRequestsListLayout(RequestModels.RequestMethod.RECEIVE)
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.list_menu_refresh_filter , menu)
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.filter -> {
				openFilter()
			}
			R.id.refresh -> {
				refreshRequest()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun initDataViews() {
		teamId = intent.getStringExtra("teamId").toString();
		val viewerIntent =  intent.getStringExtra("viewer").toString();
		if(viewerIntent=="leader"){
			viewer = RequestModels.RequestViewer.LEADER;
		}else{
			viewer = RequestModels.RequestViewer.USER;
		}
	
		requestsListSendContainerAdapter !!.setViewer(viewer);
		
		requestsListReceiveContainerAdapter !!.setViewer(viewer);
		
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.requestsListActivity)
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
		objectViewModel.resListStatus.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setRequestsContainer(it)
			}
		})
	}
	
	private fun setRequestsContainer(requestListStatus: RequestModels.RequestsListStatus){
		if(requestListStatus.viewer!=this.viewer)return;
//		println(objectViewModel.requests!!.size)
		if(requestListStatus.method==RequestModels.RequestMethod.SEND){
			requestsListSendContainerAdapter!!.addList(objectViewModel.requests!!)
			requestsListSendContainerAdapter!!.timePrevious = requestListStatus.timePrevious;
			requestsListSendContainerAdapter!!.wasLoaded = true;
			requestsSendLoadMore!!.visibility = if(requestListStatus.isFinish)View.GONE else View.VISIBLE;
		}else{
			requestsListReceiveContainerAdapter!!.addList(objectViewModel.requests!!)
			requestsListReceiveContainerAdapter!!.timePrevious = requestListStatus.timePrevious;
			requestsListReceiveContainerAdapter!!.wasLoaded = true;
			requestsReceiveLoadMore!!.visibility = if(requestListStatus.isFinish)View.GONE else View.VISIBLE;
		}
	}
	
	private fun loadRequests(method:RequestModels.RequestMethod){
		val viewerString = if(viewer==RequestModels.RequestViewer.LEADER)"leader" else "user";
		if(method==RequestModels.RequestMethod.SEND){
			objectViewModel.loadRequests(viewerString,"send",requestsListSendContainerAdapter!!.timePrevious,teamId);
		}else{
			objectViewModel.loadRequests(viewerString,"receive",requestsListReceiveContainerAdapter!!.timePrevious,teamId);
		}
	}
	
	private fun openRequest(requestId:String){
		val intent = Intent(applicationContext ,ViewRequestActivity::class.java)
		intent.putExtra("teamId", teamId);
		intent.putExtra("requestId", requestId);
		intent.putExtra("viewer", if(viewer==RequestModels.RequestViewer.LEADER)"leader" else "user");
		startActivity(intent)
	}
	
	fun navSendMethod(view:View){
		if(!requestsListSendContainerAdapter!!.wasLoaded)
			loadRequests(RequestModels.RequestMethod.SEND)
		swapRequestsListLayout(RequestModels.RequestMethod.SEND)
	}
	fun navReceiveMethod(view:View){
		if(!requestsListReceiveContainerAdapter!!.wasLoaded)
			loadRequests(RequestModels.RequestMethod.RECEIVE)
		swapRequestsListLayout(RequestModels.RequestMethod.RECEIVE)
	}
	fun loadMoreRequests(view:View){
		loadRequests(methodStatus)
	}
	private fun swapRequestsListLayout(method:RequestModels.RequestMethod){
		methodStatus = method;
		if(method==RequestModels.RequestMethod.SEND){
			navSendView!!.paintFlags = navSendView!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
			navReceiveView!!.paintFlags =  navSendView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
			requestsListSendContainerAdapter!!.setFilter(filterIsImportant,filterNotImportant,filterWasReaded,filterNotRead);
			findViewById<ViewGroup>(R.id.requestsListActivitySendContainer).visibility = View.VISIBLE;
			findViewById<ViewGroup>(R.id.requestsListActivityReceiveContainer).visibility = View.GONE;
		}else{
			navSendView!!.paintFlags = navSendView!!.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv();
			navReceiveView!!.paintFlags =  navSendView!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG;
			requestsListReceiveContainerAdapter!!.setFilter(filterIsImportant,filterNotImportant,filterWasReaded,filterNotRead);
			findViewById<ViewGroup>(R.id.requestsListActivityReceiveContainer).visibility = View.VISIBLE;
			findViewById<ViewGroup>(R.id.requestsListActivitySendContainer).visibility = View.GONE;
		}
	}
	fun applyFilter(view:View){
		filterIsImportant =  findViewById<Switch>(R.id.requestsListActivityFilterContainerSwitchIsImportant).isChecked;
		filterNotImportant =  findViewById<Switch>(R.id.requestsListActivityFilterContainerSwitchIsNotImportant).isChecked;
		filterWasReaded = findViewById<Switch>(R.id.requestsListActivityFilterContainerSwitchWasReaded).isChecked;
		filterNotRead = findViewById<Switch>(R.id.requestsListActivityFilterContainerSwitchNotRead).isChecked;
		swapRequestsListLayout(methodStatus)
		closeFilter(View(this))
	}
	fun closeFilter(view:View){
		findViewById<ViewGroup>(R.id.requestsListActivityFilterContainer).visibility = View.GONE;
	}
	private fun openFilter(){
		findViewById<ViewGroup>(R.id.requestsListActivityFilterContainer).visibility = View.VISIBLE;
	}
	private fun refreshRequest(){
		if(methodStatus==RequestModels.RequestMethod.SEND){
			requestsListSendContainerAdapter!!.setInitList(arrayListOf())
			requestsListSendContainerAdapter!!.timePrevious = 0;
		}else{
			requestsListReceiveContainerAdapter!!.setInitList(arrayListOf())
			requestsListReceiveContainerAdapter!!.timePrevious = 0;
		}
		loadRequests(methodStatus)
	}
}