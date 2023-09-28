package com.example.tcapp.view.user_profile

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
import com.example.tcapp.core.Genaral.Static.HorizontalLayoutAutoWrapper
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.view.adapter_view.SelectSkillsRecyclerAdapter
import com.example.tcapp.view.adapter_view.SkillsRecyclerAdapter
import com.example.tcapp.viewmodel.user_profile.MyUserProfileEditSkillsViewModel


class MyUserProfileEditSkillsActivity : CoreActivity() {
	private lateinit var viewModelObject: MyUserProfileEditSkillsViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	
	private var  skillsSelectedAdapter: SkillsRecyclerAdapter? = null;
	private var  allSkillsAdapter: SelectSkillsRecyclerAdapter? = null;
	
	private var skillsSelectedRecyclerView:RecyclerView? = null;
	private var allSkillsRecyclerView:RecyclerView? = null;
	private var searchInput:EditText? = null;
	private var mainContainer:ViewGroup? = null;
	
	private var idSkillsBegin:ArrayList<String> = ArrayList<String>()
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = MyUserProfileEditSkillsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.info)
		setContentView(R.layout.activity_my_user_profile_edit_skills)
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.edit_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.save -> {
				saveSkills()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	private fun saveSkills(){
		viewModelObject.saveSkills(allSkillsAdapter!!.getIdIsCheck())
	}
	
	private fun loadData() {
		viewModelObject.getAllSkills()
	}
	
	private fun setAdapter(allSkills:ArrayList<UserProfileModels.Skill>){
		var skillsStringBegin:String = intent.getStringExtra("skillsId").toString()
		if(skillsStringBegin!=""){
			idSkillsBegin = ArrayList<String>(skillsStringBegin.split("<*>").toTypedArray().asList())
		}
		
		setSelectedSkillsAdapter(allSkills)
		setAllSkillsAdapter(allSkills)
		
		
	}
	private fun setSelectedSkillsAdapter(allSkills:ArrayList<UserProfileModels.Skill>){
		//set selected skills
		val initSelectedSkills:ArrayList<SkillsRecyclerAdapter.Skill> = ArrayList<SkillsRecyclerAdapter.Skill>()
		for (i in 0 until allSkills.size){
			initSelectedSkills.add(SkillsRecyclerAdapter.Skill(
				allSkills[i].id,
				allSkills[i].name,
				allSkills[i].image,
			))
		}
//		skillsSelectedAdapter = SkillsRecyclerAdapter(arrayListOf())
//		val manager = LinearLayoutManager(this)
		
		skillsSelectedAdapter = SkillsRecyclerAdapter(this,initSelectedSkills)
		skillsSelectedRecyclerView!!.setHasFixedSize(false)
		skillsSelectedRecyclerView!!.layoutManager = LinearLayoutManager(this)
//		skillsSelectedRecyclerView!!.layoutManager =
//			Genaral.ResizeableLinearLayoutManager(this)
//		skillsSelectedRecyclerView!!.layoutManager = HorizontalLayoutAutoWrapper(this)
		skillsSelectedRecyclerView!!.adapter = skillsSelectedAdapter
//		skillsSelectedAdapter!!.setInitList(initSelectedSkills)
		skillsSelectedAdapter!!.setIsShowOfItems(idSkillsBegin,true)
	}
	private fun setAllSkillsAdapter(allSkills:ArrayList<UserProfileModels.Skill>){
		
		//set all skills
		val initAllSkills:ArrayList<SelectSkillsRecyclerAdapter.SelectSkill> = ArrayList<SelectSkillsRecyclerAdapter.SelectSkill>()
		for (i in 0 until allSkills.size){
			initAllSkills.add(SelectSkillsRecyclerAdapter.SelectSkill(
				allSkills[i].id,
				allSkills[i].name,
				allSkills[i].image,
			))
		}

//		allSkillsAdapter = SelectSkillsRecyclerAdapter(arrayListOf() ,::selectSkillsAdapterCallbackClick)
		allSkillsAdapter = SelectSkillsRecyclerAdapter(this,initAllSkills ,::selectSkillsAdapterCallbackClick)
		allSkillsRecyclerView!!.setHasFixedSize(false)
		allSkillsRecyclerView!!.layoutManager = LinearLayoutManager(this)
		
		allSkillsRecyclerView!!.adapter = allSkillsAdapter
//		allSkillsAdapter!!.setInitList(initAllSkills)
		allSkillsAdapter!!.setIsCheckOfItems(idSkillsBegin,true)
	}
	private fun selectSkillsAdapterCallbackClick(id:String,position:Int,isCheck:Boolean){
		skillsSelectedAdapter?.toggleItemIsShow(id)
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myUserProfileEditSkillsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
		skillsSelectedRecyclerView = findViewById(R.id.myUserProfileEditSkillsActivitySkillsSelected)
		allSkillsRecyclerView = findViewById(R.id.myUserProfileEditSkillsActivityAllSkills)
		searchInput = findViewById(R.id.myUserProfileEditSkillsActivitySearchInput)
		mainContainer = findViewById(R.id.myUserProfileEditSkillsMainContainer)
		
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
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//set alert notification
		viewModelObject.allSkills.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setAdapter(it)
				}
			}
		})
	}
	fun search(view:View){
		allSkillsAdapter?.filterByName(searchInput!!.text.toString())
	}
	fun toggleShowSelectedSkillsContainer(view:View){
		skillsSelectedAdapter!!.toggleIsContainerShow()
	}
}