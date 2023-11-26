package com.example.tcapp.view.project

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.GeneralModel
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.SelectKeywordsRecyclerAdapter
import com.example.tcapp.view.adapter_view.ShowSelectedKeywordsRecyclerAdapter
import com.example.tcapp.viewmodel.project.ProjectEditCategoryKeywordsViewModel


class ProjectEditCategoryKeywordsActivity : CoreActivity() {
	private lateinit var viewModelObject: ProjectEditCategoryKeywordsViewModel;
	private var backgroundColor:Int =0
	private var loadingLayout:View? = null;
	
	private var projectId:String? = null;

	private var  keywordsSelectedAdapter: ShowSelectedKeywordsRecyclerAdapter? = null;
	private var  allKeywordsAdapter: SelectKeywordsRecyclerAdapter? = null;
	
	private var keywordsSelectedRecyclerView:RecyclerView? = null;
	private var allKeywordsRecyclerView:RecyclerView? = null;
	private var searchInput:EditText? = null;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = ProjectEditCategoryKeywordsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.info)
		setContentView(R.layout.activity_project_edit_category_keywords)


		initViews()
		setRender()

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


		loadData()
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.edit_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.save -> {
				saveKeywords()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun saveKeywords(){
		viewModelObject.saveKeywords(projectId,allKeywordsAdapter!!.getIdIsCheck())
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString()
		viewModelObject.getAllKeywords()
		viewModelObject.getKeywordsOfProject(projectId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectEditCategoryKeywordsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		keywordsSelectedRecyclerView = findViewById(R.id.projectEditCategoryKeywordsActivityKeywordsSelectedRecyclerView)
		allKeywordsRecyclerView = findViewById(R.id.projectEditCategoryKeywordsActivityAllKeywordsRecyclerView)
		searchInput = findViewById(R.id.projectEditCategoryKeywordsActivitySearchInput)
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
		//set alert notification
		viewModelObject.allKeywords.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setAllKeywordsAdapter(it)
				}
			}
		})
		viewModelObject.keywordsOfProject.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setShowSelectedKeywordsAdapter(it)
				}
			}
		})
	}
	fun search(view:View){
		allKeywordsAdapter?.filterByName(searchInput!!.text.toString())
	}
	fun toggleShowSelectedKeywordsContainer(view:View){
		keywordsSelectedAdapter!!.toggleIsContainerShow()
	}
	private fun setShowSelectedKeywordsAdapter(keywords:ArrayList<ProjectModels.CategoryKeyword>){
		//set selected keywords
		val generalKeywords = ArrayList(keywords.map{GeneralModel.Keyword(it.id,it.name)})
		keywordsSelectedRecyclerView!!.setHasFixedSize(false)
		keywordsSelectedAdapter!!.setInitList(generalKeywords)
		allKeywordsAdapter!!.setIsCheckOfItems(generalKeywords,true)
	}
	private fun setAllKeywordsAdapter(keywords:ArrayList<ProjectModels.CategoryKeyword>){
		val generalKeywords = ArrayList(keywords.map{GeneralModel.Keyword(it.id,it.name)})
		allKeywordsRecyclerView!!.setHasFixedSize(false)
		allKeywordsAdapter!!.setInitList(generalKeywords)
	}
	private fun selectKeywordsAdapterCallbackClick(selectedItem: GeneralModel.Keyword, isShow:Boolean){
		keywordsSelectedAdapter?.toggleItem(selectedItem,isShow)
	}
}