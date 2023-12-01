package com.example.tcapp.view.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.search.SearchModels
import com.example.tcapp.view.adapter_view.*
import com.example.tcapp.view.project.ProjectDetailsActivity
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity
import com.example.tcapp.viewmodel.search.SearchViewModel

class SearchActivity : CoreActivity() {
	private lateinit var objectViewModel: SearchViewModel;

	public var searchObjects: SearchModels.SearchItems?=null;
	
	private var backgroundColor:Int =0;
	private var loadingLayout:View? = null;
	private var searchActivityLoadingContainer:ViewGroup? = null;
	
	private var searchActivityScrollViewContainer:NestedScrollView?= null;

	private var searchObjectsContainer:RecyclerView?= null;
	private var searchObjectsContainerAdapter:SearchObjectsRecyclerAdapter?= null;

	private var searchInput:EditText?= null;
	private var radioUser:RadioButton?= null;
	private var radioTeam:RadioButton?= null;
	private var radioProject:RadioButton?= null;

//	private lateinit var type: SearchModels.ObjectType;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = SearchViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.search)
		super.hideTitle()
		setContentView(R.layout.activity_search)

		searchObjectsContainer =  findViewById<RecyclerView>(R.id.searchActivityRecyclerView);
		searchObjectsContainer!!.layoutManager = LinearLayoutManager(this)
		searchObjectsContainerAdapter = SearchObjectsRecyclerAdapter(applicationContext,
			arrayListOf()
		)
		searchObjectsContainerAdapter!!.setCallbackOfViewUser(::viewUserNavigation)
		searchObjectsContainerAdapter!!.setCallbackOfViewProject(::viewProjectNavigation)
		searchObjectsContainerAdapter!!.setCallbackOfViewTeam(::viewTeamNavigation)
		searchObjectsContainer!!.adapter = searchObjectsContainerAdapter

		initViews()
		setRender()
		setAutoLoadWhenEndOfList();
	}

	private fun setAutoLoadWhenEndOfList(){
		findViewById<NestedScrollView>(R.id.searchActivityScrollViewContainer)
			.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v , scrollX , scrollY , oldScrollX , oldScrollY ->
				// on scroll change we are checking when users scroll as bottom.
				//300 px
				if (scrollY + 300 > v.getChildAt(0)
						.measuredHeight - v.measuredHeight
				) {
					addMoreItems()
				}
			})
	}
	fun search(view:View){
		val input = searchInput!!.text.toString()
		val type = getSearchType();
		objectViewModel.loadObjects(type,input);
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.searchActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		
		searchActivityLoadingContainer = findViewById<ViewGroup>(R.id.searchActivityLoadingContainer)
		val loadingMoreView = Genaral.getLoadingView(this,searchActivityLoadingContainer!!,backgroundColor)
		searchActivityLoadingContainer!!.addView(loadingMoreView)

		searchInput = findViewById(R.id.searchActivitySearchInput)
		radioUser = findViewById(R.id.searchActivityRadioUser)
		radioTeam = findViewById(R.id.searchActivityRadioTeam)
		radioProject = findViewById(R.id.searchActivityRadioProject)
		searchActivityScrollViewContainer = findViewById(R.id.searchActivityScrollViewContainer)
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
		//set requests
		objectViewModel.searchObjects.observe(this, Observer {
			runOnUiThread {
				if(it!=null)setPostsContainer(it)
			}
		})
	}
	private fun addMoreItems(){
		if(searchObjectsContainerAdapter!!.isFinish)return;
		searchObjectsContainer!!.setHasFixedSize(true)
		searchActivityLoadingContainer?.visibility = View.VISIBLE
		var nextCount = searchObjects!!.items!!.size-searchObjectsContainerAdapter!!.itemCount;
		if(nextCount>searchObjectsContainerAdapter!!.ITEM_NUMBER_PER_PAGE){
			nextCount = searchObjectsContainerAdapter!!.ITEM_NUMBER_PER_PAGE;
		}
		if(nextCount==0){
			//finish
			searchObjectsContainerAdapter!!.isFinish=true;
		}else{
			val subItem = ArrayList(searchObjects!!.items!!.subList(searchObjectsContainerAdapter!!.itemCount,searchObjectsContainerAdapter!!.itemCount+nextCount));
			searchObjectsContainerAdapter!!.addItems(subItem)
		}
		searchActivityLoadingContainer?.visibility = View.GONE
	}
	private fun setPostsContainer(searchObjects: SearchModels.SearchItems?){
		this.searchObjects=searchObjects;
		searchObjectsContainer!!.setHasFixedSize(true)
		searchObjectsContainerAdapter!!.let{
			it.setInitList(arrayListOf())
			it.wasLoaded = true;
			it.isFinish= false;
		}
		println(1)
		addMoreItems()
	}
	
	private fun getSearchType():SearchModels.ObjectType{
		if(radioUser!!.isChecked){
			return SearchModels.ObjectType.User;
		}else if(radioTeam!!.isChecked){
			return SearchModels.ObjectType.Team;
		}else{
			return SearchModels.ObjectType.Project;
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
	fun goToTop(view:View){
		if(searchObjectsContainerAdapter!!.itemCount>0){
			searchActivityScrollViewContainer!!.smoothScrollTo(0,0)
		}
	}
}

