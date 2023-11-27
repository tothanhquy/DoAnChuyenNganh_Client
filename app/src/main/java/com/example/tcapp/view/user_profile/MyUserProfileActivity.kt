package com.example.tcapp.view.user_profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.MyUserProfileCVsRecyclerAdapter
import com.example.tcapp.view.adapter_view.ProfileSkillsRecyclerAdapter
import com.example.tcapp.viewmodel.user_profile.MyUserProfileViewModel

const val CHOOSE_AVATAR_REQUEST_CODE = 111
class MyUserProfileActivity : CoreActivity() {
	private lateinit var myProfileViewModel: MyUserProfileViewModel;
	private var idAccount:String?=null
	private var infoProfile:UserProfileModels.Info?=null
	private var skillsIdProfile:ArrayList<String>?=null
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var skillsContainer:RecyclerView?= null;
	private var cVsContainer:RecyclerView?= null;
	
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		myProfileViewModel = MyUserProfileViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.user_profile)
		setContentView(R.layout.activity_my_user_profile)
		
		skillsContainer =  findViewById<RecyclerView>(R.id.myUserProfileActivitySkillsContainer);
		cVsContainer =  findViewById<RecyclerView>(R.id.myUserProfileActivityCVsContainer);
		
		initViews()
		setRender()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == RESULT_OK) {
			val selectedUri : Uri = data!!.data
				?: return //The uri with the location of the file
//
			val selectedPath = Genaral.URIPathHelper().getPath(applicationContext, selectedUri!!)
//			val fille = File(selectedPath)
//			println(MimeTypeMap.getSingleton().getMimeTypeFromExtension(fille.extension))
//			println(fille.extension)
			myProfileViewModel.changeAvatar(selectedPath)
		}
	}
	
	private fun loadData() {
		myProfileViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myUserProfileActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		myProfileViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		
		//set loading
		myProfileViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		myProfileViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//set avatar
		myProfileViewModel.avatar.observe(this, Observer {
			runOnUiThread {
				setAvatar(it)
			}
		})
		//set user data
		myProfileViewModel.myProfile.observe(this, Observer {
			runOnUiThread {
				setInfoContainer(it.info)
				setSkillsContainer(it.skills)
				setCVsContainer(it.cvs)
			}
		})
	}
	private fun setAvatar(avatarPath:String?){
		try{
			if(avatarPath!=null){
//			println(myProfileViewModel.userProfileAvatarsPath+avatarPath)
				val avatar = findViewById<ImageView>(R.id.myUserProfileActivityAvatar);
//			Picasso.get().load(myProfileViewModel.userProfileAvatarsPath+avatarPath).into(avatar);
//				Glide.with(applicationContext)
//					.load(myProfileViewModel.userProfileAvatarsPath+avatarPath)
//					.error(R.drawable.default_user_avatar)
//					.into(avatar)
				Genaral.setUserAvatarImage(this , avatarPath , avatar)
				
			}
		}catch(e:Exception){}
	}
	private fun setInfoContainer(info: UserProfileModels.Info?){
		if(info!=null){
			infoProfile = info
			idAccount = info.id
//			println(info)
			findViewById<TextView>(R.id.myUserProfileActivityInfoName).text=info.name;
			findViewById<TextView>(R.id.myUserProfileActivityInfoBirthYear).text=info.birthYear.toString();
			findViewById<TextView>(R.id.myUserProfileActivityInfoMaxim).text=info.maxim;
			findViewById<TextView>(R.id.myUserProfileActivityInfoContact).text=info.contact;
			findViewById<TextView>(R.id.myUserProfileActivityInfoCareerTarget).text=info.careerTarget;
			findViewById<TextView>(R.id.myUserProfileActivityInfoEducation).text=info.education;
			findViewById<TextView>(R.id.myUserProfileActivityInfoWorkExperience).text=info.workExperience;
			findViewById<TextView>(R.id.myUserProfileActivityInfoDescription).text=info.description;
		}
	}
	private fun setSkillsContainer(skills: ArrayList<UserProfileModels.Skill>?){
		if(skills!=null){
			//get list ids
			skillsIdProfile = ArrayList(skills.map { it.id!! })
			//insert new item
			var skillsAdapter = ProfileSkillsRecyclerAdapter(this,skills)
//			for (i in 0 until skills.size){
//				skillsAdapter.add(skills[i])
//			}
			skillsContainer!!.setHasFixedSize(true)
			skillsContainer!!.layoutManager = Genaral.Static.HorizontalLayoutAutoWrapper(this)
			skillsContainer!!.adapter = skillsAdapter;
		}
	}
	private fun setCVsContainer(cvs: ArrayList<UserProfileModels.MyCV>?){
		if(cvs!=null){
			var cvsAdapter = MyUserProfileCVsRecyclerAdapter(cvs)
			cvsAdapter.setCallback(::editCVNavigation)
			cVsContainer!!.setHasFixedSize(true)
			cVsContainer!!.layoutManager = LinearLayoutManager(this)
			cVsContainer!!.adapter = cvsAdapter;
//			for (i in 0 until cvs.size){
//				cvsAdapter.add(cvs[i])
//			}
		
		}
	}
	fun changeAvatar(view: View){
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
	
	fun editSkillsNavigation(view: View){
		val intent = Intent(applicationContext , MyUserProfileEditSkillsActivity::class.java)
		if(skillsIdProfile==null|| skillsIdProfile !!.size==0){
			intent.putExtra("skillsId","")
		}else{
			intent.putExtra("skillsId",skillsIdProfile!!.joinToString("<*>"))
		}
		println(skillsIdProfile!!.joinToString("<*>"))
		startActivity(intent)
	}
	fun editInfoNavigation(view: View){
		val intent = Intent(applicationContext , MyUserProfileEditInfoActivity::class.java)
		intent.putExtra("name",infoProfile!!.name)
		intent.putExtra("birth_year",infoProfile!!.birthYear)
		intent.putExtra("maxim",infoProfile!!.maxim)
		intent.putExtra("contact",infoProfile!!.contact)
		intent.putExtra("career_target",infoProfile!!.careerTarget)
		intent.putExtra("education",infoProfile!!.education)
		intent.putExtra("work_experience",infoProfile!!.workExperience)
		intent.putExtra("description",infoProfile!!.description)
		startActivity(intent)
	}
	fun viewAsGuestNavigation(view: View){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", idAccount)
		startActivity(intent)
	}
	
	private fun editCVNavigation(idCV:String,name:String,isActive:Boolean){
		val intent = Intent(applicationContext ,MyUserProfileCVActivity::class.java)
		intent.putExtra("idCV", idCV)
		intent.putExtra("name", name)
		intent.putExtra("isActive", isActive)
		intent.putExtra("method", "edit")
		startActivity(intent)
	}
	fun createCVNavigation(view:View){
		val intent = Intent(applicationContext ,MyUserProfileCVActivity::class.java)
		intent.putExtra("method", "create")
		startActivity(intent)
	}
}