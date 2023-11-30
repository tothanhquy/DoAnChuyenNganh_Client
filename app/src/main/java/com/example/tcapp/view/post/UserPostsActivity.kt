package com.example.tcapp.view.post

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.post.PostModels

//import com.example.tcapp.viewmodel.post

class UserPostsActivity : CoreActivity() {
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	private var userName:String?=null;
	private var userAvatar:String?=null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.my_posts)
		setContentView(R.layout.activity_user_posts)
		
		initViews()
		loadData();
	}
	private fun loadData(){
		userName = intent.getStringExtra("user_name")?.toString()
		userAvatar = intent.getStringExtra("user_avatar")?.toString()
		
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myPostsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
//		viewActivity.addView(loadingLayout)
	}
	fun viewPostsISavedNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", PostModels.convertFilterToString(PostModels.Filter.USER_SAVED));
		intent.putExtra("authorId", "");
		startActivity(intent)
	}
	fun viewPostsILikedNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", PostModels.convertFilterToString(PostModels.Filter.USER_LIKED));
		intent.putExtra("authorId", "");
		startActivity(intent)
	}
	fun viewPostsIFollowedNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", PostModels.convertFilterToString(PostModels.Filter.USER_FOLLOWED));
		intent.putExtra("authorId", "");
		startActivity(intent)
	}
	fun viewMyPostsNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", PostModels.convertFilterToString(PostModels.Filter.USER));
		intent.putExtra("authorId", "");
		startActivity(intent)
	}
	fun viewCreatePostNavigation(view: View){
		val intent = Intent(applicationContext , CreatePostActivity::class.java)
		intent.putExtra("creator", PostModels.convertCreatorToString(PostModels.CreatorType.USER));
		intent.putExtra("authorName", userName?:"");
		intent.putExtra("authorAvatar", userAvatar?:"");
		intent.putExtra("authorId", "");
		startActivity(intent);
	}
}