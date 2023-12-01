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
import android.widget.LinearLayout
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
import com.example.tcapp.model.GeneralModel
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.project.ProjectDetailsActivity
import com.example.tcapp.view.team_profile.CHOOSE_AVATAR_REQUEST_CODE
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.category_keyword.CategoryKeywordsViewModel
import com.example.tcapp.viewmodel.post.CreatePostViewModel
//import com.example.tcapp.viewmodel.post
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel

const val CHOOSE_UPLOAD_IMAGES_CODE = 109

class CreatePostActivity : CoreActivity() {
	private lateinit var objectViewModel: CreatePostViewModel;
	private lateinit var objectCategoryKeywordsViewModel: CategoryKeywordsViewModel;

	private var authorId:String?=null;
	private var authorName:String?=null;
	private var authorAvatar:String?=null;
	
	private lateinit var creator: PostModels.CreatorType;
	private var oldCategoryKeywords: ArrayList<GeneralModel.Keyword>?=ArrayList();

	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var uploadImagesContainerRecyclerView:RecyclerView?= null;
	private var uploadImagesContainerAdapter:CreatePostImagesListRecyclerAdapter?= null;

	private var keywordsViewContainerRecyclerView:RecyclerView?= null;
	private var keywordsViewContainerAdapter:StringListRecyclerAdapter?= null;

