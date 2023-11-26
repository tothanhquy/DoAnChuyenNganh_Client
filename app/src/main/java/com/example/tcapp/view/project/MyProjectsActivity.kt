package com.example.tcapp.view.project

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.MyProjectsRecyclerAdapter
import com.example.tcapp.viewmodel.project.MyProjectsViewModel

class MyProjectsActivity : CoreActivity() {
	private lateinit var objectViewModel: MyProjectsViewModel;
	
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var myProjectsContainer:RecyclerView?= null;
	private var invitingRequestsNumber:TextView?= null;
	private var myProjectsContainerAdapter: MyProjectsRecyclerAdapter?= null;
	
	private var newProjectNameBefore:String? = null;
	
	private var reShowCreateNewProject:Int =0;

	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = MyProjectsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.my_projects)
		setContentView(R.layout.activity_my_teams)
		
		myProjectsContainer =  findViewById<RecyclerView>(R.id.myProjectsActivityRecyclerView);
		invitingRequestsNumber =  findViewById(R.id.myProjectsActivityInvitingRequestsNumber);

		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		objectViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myProjectsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
//	@SuppressLint("SuspiciousIndentation")
	private fun setRender(){
		//set alert error
		objectViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,fun(dialogInterface: DialogInterface , i:Int){
						if(reShowCreateNewProject!=objectViewModel.reShowCreateNewProject){
							reShowCreateNewProject=objectViewModel.reShowCreateNewProject;
							showDialogCreateNewProject(newProjectNameBefore?:"")
						}
					})
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
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//set avatar
		objectViewModel.newProject.observe(this, Observer {
			runOnUiThread {
				if(it!=null)
				pushNewProject(it)
			}
		})
		//set user data
		objectViewModel.myProjects.observe(this, Observer {
			runOnUiThread {
				setProjectsContainer(it)
			}
		})
		objectViewModel.invitingRequestNumber.observe(this, Observer {
			runOnUiThread {
				if(it==0){
					invitingRequestsNumber!!.visibility=View.GONE
				}else{
					invitingRequestsNumber!!.visibility=View.VISIBLE
					invitingRequestsNumber!!.text=it.toString()+if(it==1)" New Request" else " New Requests";
				}
			}
		})
	}
	private fun pushNewProject(newProject: ProjectModels.ProjectListItem){
		try{
			myProjectsContainerAdapter?.add(newProject)
		}catch(e:Exception){}
	}
	private fun setProjectsContainer(projects: ArrayList<ProjectModels.ProjectListItem>){
		myProjectsContainerAdapter = MyProjectsRecyclerAdapter(applicationContext,projects)
		myProjectsContainerAdapter!!.setCallback(::openProject);
		myProjectsContainer!!.setHasFixedSize(true)
		myProjectsContainer!!.layoutManager = LinearLayoutManager(this)
		myProjectsContainer!!.adapter = myProjectsContainerAdapter;
	}
	
	private fun openProject(idProject:String){
		val intent = Intent(applicationContext ,ProjectDetailsActivity::class.java)
		intent.putExtra("projectId", idProject)
		startActivity(intent)
	}
	fun createNewProjectClick(view:View){
		showDialogCreateNewProject("new project")
	}
	fun openInvitingRequests(view:View){
		val intent = Intent(applicationContext , ProjectInvitingMembersActivity::class.java)
		intent.putExtra("viewer", "user");
		startActivity(intent);
	}
	private fun showDialogCreateNewProject(inputDefault:String){
		val dialog = createGetStringDialog(this@MyProjectsActivity,"New project name",inputDefault,::createNewProject)
		dialog.show();
	}
	private fun createNewProject(name:String?):Boolean{
		newProjectNameBefore = name;
		objectViewModel.createNewProject(name);
		return true;
	}
}