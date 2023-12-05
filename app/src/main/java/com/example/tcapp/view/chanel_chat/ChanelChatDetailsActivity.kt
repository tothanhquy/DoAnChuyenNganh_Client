package com.example.tcapp.view.chanel_chat

import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.chanel_chat.MessageModels
import com.example.tcapp.service.ChanelChatDetailsService
import com.example.tcapp.service.ConnectionService
import com.example.tcapp.service.MyChanelChatsService
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.chanel_chat.ChanelChatDetailsViewModel
import android.os.Handler

const val CHOOSE_AVATAR_REQUEST_CODE = 110
class ChanelChatDetailsActivity : CoreActivity() {
	private lateinit var objectViewModel: ChanelChatDetailsViewModel;

	private var mService: ChanelChatDetailsService?=null
	private var isBoundMService:Boolean=false
	private fun setMService(service: ChanelChatDetailsService){
		this.mService = service
	}
	private fun setIsBoundMService(isBound:Boolean){
		this.isBoundMService=isBound
	}
	private fun createdServiceCallback(){
		if(isBoundMService){
			mService?.setChanelChatNewMessagesCallback(::chanelChatNewMessagesSocketCallback)
			mService?.setChanelChatUserSeenCallback(::chanelChatUserSeenSocketCallback)
		}
	}
	private var mConnectionService: ServiceConnection = ConnectionService.getChanelChatDetailsServiceConnection(::setIsBoundMService,::setMService,::createdServiceCallback)


	private var chanelChatDetails: ChanelChatModels.ChanelChatDetails?=null;
	private var chanelChatId:String?=null;
	private var chanelChatName:String?=null;
	private var chanelChatAvatar:String?=null;

	private var newGroupChatNameBefore:String="";

	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var messagesRecyclerView:RecyclerView?= null;
	private var newGroupOwnerSelectContainer:RecyclerView?= null;
	private var messagesAdapter:ChanelChatIMessagesRecyclerAdapter?= null;

	private var messageOptionContainer:LinearLayout?= null;

	private var messageReplyIdOpenOption:String?=null;
	private var messageReplyContentOpenOption:String?=null;
	private var messageReplyIdReal:String?=null;
	private var messageReplyContainer:LinearLayout?=null;
	private var chatBtn:Button?=null;
	private var isChatBtnEnable:Boolean=false;
	private var chatBoxInput:EditText?=null;

	private var lastMessageIdUserSeen:String?=null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ChanelChatDetailsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.chanel_chat_details)
		setContentView(R.layout.activity_chanel_chat_details)
		
		messageOptionContainer =  findViewById<LinearLayout>(R.id.chanelChatDetailsActivityMessageOptions);
		messageReplyContainer =  findViewById<LinearLayout>(R.id.chanelChatDetailsActivityMessageChatReplyContainer);
		messagesRecyclerView =  findViewById<RecyclerView>(R.id.chanelChatDetailsActivityMessagesRecyclerView);
		newGroupOwnerSelectContainer =  findViewById<RecyclerView>(R.id.chanelChatDetailsActivityChooseNewGroupOwnerRecyclerView);
		chatBtn =  findViewById<Button>(R.id.chanelChatDetailsActivityMessageChatBoxChatBtn);
		chatBoxInput =  findViewById<EditText>(R.id.chanelChatDetailsActivityMessageChatBoxInput);

		messagesAdapter = ChanelChatIMessagesRecyclerAdapter(applicationContext, null)
		messagesAdapter!!.setCallbackLoadHistoryMessages(::loadHistoryMessages)
		messagesAdapter!!.setCallbackLoadMessagesBetweenTime(::loadMessagesBetweenTime)
		messagesAdapter!!.setCallbackOfViewUser(::openMemberProfile)
		messagesAdapter!!.setCallbackLongTouchMessage(::longTouchMessage)
		messagesAdapter!!.setCallbackScrollRecyclerView(::messagesRecyclerViewScrollToPosition)
		messagesRecyclerView !!.setHasFixedSize(true)
		messagesRecyclerView !!.layoutManager =
			LinearLayoutManager(this)
		messagesRecyclerView!!.adapter =messagesAdapter

		initViews()
		initEvents()
		setRender()
		loadData()
	}

	override fun onDestroy() {
		unbindService(mConnectionService);
		setIsBoundMService(false)
		super.onDestroy()
	}
	override fun onStart() {
		super.onStart()
	}
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.details_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}

	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.details -> {
				openOptions()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == RESULT_OK) {
			val selectedUri : Uri = data!!.data
				?: return //The uri with the location of the file
//
			val selectedPath = Genaral.URIPathHelper().getPath(applicationContext, selectedUri!!)
			objectViewModel.changeAvatar(selectedPath, chanelChatId)
		}
	}

	private fun loadData() {
		chanelChatId = intent.getStringExtra("chanelChatId").toString()
		//start service
		val intent = Intent(this, ChanelChatDetailsService::class.java)
		intent.putExtra("chanelChatId",chanelChatId)
		bindService(intent,mConnectionService , BIND_AUTO_CREATE)

		objectViewModel.loadData(chanelChatId)
//		loadDefaultMessages()
//		messagesAdapter!!.setInitList(MessageModels.Messages())
	}
	private fun initEvents(){
		chatBoxInput!!.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(s: Editable) {}
			override fun beforeTextChanged(
				s: CharSequence, start: Int,
				count: Int, after: Int
			) {
			}

			override fun onTextChanged(
				s: CharSequence, start: Int,
				before: Int, count: Int
			) {
				if (s.isNotEmpty()){
					if(!isChatBtnEnable){
						chatBtn!!.setBackgroundResource(R.drawable.action_btn_bg)
						isChatBtnEnable = true;
					}
				}else{
					if(isChatBtnEnable){
						chatBtn!!.setBackgroundResource(R.drawable.close_btn_bg)
						isChatBtnEnable = false;
					}
				}
				//user seen
				println("last message:$lastMessageIdUserSeen ${messagesAdapter!!.lastMessageId}")
				if(lastMessageIdUserSeen!=messagesAdapter!!.lastMessageId){
					objectViewModel.userSeen(chanelChatId,::userSeenOkCallback)
					lastMessageIdUserSeen=messagesAdapter!!.lastMessageId
				}
			}
		})
	}
	private fun userSeenOkCallback(){}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.chanelChatDetailsActivity)
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
		//set avatar
		objectViewModel.chanelChatAvatar.observe(this, Observer {
			runOnUiThread {
				if(it!=null) {
					setChanelChatAvatar(it)
				}
			}
		})
		//set chanel chat data
		objectViewModel.chanelChat.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					chanelChatDetails = it;
					setView(it)
				}

			}
		})
