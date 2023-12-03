package com.example.tcapp.view.post

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.comment.CommentModels
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.project.ProjectDetailsActivity
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.comment.CommentViewModel
import com.example.tcapp.viewmodel.post.PostsListViewModel

class PostDetailsActivity : CoreActivity() {
	private lateinit var objectViewModel: PostsListViewModel;
	private lateinit var objectCommentViewModel: CommentViewModel;

	private var backgroundColor:Int =0;
	private var loadingLayout:View? = null;
	private var postsListActivityLoadingContainer:ViewGroup? = null;
	
	private var postsListContainer:RecyclerView?= null;
	private var postsListContainerAdapter:PostsListRecyclerAdapter?= null;
	private var postsListViewImagesContainer:RecyclerView?= null;
	private var postsListViewImagesContainerAdapter: PostsListViewSlideImagesRecyclerAdapter?= null;
	
	private var postId: String?=null;
	private var focusPostId: String?=null;
	private var focusPostPosition: Int=-1;

	private var commentsContainer:RecyclerView?= null;
	private var commentsAdapter: CommentsRecyclerAdapter?= null;

	private var commentReplyId: String?=null;
	private var focusCommentId: String?=null;
	private var focusCommentContent: String?=null;
	private var focusCommentHolder: CommentsRecyclerAdapter.ViewHolder?=null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = PostsListViewModel(applicationContext)
		objectCommentViewModel = CommentViewModel(applicationContext)

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
		postsListContainerAdapter!!.setCallbackOfOpenComment(::openCommentContainer)

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

		commentsContainer =  findViewById<RecyclerView>(R.id.postsListActivityCommentContainerRecyclerView);
		commentsAdapter = CommentsRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		commentsAdapter!!.setCallbackReplyComment(::setReplyComment)
		commentsAdapter!!.setCallbackLongTouchComment(::openCommentOptions)
		commentsAdapter!!.setCallbackOfViewUser(::viewUserNavigation)
		commentsAdapter!!.setCallbackLoadMore(::loadMoreComment)
		commentsAdapter!!.setCallbackOfUserInteractComment(::userInteractComment)
		commentsContainer !!.setHasFixedSize(true)
		commentsContainer !!.layoutManager = LinearLayoutManager(this)
		commentsContainer!!.adapter =
			commentsAdapter

		initViews()
		setRender()
		loadData()
	}

	private fun loadData() {
		postId = intent.getStringExtra("postId")?.toString();
		loadPostDetails();
	}
	private fun loadPostDetails(){
		if(!postsListContainerAdapter!!.isFinish)
			objectViewModel.loadPostDetails(postId)
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
		objectViewModel.postDetails.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setPostsContainer(it)
			}
		})

		objectCommentViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})

		//set loading
		objectCommentViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		objectCommentViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})
	}
	
	private fun setPostsContainer(postsList: PostModels.PostsList){
		postsListContainerAdapter!!.let{
			it.isActionale = postsList.isActionable;
			it.timePrevious = postsList.timePrevious;
			it.wasLoaded = true;
			it.isFinish= postsList.isFinish;
			it.addItems(postsList.posts!!);
		}
		postsListContainer?.setHasFixedSize(true)
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




	fun closeCommentContainer(view:View){
		findViewById<LinearLayout>(R.id.postsListActivityCommentContainer).visibility=View.GONE
		commentsAdapter!!.postId=null;
		commentsAdapter!!.setInitList(arrayListOf());
		closeCommentReplyContainer(View(applicationContext))
		findViewById<EditText>(R.id.postsListActivityCommentContainerInput).setText("")
	}
	private fun openCommentContainer(postId:String,isPostAuthor:Boolean){
		commentsAdapter!!.postId = postId;
		commentsAdapter!!.isPostAuthor = isPostAuthor;
		findViewById<LinearLayout>(R.id.postsListActivityCommentContainer).visibility=View.VISIBLE
		loadMoreComment(null,0)//first
	}
	private fun loadMoreComment(replyId:String?,time:Long){
		objectCommentViewModel.getComments(commentsAdapter!!.postId,replyId,time,::loadOldComments)
	}
	private fun loadOldComments(comments: CommentModels.Comments?){
		if(comments!=null){
			runOnUiThread {
				if(commentsAdapter!!.itemCount==0)commentsContainer !!.layoutManager =
					LinearLayoutManager(this)
				commentsAdapter!!.isActionale=comments.isActionable
				commentsAdapter!!.addItems(comments.comments!!,false)
			}
		}
	}
	fun closeCommentReplyContainer(view:View){
		commentReplyId=null;
		findViewById<LinearLayout>(R.id.postsListActivityCommentContainerReplyContainer).visibility=View.GONE
	}
	private fun setReplyComment(commentId:String?,commentContent:String?){
		commentReplyId=commentId;
		findViewById<TextView>(R.id.postsListActivityCommentContainerReplyContent).text = commentContent;
		openCommentReplyContainer()
	}
	private fun openCommentReplyContainer(){
		findViewById<LinearLayout>(R.id.postsListActivityCommentContainerReplyContainer).visibility=View.VISIBLE
	}
	fun closeCommentOptions(view:View){
		focusCommentId=null;
		focusCommentContent=null;
		focusCommentHolder=null;
		findViewById<ConstraintLayout>(R.id.postsListActivityCommentOptionsContainer).visibility=View.GONE
	}
	private fun openCommentOptions(holder:CommentsRecyclerAdapter.ViewHolder,commentId:String?,commentContent:String?){
		focusCommentId=commentId;
		focusCommentContent=commentContent;
		focusCommentHolder=holder;
		findViewById<ConstraintLayout>(R.id.postsListActivityCommentOptionsContainer).visibility=View.VISIBLE
	}
	private fun userInteractComment(holder:CommentsRecyclerAdapter.ViewHolder,commentId:String,status: CommentModels.CommentUpdateInteractRequestStatus){
		objectCommentViewModel.userInteract(holder,commentId,status,::okInteractCommentCallback)
	}
	private fun okInteractCommentCallback(holder:CommentsRecyclerAdapter.ViewHolder,commentId:String,status: CommentModels.CommentUpdateInteractResponse){
		runOnUiThread{
			commentsAdapter!!.updateInteractStatus(holder,commentId,status);
			closeCommentOptions(View(applicationContext))
		}
	}
	fun deleteComment(view:View){
		val content = focusCommentContent!!.substring(0..100)
		this.showAskDialog(
			"Important!",
			"Do you ready want to delete comment'$content'",
			fun(dialogInterface: DialogInterface, i:Int){
				userInteractComment(focusCommentHolder!!,focusCommentId!!, CommentModels.CommentUpdateInteractRequestStatus.DELETE);
			}
		)
	}
	fun createComment(view:View){
		val input:String = findViewById<EditText>(R.id.postsListActivityCommentContainerInput).text.toString()
		if(input.isNotEmpty()){
			objectCommentViewModel!!.create(commentsAdapter!!.postId!!, commentReplyId,input,::loadCreateComment)
		}
	}
	private fun loadCreateComment(comment: CommentModels.Comment?){
		if(comment!=null){
			runOnUiThread {
				if(commentsAdapter!!.itemCount==0)commentsContainer !!.layoutManager =
					LinearLayoutManager(this)
				findViewById<EditText>(R.id.postsListActivityCommentContainerInput).setText("")
				commentsAdapter!!.addItems(arrayListOf(comment),true)
			}
		}
	}
}