	private var keywordsSelectedAdapter: ShowSelectedKeywordsRecyclerAdapter? = null;
	private var allKeywordsAdapter: SelectKeywordsRecyclerAdapter? = null;
	private var keywordsSelectedRecyclerView:RecyclerView? = null;
	private var allKeywordsRecyclerView:RecyclerView? = null;
	private var searchInput:EditText? = null;
	private var changeCategoryKeywordsContainer:LinearLayout? = null;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = CreatePostViewModel(applicationContext)
		objectCategoryKeywordsViewModel = CategoryKeywordsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.create_post)
		setContentView(R.layout.activity_create_post)


		keywordsSelectedRecyclerView = findViewById(R.id.createPostActivityChangeCategoryKeywordsContainerKeywordsSelectedRecyclerView)
		allKeywordsRecyclerView = findViewById(R.id.createPostActivityChangeCategoryKeywordsContainerAllKeywordsRecyclerView)
		keywordsViewContainerRecyclerView = findViewById(R.id.createPostActivityCategoryKeywordsViewRecyclerView)
		searchInput = findViewById(R.id.createPostActivityChangeCategoryKeywordsContainerSearchInput)
		changeCategoryKeywordsContainer = findViewById(R.id.createPostChangeCategoryKeywordsContainer)
		uploadImagesContainerRecyclerView =  findViewById(R.id.createPostActivityUploadImagesRecyclerView);

		uploadImagesContainerAdapter = CreatePostImagesListRecyclerAdapter(this,
			arrayListOf()
		)
		uploadImagesContainerRecyclerView!!.setHasFixedSize(true)
		uploadImagesContainerRecyclerView!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		uploadImagesContainerRecyclerView!!.adapter = uploadImagesContainerAdapter;

		keywordsSelectedAdapter = ShowSelectedKeywordsRecyclerAdapter(this, arrayListOf())
		keywordsSelectedRecyclerView!!.setHasFixedSize(false)
		keywordsSelectedRecyclerView!!.layoutManager = LinearLayoutManager(this)
		keywordsSelectedRecyclerView!!.adapter = keywordsSelectedAdapter

		allKeywordsAdapter = SelectKeywordsRecyclerAdapter(this, arrayListOf())
		allKeywordsAdapter!!.setClickCallback(::selectKeywordsAdapterCallbackClick)
		allKeywordsRecyclerView!!.setHasFixedSize(false)
		allKeywordsRecyclerView!!.layoutManager = LinearLayoutManager(this)
		allKeywordsRecyclerView!!.adapter = allKeywordsAdapter
		allKeywordsAdapter!!.setIsCheckOfItems(arrayListOf(),true)

		keywordsViewContainerAdapter = StringListRecyclerAdapter(applicationContext, arrayListOf());
		keywordsViewContainerRecyclerView!!.setHasFixedSize(true)
		keywordsViewContainerRecyclerView!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		keywordsViewContainerRecyclerView!!.adapter = keywordsViewContainerAdapter;
		
		initViews()
		setRender()
		loadData()
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
		creator = PostModels.convertStringToCreator(creatorString);

		authorId = intent.getStringExtra("authorId")?.toString()
		authorName = intent.getStringExtra("authorName")?.toString()
		authorAvatar = intent.getStringExtra("authorAvatar")?.toString()
		
		viewAuthorInfo();
		objectCategoryKeywordsViewModel.getAllKeywords()
	}
	private fun viewAuthorInfo(){
		findViewById<TextView>(R.id.createPostActivityName).text = authorName;
		val avatarView = findViewById<ImageView>(R.id.createPostActivityAvatar);
		when (creator) {
			PostModels.CreatorType.TEAM -> {
				Genaral.setTeamAvatarImage(this , authorAvatar , avatarView);
			}
			PostModels.CreatorType.PROJECT -> {
				Genaral.setProjectImageWithPlaceholder(this , authorAvatar , avatarView)
			}
			else -> {
				Genaral.setUserAvatarImage(this , authorAvatar , avatarView);
			}
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
		//set alert error
		objectCategoryKeywordsViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set loading
		objectCategoryKeywordsViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		objectCategoryKeywordsViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})

		objectCategoryKeywordsViewModel.allKeywords.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setAllCategoryKeywordsContainer(it.keywords)
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
		val categoryKeywordsId = ArrayList(oldCategoryKeywords!!.map{it.id})
		objectViewModel.createPost(creator,authorId,content,categoryKeywordsId,files,::createPostOkCallback)
	}
	private fun createPostOkCallback(newPostId:String?,uploadImageSuccessNumber:String?){
		val intent = Intent(applicationContext , PostDetailsActivity::class.java)
		intent.putExtra("postId", newPostId)
		startActivity(intent)
		finish()
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

	private fun setAllCategoryKeywordsContainer(keywords: ArrayList<GeneralModel.Keyword>?){
		if(keywords!=null){
			allKeywordsAdapter?.setInitList(keywords)
		}
	}
	fun search(view:View){
		allKeywordsAdapter?.filterByName(searchInput!!.text.toString())
	}
	fun toggleShowSelectedKeywordsContainer(view:View){
		keywordsSelectedAdapter!!.toggleIsContainerShow()
	}
	private fun selectKeywordsAdapterCallbackClick(selectedItem: GeneralModel.Keyword, isShow:Boolean){
		keywordsSelectedAdapter?.toggleItem(selectedItem,isShow)
	}

	fun closeChangeCategoryKeywordsContainer(view:View){
		//reset is check
		allKeywordsAdapter?.setIsCheckOfAll(false)
		allKeywordsAdapter?.setIsCheckOfItems(oldCategoryKeywords!!,true)
		keywordsSelectedAdapter?.setInitList(oldCategoryKeywords!!)
		closeChangeCategoryKeywordsContainer();
	}
	private fun closeChangeCategoryKeywordsContainer(){
		changeCategoryKeywordsContainer?.visibility=View.GONE;
	}
	fun openChangeCategoryKeywordsContainer(view:View){
		changeCategoryKeywordsContainer?.visibility=View.VISIBLE;
	}
	fun changeCategoryKeywords(view:View){
		oldCategoryKeywords = allKeywordsAdapter!!.getKeywordsIsCheck();
		keywordsViewContainerAdapter?.setList(ArrayList(oldCategoryKeywords!!.map{it.name}));
		closeChangeCategoryKeywordsContainer();
	}


}