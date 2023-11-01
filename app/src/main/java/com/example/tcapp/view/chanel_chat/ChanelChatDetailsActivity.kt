package com.example.tcapp.view.chanel_chat

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.post.CreatePostActivity
import com.example.tcapp.view.post.PostsListActivity
import com.example.tcapp.view.request.CreateRequestActivity
import com.example.tcapp.view.request.RequestsListActivity
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.chanel_chat.ChanelChatDetailsViewModel

const val CHOOSE_AVATAR_REQUEST_CODE = 110
class ChanelChatDetailsActivity : CoreActivity() {
	private lateinit var objectViewModel: ChanelChatDetailsViewModel;

	private var chanelChatDetails: ChanelChatModels.ChanelChatDetails?=null;
	private var chanelChatId:String?=null;
	private var chanelChatName:String?=null;
	private var chanelChatAvatar:String?=null;

	private var newGroupChatNameBefore:String?=null;

	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
//	private var skillsContainer:RecyclerView?= null;
//	private var membersContainer:RecyclerView?= null;
	private var newGroupOwnerSelectContainer:RecyclerView?= null;
//
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ChanelChatDetailsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.chanel_chat_details)
		setContentView(R.layout.activity_chanel_chat_details)
		
//		skillsContainer =  findViewById<RecyclerView>(R.id.chanelChatDetailsActivitySkills);
//		membersContainer =  findViewById<RecyclerView>(R.id.chanelChatDetailsActivityMembers);
		newGroupOwnerSelectContainer =  findViewById<RecyclerView>(R.id.chanelChatDetailsActivityChooseNewGroupOwnerRecyclerView);
//
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
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
		println(chanelChatId)
		objectViewModel.loadData(chanelChatId)
	}
	
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
				setChanelChatAvatar(it)
			}
		})
		//set chanel chat data
		objectViewModel.chanelChat.observe(this, Observer {
			runOnUiThread {
				chanelChatDetails = it;
				setView(it)
			}
		})
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
					ChanelChatModels.Type.User -> {
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
			super.setDynamicTitleBar(chanelChat.name)
			setOptions(chanelChat);
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
			viewMembers.visibility = View.VISIBLE
			viewTeam.visibility = View.VISIBLE
		}else if(chanelChat?.type==ChanelChatModels.Type.User){
			if(chanelChat.friendId!=null&&chanelChat.friendId!=""){
				viewUser.visibility = View.VISIBLE
			}
		}else{
			//group
			viewMembers.visibility = View.VISIBLE
			exitGroupChat.visibility = View.VISIBLE
			if(chanelChat?.isGroupOwner==true){
				editAvatar.visibility = View.VISIBLE
				editName.visibility = View.VISIBLE
			}
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
		showDialogChangeName("new name")
	}
	private fun showDialogChangeName(inputDefault:String){
		val dialog = createGetStringDialog(this@ChanelChatDetailsActivity,"New group chat name",inputDefault,::changeGroupName)
		dialog.show();
	}
	private fun changeGroupName(name:String?):Boolean{
		newGroupChatNameBefore = name;
		objectViewModel.changeNewNameGroupChat(chanelChatId,name,::changeGroupNameOkCallback);
		return true;
	}
	fun changeGroupNameOkCallback(newName:String?){
		findViewById<TextView>(R.id.chanelChatDetailsActivityName).text =
			newName;
		chanelChatName = newName;
		super.setDynamicTitleBar(newName)
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
	
	
}