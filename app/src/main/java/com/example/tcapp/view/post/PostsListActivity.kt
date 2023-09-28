package com.example.tcapp.view.post

import android.content.Intent
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
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.post.PostsListViewModel

class PostsListActivity : CoreActivity() {
	private lateinit var objectViewModel: PostsListViewModel;
	
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	private var  postsListActivityLoadingContainer:ViewGroup? = null;
	
	private var postsListContainer:RecyclerView?= null;
	private var postsListContainerAdapter:PostsListRecyclerAdapter?= null;
	private var postsListViewImagesContainer:RecyclerView?= null;
	private var postsListViewImagesContainerAdapter: PostsListViewSlideImagesRecyclerAdapter?= null;
	
	private lateinit var filter: PostModels.Filter;
	private var teamId: String?=null;
	private var postIdWhenChoose: String?=null;
	private var postPositionWhenChoose: Int=-1;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = PostsListViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.posts)
		setContentView(R.layout.activity_list_posts)
		
		
		postsListContainer =  findViewById<RecyclerView>(R.id.postsListActivityContainer);
		postsListContainerAdapter = PostsListRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		postsListContainerAdapter!!.setCallbackOfOpenSlideImages(::openSlideImages)
		postsListContainerAdapter!!.setCallbackOfOpenOptions(::openOptions)
		postsListContainerAdapter!!.setCallbackOfViewUser(::viewUserNavigation)
		postsListContainerAdapter!!.setCallbackOfViewTeam(::viewTeamNavigation)
		
		postsListContainer !!.setHasFixedSize(true)
		postsListContainer !!.layoutManager = LinearLayoutManager(this)
		postsListContainer!!.adapter =
			postsListContainerAdapter
		
		postsListViewImagesContainer =  findViewById<RecyclerView>(R.id.postsListActivityViewImagesContainer);
		postsListViewImagesContainerAdapter = PostsListViewSlideImagesRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		postsListViewImagesContainer !!.setHasFixedSize(true)
		postsListViewImagesContainer !!.layoutManager =
			LinearLayoutManager(this)
		postsListViewImagesContainer!!.adapter =
			postsListViewImagesContainerAdapter
		
		initViews()
		setRender()
		loadData()
		setAutoLoadWhenEndOfList();
	}
	
//	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
//		menuInflater.inflate(R.menu.list_menu_refresh_filter , menu)
//		return super.onCreateOptionsMenu(menu)
//	}
//
//	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
//		when (item.getItemId()) {
//			R.id.filter -> {
//				openFilter()
//			}
//			R.id.refresh -> {
//				refreshRequest()
//			}
//		}
//		return super.onOptionsItemSelected(item)
//	}
	private fun setAutoLoadWhenEndOfList(){
		findViewById<NestedScrollView>(R.id.postsListActivityScrollViewContainer)
			.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v , scrollX , scrollY , oldScrollX , oldScrollY ->
				// on scroll change we are checking when users scroll as bottom.
				//300 px
				if (scrollY + 300 > v.getChildAt(0)
						.measuredHeight - v.measuredHeight
				) {
					loadMorePosts()
				}
			})
	}
	private fun loadData() {
		teamId = intent.getStringExtra("teamId")?.toString();
		val filterIntent =  intent.getStringExtra("filter").toString();
		this.filter = PostModels.convertStringToFilter(filterIntent);
		postsListContainerAdapter!!.filter = this.filter;
		loadMorePosts();
	}
	private fun loadMorePosts(){
		if(!postsListContainerAdapter!!.isFinish)
			objectViewModel.loadPosts(filter,postsListContainerAdapter!!.timePrevious,teamId)
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.postsListActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		
		postsListActivityLoadingContainer = findViewById<ViewGroup>(R.id.postsListActivityLoadingContainer)
		val loadingMoreView = Genaral.getLoadingView(this,postsListActivityLoadingContainer!!,backgroundColor)
		postsListActivityLoadingContainer!!.addView(loadingMoreView)
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
					postsListActivityLoadingContainer?.visibility = View.VISIBLE
				}else{
					postsListActivityLoadingContainer?.visibility = View.GONE
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
				if(it!=null)setPostsContainer(it)
			}
		})
	}
	
	private fun setPostsContainer(postsListStatus: PostModels.PostsListStatus){
		postsListContainerAdapter!!.let{
			it.isActionale = postsListStatus.isActionable;
			it.timePrevious = postsListStatus.timePrevious;
			it.wasLoaded = true;
			it.isFinish= postsListStatus.isFinish;
			it.addItems(objectViewModel.posts!!);
		}
	}
	
	private fun openSlideImages(arr:ArrayList<String?>){
		findViewById<ViewGroup>(R.id.postsListActivityViewImages).visibility = View.VISIBLE;
		postsListViewImagesContainerAdapter!!.setInitList(arr);
	}
	fun closeSlideImages(view:View){
		findViewById<ViewGroup>(R.id.postsListActivityViewImages).visibility = View.GONE;
	}
	private fun openOptions(position:Int,postId:String, relationship:PostModels.Relationship, isActive:Boolean, wasSaved:Boolean){
		val setActive = findViewById<Button>(R.id.postsListActivityOptionSetActive);
		val setInactive = findViewById<Button>(R.id.postsListActivityOptionSetInactive);
		val setSave = findViewById<Button>(R.id.postsListActivityOptionSetSave);
		val setUnsave = findViewById<Button>(R.id.postsListActivityOptionSetUnsave);
		setActive.visibility = View.GONE;
		setInactive.visibility = View.GONE;
		setSave.visibility = View.GONE;
		setUnsave.visibility = View.GONE;
		postIdWhenChoose = postId;
		postPositionWhenChoose = position;
		if(relationship==PostModels.Relationship.OWNER){
			if(isActive){
				setInactive.visibility = View.VISIBLE;
			}else{
				setActive.visibility = View.VISIBLE;
			}
		}
		if(wasSaved){
			setUnsave.visibility = View.VISIBLE;
		}else {
			setSave.visibility = View.VISIBLE;
		}
		findViewById<ViewGroup>(R.id.postsListActivityOptionsContainer).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		closeOptions();
	}
	private fun closeOptions(){
		findViewById<ViewGroup>(R.id.postsListActivityOptionsContainer).visibility = View.GONE;
	}
	fun  toggleIsActive(view:View){
		updatePost(postIdWhenChoose!!,PostModels.StatusUpdate.ACTIVE);
	}
	fun  toggleWasSaved(view:View){
		updatePost(postIdWhenChoose!!,PostModels.StatusUpdate.SAVE);
	}
	private fun updatePost(postId:String,status:PostModels.StatusUpdate){
		objectViewModel.updatePost(postId,status,::okUpdateCallback)
	}
	private fun okUpdateCallback(status:PostModels.StatusUpdate){
		runOnUiThread{
			if(status==PostModels.StatusUpdate.ACTIVE){
				postsListContainerAdapter!!.toggleActiveStatus(postPositionWhenChoose);
			}else{
				postsListContainerAdapter!!.toggleSaveStatus(postPositionWhenChoose);
			}
			closeOptions();
		}
	}
	private fun viewUserNavigation(userId:String){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", userId);
		startActivity(intent)
	}
	private fun viewTeamNavigation(teamId:String){
		val intent = Intent(applicationContext , TeamProfileActivity::class.java)
		intent.putExtra("teamId", teamId);
		startActivity(intent)
	}
}

//private fun ScrollView.setOnScrollChangeListener(onScrollChangeListener : NestedScrollView.OnScrollChangeListener) {

//}
