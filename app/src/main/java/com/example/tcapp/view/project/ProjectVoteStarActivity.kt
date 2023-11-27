package com.example.tcapp.view.project

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.viewmodel.project.ProjectVoteStarViewModel


class ProjectVoteStarActivity : CoreActivity() {
	private lateinit var viewModelObject: ProjectVoteStarViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var projectId:String?=null;
	private var projectName:TextView? =null;
	private var projectAvatar:ImageView? =null;

	private var star1:ImageView? =null;
	private var star2:ImageView? =null;
	private var star3:ImageView? =null;
	private var star4:ImageView? =null;
	private var star5:ImageView? =null;

	private var beforeNumber:Int=5;
	
	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = ProjectVoteStarViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.vote_star)
		setContentView(R.layout.activity_project_vote_star)
		
		projectName = findViewById(R.id.projectVoteStarActivityProjectName)
		projectAvatar = findViewById(R.id.projectVoteStarActivityProjectAvatar)

		star1 = findViewById(R.id.projectVoteStarActivityStar1)
		star2 = findViewById(R.id.projectVoteStarActivityStar2)
		star3 = findViewById(R.id.projectVoteStarActivityStar3)
		star4 = findViewById(R.id.projectVoteStarActivityStar4)
		star5 = findViewById(R.id.projectVoteStarActivityStar5)

		initViews()
		setRender()
		loadData()
	}

	private fun changeStar(number:Int){
		if(number>beforeNumber){
			for(i in beforeNumber+1..number){
				changeBackgroundStar(i,true)
			}
		}else if(number<beforeNumber){
			for(i in number+1..beforeNumber){
				changeBackgroundStar(i,false)
			}
		}
		beforeNumber=number
	}
	private fun changeBackgroundStar(index:Int,isChoose:Boolean){
		var starView = when(index){
			1->star1
			2->star2
			3->star3
			4->star4
			else->star5
		}
		if(isChoose){
			starView?.setImageResource(R.drawable.vote_star_icon)
		}else{
			starView?.setImageResource(R.drawable.vote_star_empty_icon)
		}
	}
	public fun chooseStar(view:View){
		when (view.id) {
			R.id.projectVoteStarActivityStar1 -> {
				changeStar(1)
			}
			R.id.projectVoteStarActivityStar2 -> {
				changeStar(2)
			}
			R.id.projectVoteStarActivityStar3 -> {
				changeStar(3)
			}
			R.id.projectVoteStarActivityStar4 -> {
				changeStar(4)
			}
			else -> {
				changeStar(5)
			}
		}
	}
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString();
		val name = intent.getStringExtra("name").toString();
		projectName?.text=name
		val avatar = intent.getStringExtra("avatar").toString();
		Genaral.setProjectImageWithPlaceholder(applicationContext,avatar,projectAvatar!!)
		viewModelObject.getMyVoteStar(projectId,::getMyStarOkCallback)
	}
	private fun getMyStarOkCallback(star:Int){
		changeStar(star)
	}
	public fun voteStar(view:View){
		viewModelObject.voteStar(projectId,beforeNumber,::voteStarOkCallback);
	}
	private fun voteStarOkCallback(){
		runOnUiThread{
			finish()
		}
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectVoteStarActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
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
	}
	
}