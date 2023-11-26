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
import com.example.tcapp.viewmodel.project.ProjectEditInfoViewModel


class ProjectEditInfoActivity : CoreActivity() {
	private lateinit var viewModelObject: ProjectEditInfoViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var projectId:String?=null;
	
	private var projectName:EditText? =null;
	private var projectSlogan:EditText? =null;
	private var projectDescription:EditText? =null;
	
	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = ProjectEditInfoViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.edit_info)
		setContentView(R.layout.activity_project_edit_info)
		
		projectName = findViewById(R.id.projectEditInfoActivityName)
		projectSlogan = findViewById(R.id.projectEditInfoActivitySlogan)
		projectDescription=findViewById(R.id.projectEditInfoActivityDescription)
		
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
				changeInfo()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	private fun changeInfo(){
		viewModelObject.changeInfo(
			projectId,
			projectName!!.text.toString(),
			projectSlogan!!.text.toString(),
			projectDescription!!.text.toString()
		)
	}
	private fun setInfo(info:ProjectModels.ProjectEditBasicInfo){
		projectName!!.setText(info.name)
		projectSlogan!!.setText(info.slogan)
		projectDescription!!.setText(info.description)
	}
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString();
		viewModelObject.loadData(projectId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectEditInfoActivity)
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
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})

		viewModelObject.projectEditBasicInfo.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setInfo(it);
				}
			}
		})
	}
	
}