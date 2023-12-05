package com.example.tcapp.view.notification

import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.core.NotificationSystem
import com.example.tcapp.model.notification.NotificationModels
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.service.ChanelChatDetailsService
import com.example.tcapp.service.ConnectionService
import com.example.tcapp.service.MyNotificationsService
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.notification.MyNotificationViewModel

class MyNotificationsActivity : CoreActivity() {
	private lateinit var objectViewModel: MyNotificationViewModel;

	private var mService: MyNotificationsService?=null
	private var isBoundMService:Boolean=false
	private fun setMService(service: MyNotificationsService){
		this.mService = service
	}
	private fun setIsBoundMService(isBound:Boolean){
		this.isBoundMService=isBound
	}
	private fun createdServiceCallback(){
		if(isBoundMService){
			mService?.setNotificationNewCallback(::hasNewNotificationServiceCallBack)
		}
	}
	private var mConnectionService: ServiceConnection = ConnectionService.getMyNotificationsServiceConnection(::setIsBoundMService,::setMService,::createdServiceCallback)

	private var backgroundColor:Int =0;
	private var loadingLayout:View? = null;
	private var myNotificationsActivityLoadingContainer:ViewGroup? = null;
	
	private var myNotificationsContainer:RecyclerView?= null;
	private var myNotificationsContainerAdapter:MyNotificationsRecyclerAdapter?= null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = MyNotificationViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.my_notifications)
		setContentView(R.layout.activity_my_notifications)

		val intent = Intent(this, MyNotificationsService::class.java)
		bindService(intent,mConnectionService , BIND_AUTO_CREATE)
		
		myNotificationsContainer =  findViewById<RecyclerView>(R.id.myNotificationsActivityContainer);
		myNotificationsContainerAdapter = MyNotificationsRecyclerAdapter(applicationContext,null)
		myNotificationsContainerAdapter!!.setCallbackOfUserRead(::userReadCallback)
		myNotificationsContainerAdapter!!.setCallbackOfOpenDirectActivity(::openDirectActivity)
		myNotificationsContainerAdapter!!.setInit()

		myNotificationsContainer !!.setHasFixedSize(true)
		myNotificationsContainer !!.layoutManager = LinearLayoutManager(this)
		myNotificationsContainer!!.adapter =
			myNotificationsContainerAdapter

		initViews()
		setRender()
		loadData()
		setAutoLoadWhenEndOfList();
	}

	private fun setAutoLoadWhenEndOfList(){
		findViewById<NestedScrollView>(R.id.myNotificationsActivityScrollViewContainer)
			.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v , scrollX , scrollY , oldScrollX , oldScrollY ->
				// on scroll change we are checking when users scroll as bottom.
				//300 px
				if (scrollY + 300 > v.getChildAt(0)
						.measuredHeight - v.measuredHeight
				) {
					loadMoreNotifications()
				}
			})
	}
	private fun loadData() {
		loadMoreNotifications();
	}
	private fun loadMoreNotifications(){
		if(!myNotificationsContainerAdapter!!.isFinish)
			objectViewModel.loadNotifications(myNotificationsContainerAdapter!!.firstTime)
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myNotificationsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		
		myNotificationsActivityLoadingContainer = findViewById<ViewGroup>(R.id.myNotificationsActivityLoadingContainer)
		val loadingMoreView = Genaral.getLoadingView(this,myNotificationsActivityLoadingContainer!!,backgroundColor)
		myNotificationsActivityLoadingContainer!!.addView(loadingMoreView)
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
		//set loading more
		objectViewModel.isLoadingMore.observe(this, Observer {
			runOnUiThread {
				if(it){
					myNotificationsActivityLoadingContainer?.visibility = View.VISIBLE
				}else{
					myNotificationsActivityLoadingContainer?.visibility = View.GONE
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
		//set notifications
		objectViewModel.myNotifications.observe(this, Observer {
			runOnUiThread {
				if(it!=null)insertIntoMyNotificationsContainer(it)
			}
		})
	}
	
	private fun insertIntoMyNotificationsContainer(myNotifications: NotificationModels.Notifications){
		myNotificationsContainer !!.layoutManager = LinearLayoutManager(this)

		myNotificationsContainerAdapter!!.let{
			it.isFinish= myNotifications.isFinish;
			it.insertMany(myNotifications.notifications);
		}
	}

	private fun userReadCallback(notificationId:String?){
		if(notificationId!=null){
			objectViewModel.updateNotification(notificationId)
		}
	}
	private fun openDirectActivity(intent:Intent?){
		if(intent!=null){
			startActivity(intent)
		}
	}

	private fun hasNewNotificationServiceCallBack(newNotificationId:String?){
		if(newNotificationId!=null){
			objectViewModel!!.loadNotification(newNotificationId,::loadNewNotificationCallBack);
		}
	}
	private fun loadNewNotificationCallBack(newNotification:NotificationModels.Notification?){
		if(newNotification!=null){
			myNotificationsContainerAdapter!!.changeOrAdd(newNotification);
		}
	}

}

//private fun ScrollView.setOnScrollChangeListener(onScrollChangeListener : NestedScrollView.OnScrollChangeListener) {

//}
