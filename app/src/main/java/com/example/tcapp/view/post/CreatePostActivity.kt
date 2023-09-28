package com.example.tcapp.view.post

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.core.Genaral.Static.setTeamAvatarImage
import com.example.tcapp.core.Genaral.Static.setUserAvatarImage
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.CHOOSE_AVATAR_REQUEST_CODE
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.post.CreatePostViewModel
//import com.example.tcapp.viewmodel.post
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel

const val CHOOSE_UPLOAD_IMAGES_CODE = 109

class CreatePostActivity : CoreActivity() {
	private lateinit var objectViewModel: CreatePostViewModel;
	private var teamId:String?=null;
	private var creatorName:String?=null;
	private var creatorAvatar:String?=null;
	
	private lateinit var creator: PostModels.Creator;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var uploadImagesContainer:RecyclerView?= null;
	private var uploadImagesContainerAdapter:CreatePostImagesListRecyclerAdapter?= null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = CreatePostViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.create_post)
		setContentView(R.layout.activity_create_post)
		
		uploadImagesContainer =  findViewById(R.id.createPostActivityUploadImages);
		uploadImagesContainerAdapter = CreatePostImagesListRecyclerAdapter(this,
			arrayListOf()
		)
		uploadImagesContainer!!.setHasFixedSize(true)
		uploadImagesContainer!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		uploadImagesContainer!!.adapter = uploadImagesContainerAdapter;
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
	}
	
	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_UPLOAD_IMAGES_CODE && resultCode == RESULT_OK) {
			val selectedImages = ArrayList<Uri>()
			
			// Check if multiple images were selected
			if (data?.clipData != null) {
				val clipData = data.clipData
				val count = clipData?.itemCount ?: 0
				
				for (i in 0 until count) {
					val imageUri = clipData?.getItemAt(i)?.uri
					imageUri?.let { selectedImages.add(it) }
				}
			} else {
				// Single image selected
				val imageUri = data?.data
				imageUri?.let { selectedImages.add(it) }
			}
			if(selectedImages.size!=0)
				uploadImagesContainerAdapter!!.addList(selectedImages)
		}
	}
	
	private fun loadData() {
		val creatorString = intent.getStringExtra("creator").toString()
		creator = if(creatorString=="leader"){
			PostModels.Creator.LEADER;
		}else{
			PostModels.Creator.USER;
		}
		teamId = intent.getStringExtra("teamId")?.toString()
		creatorName = intent.getStringExtra("name")?.toString()
		creatorAvatar = intent.getStringExtra("avatar")?.toString()
		
		viewCreatorInfo();
	}
	private fun viewCreatorInfo(){
		findViewById<TextView>(R.id.createPostActivityName).text = creatorName;
		val avatarView = findViewById<ImageView>(R.id.createPostActivityAvatar);
		if(creator==PostModels.Creator.LEADER){
			Genaral.setTeamAvatarImage(this , creatorAvatar , avatarView);
		}else{
			Genaral.setUserAvatarImage(this , creatorAvatar , avatarView);
		}
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.createPostActivity)
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
	}
	
	fun createPost(view:View){
		val content = findViewById<TextView>(R.id.createPostActivityContent).text.toString();
		
		val uris = uploadImagesContainerAdapter!!.getList();
		val files = uris.map {
			a->Genaral.URIPathHelper().getPath(applicationContext, a)
		} as ArrayList<String?>;
		val creatorString = if(creator==PostModels.Creator.LEADER)"leader" else "user";
		objectViewModel.createPost(creatorString,teamId,content,files)
	}
	fun uploadImage(view: View){
		if(checkPermissionsReadFile()){
			var intent:Intent =  Intent()
				.setType("image/*")
				.setAction(Intent.ACTION_GET_CONTENT)
				.addCategory(Intent.CATEGORY_OPENABLE)
				.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
			startActivityForResult(Intent.createChooser(intent, "Select  upload images"), CHOOSE_UPLOAD_IMAGES_CODE);
		}else{
			requestPermissionsReadFile()
		}
	}
	
}