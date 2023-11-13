package com.example.tcapp.view.chanel_chat

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
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
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.service.ConnectionService
import com.example.tcapp.service.MyChanelChatsService
import com.example.tcapp.service.SocketService
import com.example.tcapp.view.adapter_view.MyChanelChatsRecyclerAdapter
import com.example.tcapp.viewmodel.chanel_chat.MyChanelChatsViewModel

class MyChanelChatsActivity : CoreActivity() {
	private lateinit var objectViewModel: MyChanelChatsViewModel;

	private var mService: MyChanelChatsService?=null
	private var isBoundMService:Boolean=false
	private fun setMService(service: MyChanelChatsService){
		this.mService = service
	}
	private fun setIsBoundMService(isBound:Boolean){
		this.isBoundMService=isBound
	}
	private var mConnectionService: ServiceConnection = ConnectionService.getMyChanelChatsServiceConnection(::setIsBoundMService,::setMService)


	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var myChanelChatsContainer:RecyclerView?= null;
	private var myChanelChatsContainerAdapter:MyChanelChatsRecyclerAdapter?= null;
	
	private var newChanelChatNameBefore:String? = null;
	
	private var reShowCreateNewGroupChat:Int =0;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = MyChanelChatsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.my_chanel_chats)
		setContentView(R.layout.activity_my_chanel_chats)
		
		myChanelChatsContainer =  findViewById<RecyclerView>(R.id.myChanelChatsActivityRecyclerView);

		//start service
		val intent = Intent(this, MyChanelChatsService::class.java)
		bindService(intent,mConnectionService , BIND_AUTO_CREATE)

		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}

	override fun onDestroy() {
		unbindService(mConnectionService);
		setIsBoundMService(false)
		super.onDestroy()
	}

	override fun onStart() {
		super.onStart()
		if(isBoundMService){
			mService?.setChanelChatYouHasNewChanelCallback(::chanelChatYouHasNewChanelSocketCallback)
			mService?.setChanelChatNotifiLastMessageCallback(::chanelChatNotifiLastMessageSocketCallback)
		}
	}

	private fun loadData() {
		objectViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myChanelChatsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	@SuppressLint("SuspiciousIndentation")
	private fun setRender(){
		//set alert error
		objectViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,fun(dialogInterface: DialogInterface , i:Int){
						if(reShowCreateNewGroupChat!=objectViewModel.reShowCreateNewGroupChat){
							reShowCreateNewGroupChat=objectViewModel.reShowCreateNewGroupChat;
							showDialogCreateNewGroupChat(newChanelChatNameBefore?:"")
						}
					})
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
		//set avatar
		objectViewModel.newChanelChat.observe(this, Observer {
			if(it!=null)
			runOnUiThread {
				pushNewChanelChat(it)
			}
		})
		//set user data
		objectViewModel.myChanelChats.observe(this, Observer {
			runOnUiThread {
				setChanelChatsContainer(it)
			}
		})
		//set
//		objectViewModel.reShowCreateNewGroupChat.observe(this, Observer {
//			if(it!=0)
//				showDialogCreateNewTeam(newChanelChatNameBefore?:"")
//		})
	}
	private fun pushNewChanelChat(newChanelChat: ChanelChatModels.ChanelChat){
		try{
			myChanelChatsContainerAdapter!!.changeOrAdd(newChanelChat)
			if(myChanelChatsContainerAdapter!!.itemCount<=1){
				loadData()
			}
		}catch(e:Exception){}
	}
	private fun setChanelChatsContainer(chanelChats: ArrayList<ChanelChatModels.ChanelChat>){
		myChanelChatsContainerAdapter = MyChanelChatsRecyclerAdapter(applicationContext,null)
		myChanelChatsContainerAdapter!!.setCallback(::openChanelChat);
		myChanelChatsContainer!!.setHasFixedSize(true)
		myChanelChatsContainer!!.layoutManager = LinearLayoutManager(this)
		myChanelChatsContainer!!.adapter = myChanelChatsContainerAdapter;
		myChanelChatsContainerAdapter!!.setInitList(chanelChats)
		
	}
	
	private fun openChanelChat(id:String){
		val intent = Intent(applicationContext ,ChanelChatDetailsActivity::class.java)
		intent.putExtra("chanelChatId", id)
		startActivity(intent)
	}
	fun createNewChanelChatClick(view:View){
		showDialogCreateNewGroupChat("new group chat")
	}
	private fun showDialogCreateNewGroupChat(inputDefault:String){
		val dialog = createGetStringDialog(this@MyChanelChatsActivity,"New group chat name",inputDefault,::createNewGroupChat)
		dialog.show();
	}
	private fun createNewGroupChat(name:String?):Boolean{
		newChanelChatNameBefore = name;
		objectViewModel.createNewGroupChat(name);
		return true;
	}

	private fun chanelChatYouHasNewChanelSocketCallback(idUser:String?){
		loadData()
	}
	private fun chanelChatNotifiLastMessageSocketCallback(chanelChat:ChanelChatModels.LastNewMessageSocket?){
		runOnUiThread {
			if (chanelChat != null) myChanelChatsContainerAdapter?.updateLastMessageBySocket(
				chanelChat
			)
		}
	}
}