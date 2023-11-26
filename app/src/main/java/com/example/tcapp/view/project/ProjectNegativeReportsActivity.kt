package com.example.tcapp.view.project

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.GeneralModel
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.ProjectNegativeReportGeneralKeywordsRecyclerAdapter
import com.example.tcapp.view.adapter_view.SelectKeywordsRecyclerAdapter
import com.example.tcapp.view.adapter_view.ShowSelectedKeywordsRecyclerAdapter
import com.example.tcapp.viewmodel.project.ProjectNegativeReportsViewModel


class ProjectNegativeReportsActivity : CoreActivity() {
	private lateinit var viewModelObject: ProjectNegativeReportsViewModel;
	private var backgroundColor:Int =0
	private var loadingLayout:View? = null;
	
	private var projectId:String? = null;

	private var allNegativeReportKeywords:ArrayList<GeneralModel.Keyword>?=null;

	private var keywordsSelectedAdapter: ShowSelectedKeywordsRecyclerAdapter? = null;
	private var allKeywordsAdapter: SelectKeywordsRecyclerAdapter? = null;
	private var generalKeywordsOfProjectAdapter: ProjectNegativeReportGeneralKeywordsRecyclerAdapter? = null;
	
	private var keywordsSelectedRecyclerView:RecyclerView? = null;
	private var allKeywordsRecyclerView:RecyclerView? = null;
	private var generalKeywordsOfProjectRecyclerView:RecyclerView? = null;
	
	private var searchInput:EditText? = null;
	private var createContainer:LinearLayout? = null;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = ProjectNegativeReportsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.reports)
		setContentView(R.layout.activity_project_edit_category_keywords)

		initViews()

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

		generalKeywordsOfProjectAdapter = ProjectNegativeReportGeneralKeywordsRecyclerAdapter(this, arrayListOf())
		generalKeywordsOfProjectRecyclerView!!.setHasFixedSize(true)
		generalKeywordsOfProjectRecyclerView!!.layoutManager = LinearLayoutManager(this)
		generalKeywordsOfProjectRecyclerView!!.adapter = generalKeywordsOfProjectAdapter

		setRender()
		loadData()
	}
	
	public fun updateReport(view: View){
		viewModelObject.saveKeywords(projectId,allKeywordsAdapter!!.getIdIsCheck())
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString()
		viewModelObject.getAllKeywords()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectReportsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		keywordsSelectedRecyclerView = findViewById(R.id.projectReportsActivityKeywordsSelectedRecyclerView)
		allKeywordsRecyclerView = findViewById(R.id.projectReportsActivityAllKeywordsRecyclerView)
		generalKeywordsOfProjectRecyclerView = findViewById(R.id.projectReportsActivityGeneralKeywordsRecyclerView)
		searchInput = findViewById(R.id.projectReportsActivitySearchInput)
		createContainer = findViewById(R.id.projectReportsActivityCreateContainer)
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
		viewModelObject.myKeywordsOfProject.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setShowSelectedKeywordsAdapter(it)
				}
			}
		})
		viewModelObject.generalKeywordsOfProject.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setGeneralKeywordsOfProjectAdapter(it)
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
	private fun setShowSelectedKeywordsAdapter(myKeywords:ArrayList<ProjectModels.GeneralNegativeKeyword>){
		//set selected keywords
		val myKeywordsId = ArrayList<String?>(myKeywords.map{it.id})
		val subMyKeywords = ArrayList(allNegativeReportKeywords!!.filter { myKeywordsId.indexOf(it.id)!=-1 })

		keywordsSelectedRecyclerView!!.setHasFixedSize(false)
		keywordsSelectedAdapter!!.setInitList(subMyKeywords)
	}
	private fun setGeneralKeywordsOfProjectAdapter(myKeywords:ArrayList<ProjectModels.GeneralNegativeKeyword>){
		val myKeywordsId = ArrayList<String?>(myKeywords.map{it.id})
		val subMyKeywords = ArrayList(allNegativeReportKeywords!!.filter{ myKeywordsId.indexOf(it.id)!=-1 }.map{ProjectNegativeReportGeneralKeywordsRecyclerAdapter.GeneralNegativeKeyword(it.id,it.name,0)});
		for(i in 0 until subMyKeywords.size){
			val ind = myKeywords.indexOfFirst { it.id == subMyKeywords[i].id };
			if(ind!=-1){
				subMyKeywords[i].number = myKeywords[ind].number
			}
		}
		generalKeywordsOfProjectRecyclerView!!.setHasFixedSize(true)
		generalKeywordsOfProjectAdapter!!.setInitList(subMyKeywords)
	}
	private fun setAllKeywordsAdapter(keywords:ArrayList<GeneralModel.Keyword>){
		allKeywordsRecyclerView!!.setHasFixedSize(false)
		allKeywordsAdapter!!.setInitList(keywords)
		allNegativeReportKeywords = keywords

		viewModelObject.getGeneralNegativeReportKeywordsOfProject(projectId)
		viewModelObject.getMyNegativeReportKeywordsOfProject(projectId)
	}
	private fun selectKeywordsAdapterCallbackClick(selectedItem: GeneralModel.Keyword, isShow:Boolean){
		keywordsSelectedAdapter?.toggleItem(selectedItem,isShow)
	}

	public fun closeCreateContainer(view: View){
		createContainer?.visibility = View.GONE
	}
	public fun openCreateContainer(view: View){
		createContainer?.visibility = View.VISIBLE
	}

}