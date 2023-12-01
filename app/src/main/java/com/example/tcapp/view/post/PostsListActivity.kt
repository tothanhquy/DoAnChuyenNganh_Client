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
import com.example.tcapp.view.project.ProjectDetailsActivity
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.post.PostsListViewModel

class PostsListActivity : CoreActivity() {
	private lateinit var objectViewModel: PostsListViewModel;
	
	private var backgroundColor:Int =0;
	private var loadingLayout:View? = null;
	private var postsListActivityLoadingContainer:ViewGroup? = null;
	
	private var postsListContainer:RecyclerView?= null;
	private var postsListContainerAdapter:PostsListRecyclerAdapter?= null;
	private var postsListViewImagesContainer:RecyclerView?= null;
	private var postsListViewImagesContainerAdapter: PostsListViewSlideImagesRecyclerAdapter?= null;
	
	private lateinit var filter: PostModels.Filter;
	private var authorId: String?=null;
	private var focusPostId: String?=null;
	private var focusPostPosition: Int=-1;
	
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
		postsListContainerAdapter!!.setCallbackOfViewProject(::viewProjectNavigation)
		postsListContainerAdapter!!.setCallbackOfViewTeam(::viewTeamNavigation)
		postsListContainerAdapter!!.setCallbackOfUserInteractPost(::userInteractPost)

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
		authorId = intent.getStringExtra("authorId")?.toString();
		val filterIntent =  intent.getStringExtra("filter").toString();
		this.filter = PostModels.convertStringToFilter(filterIntent);
		loadMorePosts();
	}
	private fun loadMorePosts(){
		if(!postsListContainerAdapter!!.isFinish)
			objectViewModel.loadPosts(filter,postsListContainerAdapter!!.timePrevious,authorId)
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
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})
		//set requests
		objectViewModel.postsList.observe(this, Observer {
			runOnUiThread {
				println(it)
				if(it!=null)setPostsContainer(it)
			}
		})
	}
	
	private fun setPostsContainer(postsList: PostModels.PostsList){
		println(postsList.posts.size)
		postsListContainer!!.setHasFixedSize(true)
		postsListContainerAdapter!!.let{
			it.isActionale = postsList.isActionable;
			it.timePrevious = postsList.timePrevious;
			it.wasLoaded = true;
			it.isFinish= postsList.isFinish;
			it.addItems(postsList.posts!!);
		}
	}
	
	private fun openSlideImages(arr:ArrayList<String>){
		findViewById<ViewGroup>(R.id.postsListActivityViewImages).visibility = View.VISIBLE;
		postsListViewImagesContainerAdapter!!.setInitList(arr);
	}
	fun closeSlideImages(view:View){
		findViewById<ViewGroup>(R.id.postsListActivityViewImages).visibility = View.GONE;
	}
	private fun openOptions(position:Int,postId:String){
		focusPostId = postId;
		focusPostPosition = position;
		findViewById<ViewGroup>(R.id.postsListActivityOptionsContainer).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		closeOptions();
	}
	private fun closeOptions(){
		findViewById<ViewGroup>(R.id.postsListActivityOptionsContainer).visibility = View.GONE;
	}
	private fun userInteractPost(holder:PostsListRecyclerAdapter.ViewHolder,postId:String,status:PostModels.UserUpdateInteractStatus){
		objectViewModel.userInteract(holder,postId,status,::okInteractCallback)
	}
	private fun okInteractCallback(holder:PostsListRecyclerAdapter.ViewHolder,postId:String,status:PostModels.PostUpdateInteractResponse){
		runOnUiThread{
			postsListContainerAdapter!!.updateInteractStatus(holder,postId,status);
		}
	}
	private fun viewUserNavigation(authorId:String){
		val intent = Intent(applicationContext , GuestUserProfileActivity::class.java)
		intent.putExtra("idUser", authorId);
		startActivity(intent)
	}
	private fun viewProjectNavigation(authorId:String){
		val intent = Intent(applicationContext , ProjectDetailsActivity::class.java)
		intent.putExtra("projectId", authorId);
		startActivity(intent)
	}
	private fun viewTeamNavigation(authorId:String){
		val intent = Intent(applicationContext , TeamProfileActivity::class.java)
		intent.putExtra("teamId", authorId);
		startActivity(intent)
	}
	fun openEditPost(view:View){
		val intent = Intent(applicationContext , PostEditActivity::class.java)
		intent.putExtra("postId", focusPostId);
		startActivity(intent)
	}
}