//		objectViewModel.fullMembers.observe(this, Observer {
//			runOnUiThread {
//				if(it!=null){
//					messagesAdapter!!.setInitList(it)
//				}
//
//			}
//		})
	}
	private fun setChanelChatAvatar(avatarPath:String?){
		try{
			if(avatarPath!=null){
				chanelChatAvatar = avatarPath
				val type = objectViewModel.chanelChatType
				val avatar = findViewById<ImageView>(R.id.chanelChatDetailsActivityAvatar);
				when (type) {
					ChanelChatModels.Type.Team -> {
						Genaral.setTeamAvatarImage(this,avatarPath,avatar)
					}
					ChanelChatModels.Type.Friend -> {
						Genaral.setUserAvatarImage(this,avatarPath,avatar)
					}
					else -> {
						Genaral.setGroupChatAvatarImage(this,avatarPath,avatar)
					}
				}
			}
		}catch(e:Exception){}
	}
	private fun setView(chanelChat: ChanelChatModels.ChanelChatDetails?){
		if(chanelChat!=null) {
			findViewById<TextView>(R.id.chanelChatDetailsActivityName).text =
				chanelChat.name;
			chanelChatName = chanelChat.name;
			newGroupChatNameBefore = chanelChatName ?: ""
			super.setDynamicTitleBar(chanelChat.name)
			setOptions(chanelChat);
			setSelectNewGroupOwnerContainer(chanelChat.members,chanelChat.accountId)
			messagesAdapter!!.setAccountId(chanelChat.accountId)
			messagesAdapter!!.setMembers(chanelChat.members!!)
			messagesAdapter!!.setUsersSeen(chanelChat.lastTimeMemberSeen!!)
			loadDefaultMessages()
		}
	}
	private fun setOptions(chanelChat: ChanelChatModels.ChanelChatDetails?){
		val editAvatar = findViewById<Button>(R.id.chanelChatDetailsActivityOptionEditAvatar);
		editAvatar.visibility = View.GONE;
		val viewMembers = findViewById<Button>(R.id.chanelChatDetailsActivityOptionViewMember);
		viewMembers.visibility = View.GONE;
		val viewUser = findViewById<Button>(R.id.chanelChatDetailsActivityOptionViewUser);
		viewUser.visibility = View.GONE;
		val viewTeam = findViewById<Button>(R.id.chanelChatDetailsActivityOptionViewTeam);
		viewTeam.visibility = View.GONE;
		val editName = findViewById<Button>(R.id.chanelChatDetailsActivityOptionEditName);
		editName.visibility = View.GONE;
		val exitGroupChat = findViewById<Button>(R.id.chanelChatDetailsActivityOptionExitGroupChat);
		exitGroupChat.visibility = View.GONE;
		
		if(chanelChat?.type==ChanelChatModels.Type.Team){
//			viewMembers.visibility = View.VISIBLE
			viewTeam.visibility = View.VISIBLE
		}else if(chanelChat?.type==ChanelChatModels.Type.Friend){
			if(chanelChat.friendId!=null&&chanelChat.friendId!=""){
				viewUser.visibility = View.VISIBLE
			}
		}else{
			//group
			viewMembers.visibility = View.VISIBLE
			exitGroupChat.visibility = View.VISIBLE
//			if(chanelChat?.isGroupOwner==true){
				editAvatar.visibility = View.VISIBLE
				editName.visibility = View.VISIBLE
//			}
		}
	}

	private fun setSelectNewGroupOwnerContainer(members: ArrayList<ChanelChatModels.Member>?, accountId:String?){
		if(members!=null){
			val memberFilter = ArrayList(members.filter { it.id!=accountId })
			var membersAdapter = ChanelChatDetailsSelectNewGroupOwnerRecyclerAdapter(applicationContext,
				memberFilter
			)
			newGroupOwnerSelectContainer!!.setHasFixedSize(true)
			newGroupOwnerSelectContainer!!.layoutManager = LinearLayoutManager(this)
			membersAdapter.setCallback(::newGroupOwnerSelectItemChoose)
			newGroupOwnerSelectContainer!!.adapter = membersAdapter;
		}
	}
	fun changeGroupAvatar(view: View){
		if(checkPermissionsReadFile()){
			var intent:Intent =  Intent()
				.setType("image/*")
				.setAction(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(Intent.createChooser(intent, "Select  avatar"), CHOOSE_AVATAR_REQUEST_CODE);
		}else{
			requestPermissionsReadFile()
		}
	}
	
	fun viewMembersNavigation(view: View){
		val intent = Intent(applicationContext , ChanelChatViewMembersListActivity::class.java)
		intent.putExtra("chanelChatId", chanelChatId)
		startActivity(intent)
	}
	fun viewUserNavigation(view: View){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", chanelChatDetails?.friendId)
		startActivity(intent)
	}
	fun viewTeamNavigation(view: View){
		val intent = Intent(applicationContext , TeamProfileActivity::class.java)
		intent.putExtra("teamId", chanelChatDetails?.teamId)
		startActivity(intent)
	}
	fun exitGroupChatNavigation(view: View){
		if((chanelChatDetails?.isGroupOwner == true) && (chanelChatDetails?.members?.size != 1)){
			openNewGroupOwnerSelect()
		}else{
			this.showAskDialog(
				"Important!",
				"Do you really want to exit this group chat.",
				fun(dialogInterface: DialogInterface , i:Int){
					objectViewModel.exitChanelChat(chanelChatId,null,::exitChanelChatSuccessCallback)
				}
			)
		}
	}
	private fun exitChanelChatSuccessCallback(){
		println("callback")
		closeOptions()
		closeNewGroupOwnerSelect()
		finish()
	}
	private fun openOptions(){
		findViewById<ViewGroup>(R.id.chanelChatDetailsActivityOptions).visibility = View.VISIBLE
	}
	fun closeOptions(view:View?=null){
		findViewById<ViewGroup>(R.id.chanelChatDetailsActivityOptions).visibility = View.GONE
	}

	fun changeGroupName(view:View){
		showDialogChangeName(newGroupChatNameBefore!!)
	}
	private fun showDialogChangeName(inputDefault:String){
		val dialog = createGetStringDialog(this@ChanelChatDetailsActivity,"New group chat name",inputDefault,::changeGroupName)
		dialog.show();
	}
	private fun changeGroupName(name:String?):Boolean{
		newGroupChatNameBefore = name?: "";
		objectViewModel.changeNewNameGroupChat(chanelChatId,name,::changeGroupNameOkCallback);
		return true;
	}
	fun changeGroupNameOkCallback(newName:String?){
		runOnUiThread {
			findViewById<TextView>(R.id.chanelChatDetailsActivityName).text =
				newName;
			chanelChatName = newName;
			super.setDynamicTitleBar(newName)
		}
	}
	
	private fun newGroupOwnerSelectItemChoose(idMember:String? , name:String?){
		this.showAskDialog(
			"Important!",
			"Do you really want to exit this group chat and trade group owner position for $name.",
			fun(dialogInterface: DialogInterface , i:Int){
				objectViewModel.exitChanelChat(chanelChatId,idMember,::exitChanelChatSuccessCallback)
			}
		)
	}
	private fun openNewGroupOwnerSelect(){
		findViewById<ViewGroup>(R.id.chanelChatDetailsActivityChooseNewGroupOwner).visibility = View.VISIBLE
	}
	fun closeNewGroupOwnerSelect(view:View?=null){
		findViewById<ViewGroup>(R.id.chanelChatDetailsActivityChooseNewGroupOwner).visibility = View.GONE
	}
	
	fun loadDefaultMessages(){
		objectViewModel!!.getMessagesHistory(chanelChatId,0,::loadDefaultMessagesOkCallback)
	}
	private fun loadDefaultMessagesOkCallback(messages:MessageModels.Messages?){
		runOnUiThread {
			messagesRecyclerView !!.layoutManager =
				LinearLayoutManager(this)
			messagesAdapter!!.setInitList(messages)
			//user seen
			if(lastMessageIdUserSeen!=messagesAdapter!!.lastMessageId){
				objectViewModel.userSeen(chanelChatId,::userSeenOkCallback)
				lastMessageIdUserSeen=messagesAdapter!!.lastMessageId
			}
		}

	}
	fun loadHistoryMessages(time:Long){
		objectViewModel!!.getMessagesHistory(chanelChatId,time,::loadHistoryMessagesOkCallback)
	}
	private fun loadHistoryMessagesOkCallback(messages:MessageModels.Messages?){
		runOnUiThread {
			messagesRecyclerView !!.layoutManager =
				LinearLayoutManager(this)
			messagesAdapter!!.insertMessagesBefore(messages)
		}
	}
	fun loadMessagesBetweenTime(startTime:Long, lastTime:Long){
		objectViewModel!!.getMessagesBetweenTime(chanelChatId,startTime,lastTime,::loadMessagesBetweenTimeOkCallback)
	}
	private fun loadMessagesBetweenTimeOkCallback(messages:MessageModels.Messages?){
		runOnUiThread {
			if (messages != null) {
				messagesRecyclerView !!.layoutManager =
					LinearLayoutManager(this)
				messagesAdapter!!.insertMessages(messages.messages)
			}
		}
	}
	private fun openMemberProfile(idUser:String){
		val intent = Intent(applicationContext ,GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idUser)
		startActivity(intent)
	}

	fun longTouchMessage(messageId:String?,messageContent:String?){
		messageReplyIdOpenOption = messageId;
		messageReplyContentOpenOption = messageContent;
		messageOptionContainer!!.visibility = View.VISIBLE
	}
	fun closeMessageOptionContainer(view:View){
		messageOptionContainer!!.visibility = View.GONE
	}
	fun messageReplyOptionChoose(view:View){
		messageReplyIdReal = messageReplyIdOpenOption
		findViewById<TextView>(R.id.chanelChatDetailsActivityMessageChatReplyContent).text=messageReplyContentOpenOption
		messageReplyContainer!!.visibility = View.VISIBLE
		closeMessageOptionContainer(View(applicationContext))
	}
	fun closeMessageReplyContainer(view:View){
		messageReplyContainer!!.visibility = View.GONE
		messageReplyIdReal=null
	}

	fun messagesRecyclerViewScrollToPosition(position:Int){
		if(position>=0){
			val handler = Handler()
			handler.postDelayed({
				// Code to be executed after a delay
				messagesRecyclerView!!.smoothScrollToPosition(position)
			}, 700)
		}
	}
	fun chat(view:View){
		if(chatBoxInput!!.text.isNotEmpty()){
			objectViewModel.insert(chanelChatId,messageReplyIdReal,chatBoxInput!!.text.toString(),::chatOkCallback)
		}
	}
	fun chatOkCallback(){
		runOnUiThread {
			closeMessageReplyContainer(View(applicationContext))
			chatBoxInput!!.setText("")
		}
	}

	private fun chanelChatNewMessagesSocketCallback(newMessages:ArrayList<MessageModels.NewMessageSocket>?){
		if(newMessages!=null){
			val messages:ArrayList<MessageModels.Message> = ArrayList(newMessages.filter{it.chanelChatId==chanelChatId}.map { MessageModels.Message(it.id,it.content,it.userId,it.time,it.replyContent,it.replyTime,it.replyId) })
			runOnUiThread {
				messagesAdapter?.insertMessages(messages)
			}

		}
	}
	private fun chanelChatUserSeenSocketCallback(userSeen:ChanelChatModels.UserSeenSocket?){
		runOnUiThread {
			if (userSeen != null && userSeen.idChanelChat == chanelChatId) messagesAdapter?.updateUserSeen(
				userSeen.idUserSeen,
				userSeen.idMessage
			)
		}
	}
}