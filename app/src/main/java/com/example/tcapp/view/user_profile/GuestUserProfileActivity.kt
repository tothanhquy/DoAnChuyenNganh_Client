package com.example.tcapp.view.user_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.api.API
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.friend.CreateFriendRequestActivity
import com.example.tcapp.view.request.CreateRequestActivity
import com.example.tcapp.view.team_profile.TeamProfileEditInfoActivity
import com.example.tcapp.viewmodel.user_profile.GuestUserProfileViewModel

class GuestUserProfileActivity : CoreActivity() {
	private lateinit var viewModelObject: GuestUserProfileViewModel;
	private var idAccount:String?=null
	private var nameOfAccount:String?=null
	private var avatarOfAccount:String?=null
//	private var infoProfile:UserProfileModels.Info?=null
//	private var skillsIdProfile:ArrayList<String>?=null
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var skillsContainer:RecyclerView?= null;
	private var cVsContainer:RecyclerView?= null;
	
	private var optionsContainer:View?=null;
	private var optionAddFriend: Button?=null;
	private var optionCancelFriend: Button?=null;
	private var optionChat: Button?=null;
	private var optionLetter: Button?=null;
	private var optionRecruitLetter: Button?=null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = GuestUserProfileViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.user_profile)
		setContentView(R.layout.activity_guest_user_profile)
		
		skillsContainer =  findViewById(R.id.guestUserProfileActivitySkillsContainer);
		cVsContainer =  findViewById(R.id.guestUserProfileActivityCVsContainer);
		
		initViews()
		setRender()
		loadData()
	}
	
	private fun loadData() {
		idAccount = intent.getStringExtra("idUser").toString()
		viewModelObject.loadData(idAccount)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.guestUserProfileActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		
		optionsContainer = findViewById(R.id.guestUserProfileActivityOptions)
		optionAddFriend = findViewById(R.id.guestUserProfileActivityOptionAddFriend)
		optionCancelFriend = findViewById(R.id.guestUserProfileActivityOptionCancelFriend)
		optionChat = findViewById(R.id.guestUserProfileActivityOptionChat)
		optionLetter = findViewById(R.id.guestUserProfileActivityOptionLetter)
		optionRecruitLetter = findViewById(R.id.guestUserProfileActivityOptionRecruitLetter)
		
	}
	private fun setRender(){
		//set alert error
		viewModelObject.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		
		//set loading
		viewModelObject.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		viewModelObject.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})
		//set avatar
		viewModelObject.relationship.observe(this, Observer {
			runOnUiThread {
				setOptions(it)
			}
		})
		//set avatar
		viewModelObject.avatar.observe(this, Observer {
			runOnUiThread {
				setAvatar(it)
			}
		})
		//set user data
		viewModelObject.guestProfile.observe(this, Observer {
			runOnUiThread {
				setInfoContainer(it.info)
				setSkillsContainer(it.skills)
				setCVsContainer(it.cvs)
			}
		})
	}
	private fun setAvatar(avatarPath:String?){
		
		try{
			avatarOfAccount = avatarPath
			if(avatarPath!=null){
				val avatar = findViewById<ImageView>(R.id.guestUserProfileActivityAvatar);
				Genaral.setUserAvatarImage(this , avatarPath , avatar)
			}
		}catch(e:Exception){}
	}
	private fun setOptions(relationship: String){
		optionAddFriend!!.visibility = View.GONE
		optionCancelFriend!!.visibility = View.GONE
		optionChat!!.visibility = View.GONE
		optionLetter!!.visibility = View.GONE
		optionRecruitLetter!!.visibility = View.GONE
		if(relationship.contains("guest")){
			//do nothing
		}else if(relationship.contains("owner")){
			//do nothing
		}else {
			//user login
			if(relationship.contains("leader")){
				//do nothing
				optionRecruitLetter!!.visibility = View.VISIBLE
			}
			if(relationship.contains("friend")){
				//do nothing
				optionCancelFriend!!.visibility = View.VISIBLE
				optionChat!!.visibility = View.VISIBLE
				optionLetter!!.visibility = View.VISIBLE
			}else{
				optionAddFriend!!.visibility = View.VISIBLE
			}
		}
	}
	private fun setInfoContainer(info: UserProfileModels.Info?){
		if(info!=null){
			idAccount = info.id
			nameOfAccount = info.name
			findViewById<TextView>(R.id.guestUserProfileActivityInfoName).text=info.name;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoBirthYear).text=info.birthYear.toString();
			findViewById<TextView>(R.id.guestUserProfileActivityInfoMaxim).text=info.maxim;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoContact).text=info.contact;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoCareerTarget).text=info.careerTarget;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoEducation).text=info.education;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoWorkExperience).text=info.workExperience;
			findViewById<TextView>(R.id.guestUserProfileActivityInfoDescription).text=info.description;
		}
	}
	private fun setSkillsContainer(skills: ArrayList<UserProfileModels.Skill>?){
		if(skills!=null){
			var skillsAdapter = ProfileSkillsRecyclerAdapter(this,skills)
//			for (i in 0 until skills.size){
//				skillsAdapter.add(skills[i])
//			}
			skillsContainer!!.setHasFixedSize(true)
			skillsContainer!!.layoutManager = Genaral.Static.HorizontalLayoutAutoWrapper(this)
			skillsContainer!!.adapter = skillsAdapter;
		}
	}
	private fun setCVsContainer(cvs: ArrayList<UserProfileModels.GuestCV>?){
		if(cvs!=null){
			var cvsAdapter = GuestUserProfileCVsRecyclerAdapter(cvs)
			cvsAdapter.setCallback(::viewPDFCV)
			cVsContainer!!.setHasFixedSize(true)
			cVsContainer!!.layoutManager = LinearLayoutManager(this)
			cVsContainer!!.adapter = cvsAdapter;
		}
	}
	
	private fun viewPDFCV(idCV:String){
//		val intent = Intent(applicationContext , UserProfileViewCVPDFActivity::class.java)
//		intent.putExtra("idCV", idCV)
//		startActivity(intent)
		
		val pdfUrl = API.getBaseUrl()+"/UserProfile/viewPDFCV?idcv="+idCV;
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
		startActivity(intent)
	}
	fun optionAddFriend(view:View){
		val intent = Intent(applicationContext , CreateFriendRequestActivity::class.java)
		intent.putExtra("userId", idAccount);
		intent.putExtra("userName", nameOfAccount);
		intent.putExtra("userAvatar", avatarOfAccount);
		startActivity(intent)
	}
	fun optionCancelFriend(view:View){
		viewModelObject.cancelFriend(idAccount,::cancelFriendOkCallback);
	}
	fun cancelFriendOkCallback(){
		loadData()
	}
	fun optionChat(view:View){
	
	}
	fun optionLetter(view:View){
	
	}
	fun optionRecruitLetter(view:View){
		val intent = Intent(applicationContext , CreateRequestActivity::class.java)
		intent.putExtra("creator", "leader");
		intent.putExtra("userId", idAccount);
		intent.putExtra("userName", nameOfAccount);
		intent.putExtra("userAvatar", avatarOfAccount);
		startActivity(intent)
	}
	fun openOptions(view: View){
		optionsContainer?.visibility = View.VISIBLE
	}
	fun closeOptions(view: View){
		optionsContainer?.visibility = View.GONE
	}
}