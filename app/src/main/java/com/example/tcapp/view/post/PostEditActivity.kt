package com.example.tcapp.view.post

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
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
import com.example.tcapp.viewmodel.post.PostEditViewModel
//import com.example.tcapp.viewmodel.post
import com.example.tcapp.viewmodel.team_profile.TeamProfileViewModel
import khttp.post


class PostEditActivity : CoreActivity() {
	val CHOOSE_UPLOAD_IMAGES_CODE = 178

	private lateinit var objectViewModel: PostEditViewModel;
	private lateinit var objectCategoryKeywordsViewModel: CategoryKeywordsViewModel;

	private var oldCategoryKeywords: ArrayList<GeneralModel.Keyword>?=ArrayList();
	private var allCategoryKeywords: ArrayList<GeneralModel.Keyword>?=ArrayList();

	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;

	private var  postId:String? = null;

	private var uploadImagesContainerRecyclerView:RecyclerView?= null;
	private var uploadImagesContainerAdapter:CreatePostImagesListRecyclerAdapter?= null;

	private var oldImagesRecyclerView:RecyclerView?= null;
	private var oldImagesAdapter:PostEditOldImagesRecyclerAdapter?= null;

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
		objectViewModel = PostEditViewModel(applicationContext)
		objectCategoryKeywordsViewModel = CategoryKeywordsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.edit_post)
		setContentView(R.layout.activity_post_edit)

		changeCategoryKeywordsContainer =  findViewById(R.id.postEditActivityChangeCategoryKeywordsContainer);

		oldImagesRecyclerView =  findViewById(R.id.postEditActivityOldImagesRecyclerView);
		oldImagesAdapter = PostEditOldImagesRecyclerAdapter(this,
			arrayListOf()
		)
		oldImagesRecyclerView!!.setHasFixedSize(true)
		oldImagesRecyclerView!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		oldImagesRecyclerView!!.adapter = oldImagesAdapter;

		uploadImagesContainerRecyclerView =  findViewById(R.id.postEditActivityUploadImagesRecyclerView);
		uploadImagesContainerAdapter = CreatePostImagesListRecyclerAdapter(this,
			arrayListOf()
		)
		uploadImagesContainerRecyclerView!!.setHasFixedSize(true)
		uploadImagesContainerRecyclerView!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		uploadImagesContainerRecyclerView!!.adapter = uploadImagesContainerAdapter;

		keywordsSelectedRecyclerView =  findViewById(R.id.postEditActivityChangeCategoryKeywordsContainerKeywordsSelectedRecyclerView);
		keywordsSelectedAdapter = ShowSelectedKeywordsRecyclerAdapter(this, arrayListOf())
		keywordsSelectedRecyclerView!!.setHasFixedSize(false)
		keywordsSelectedRecyclerView!!.layoutManager = LinearLayoutManager(this)
		keywordsSelectedRecyclerView!!.adapter = keywordsSelectedAdapter

		allKeywordsRecyclerView =  findViewById(R.id.postEditActivityChangeCategoryKeywordsContainerAllKeywordsRecyclerView);
		allKeywordsAdapter = SelectKeywordsRecyclerAdapter(this, arrayListOf())
		allKeywordsAdapter!!.setClickCallback(::selectKeywordsAdapterCallbackClick)
		allKeywordsRecyclerView!!.setHasFixedSize(false)
		allKeywordsRecyclerView!!.layoutManager = LinearLayoutManager(this)
		allKeywordsRecyclerView!!.adapter = allKeywordsAdapter
		allKeywordsAdapter!!.setIsCheckOfItems(arrayListOf(),true)

		keywordsViewContainerRecyclerView =  findViewById(R.id.postEditActivityCategoryKeywordsViewRecyclerView);
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
		postId = intent.getStringExtra("postId").toString()
		objectCategoryKeywordsViewModel.getAllKeywords()
	}
	private fun setEditDetailsView(post:PostModels.PostOwnerDetail){
		findViewById<TextView>(R.id.postEditActivityName).text = post.authorName;
		val avatarView = findViewById<ImageView>(R.id.postEditActivityAvatar);
		when (post.authorType) {
			PostModels.AuthorType.TEAM -> {
				Genaral.setTeamAvatarImage(this , post.authorAvatar , avatarView);
			}
			PostModels.AuthorType.PROJECT -> {
				Genaral.setProjectImageWithPlaceholder(this , post.authorAvatar , avatarView)
			}
			else -> {
				Genaral.setUserAvatarImage(this , post.authorAvatar , avatarView);
			}
		}
		val postKeywords = ArrayList(allCategoryKeywords!!.filter { a -> post.categoryKeywordsId!!.indexOfFirst { it==a.id}!=-1 });

		oldCategoryKeywords = postKeywords;
		keywordsViewContainerAdapter?.setList(ArrayList(postKeywords!!.map{it.name}));
		keywordsSelectedAdapter?.setInitList(postKeywords);
		allKeywordsAdapter?.setIsCheckOfAll(false);
		allKeywordsAdapter?.setIsCheckOfItems(postKeywords,true);
		oldImagesAdapter?.setInitList(ArrayList(post.images!!.map{it!!}));
		findViewById<TextView>(R.id.postEditActivityContent).text = post.content;
		findViewById<CheckBox>(R.id.postEditActivityIsActive).isChecked = post.isActive;
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.postEditActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		searchInput = findViewById(R.id.postEditActivityChangeCategoryKeywordsContainerSearchInput)
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

		objectViewModel.postEditDetails.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setEditDetailsView(it!!)
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
	
	fun updatePost(view:View){
		val content = findViewById<TextView>(R.id.postEditActivityContent).text.toString();
		val isActive = findViewById<CheckBox>(R.id.postEditActivityIsActive).isChecked

		val uris = uploadImagesContainerAdapter!!.getList();
		val files = uris.map {
			a->Genaral.URIPathHelper().getPath(applicationContext, a)
		} as ArrayList<String?>;
		val categoryKeywordsId = ArrayList(oldCategoryKeywords!!.map{it.id})
		val oldImages = oldImagesAdapter!!.getResList()
		objectViewModel.updatePost(postId!!,isActive,content,categoryKeywordsId,oldImages,files,::updatePostOkCallback)
	}
	private fun updatePostOkCallback(){
		runOnUiThread {
			val intent = Intent(applicationContext , PostDetailsActivity::class.java)
			intent.putExtra("postId", postId!!);
			startActivity(intent)
			this.finish()
		}
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
			allCategoryKeywords = keywords
			allKeywordsAdapter?.setInitList(keywords)
		}
		objectViewModel.loadPostEditDetails(postId)
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

	fun deletePost(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to delete this post.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.deletePost(postId!!,::deletePostOkCallback)
			}
		)
	}
	private fun deletePostOkCallback(){
		runOnUiThread {
			finish()
		}
	}
}