package com.example.tcapp.view.project

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.view.adapter_view.ProjectResourceRecyclerAdapter
import com.example.tcapp.viewmodel.project.ProjectResourceViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton



class ProjectResourcesActivity : CoreActivity() {
	private val CHOOSE_AVATAR_REQUEST_CODE = 113

	private lateinit var objectViewModel: ProjectResourceViewModel;
	
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var resourcesContainer:RecyclerView?= null;
	private var resourcesContainerAdapter: ProjectResourceRecyclerAdapter?= null;

	private var uploadInputAlt: EditText?= null;
	private var uploadPathView: TextView?= null;

	private var projectId:String? = null;
	private var resourceType:String = "";
	private var selectedFilePath:String? = "";

	private var resourceFocusPath:String? = null;
	private var resourceFocusAlt:String? = null;

	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = ProjectResourceViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.resources)
		setContentView(R.layout.activity_project_resource)

		uploadInputAlt =  findViewById(R.id.projectResourceActivityUploadContainerAlt);
		uploadPathView =  findViewById(R.id.projectResourceActivityUploadContainerPath);

		resourcesContainer =  findViewById(R.id.projectResourceActivityRecyclerView);
		resourcesContainer!!.setHasFixedSize(true)
		resourcesContainer!!.layoutManager = LinearLayoutManager(this)

		initViews()
		setRender()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString();
		resourceType = intent.getStringExtra("type").toString();


		resourcesContainerAdapter = ProjectResourceRecyclerAdapter(applicationContext,
			this,arrayListOf()
		)
		resourcesContainerAdapter!!.setType(resourceType);
		resourcesContainerAdapter!!.setCallbackOfOpenOptions(::openOptions);
		resourcesContainer!!.adapter = resourcesContainerAdapter;

		objectViewModel.loadData(projectId,resourceType)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectResourceActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
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

		objectViewModel.resources.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					resourcesContainerAdapter!!.setActionAble(it.isLeader)
					setOptions(it.isLeader)
					setResourcesContainer(it.resources)
				}
			}
		})
		objectViewModel.newResource.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					resourcesContainer!!.setHasFixedSize(true)
					resourcesContainerAdapter!!.add(it)
					closeUploadContainer(View(applicationContext))
				}
			}
		})
	}

	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == RESULT_OK) {
			val selectedUri : Uri = data!!.data
				?: return //The uri with the location of the file
//
			val selectedPath = Genaral.URIPathHelper().getPath(applicationContext, selectedUri!!)
			uploadSelectedFilePath(selectedPath)
		}
	}

	private fun setResourcesContainer(resources: ArrayList<ProjectModels.Resource>?){
		if(resources!=null){
			resourcesContainer?.setHasFixedSize(true)
			resourcesContainerAdapter?.setInitList(resources)
		}
	}
	
	private fun setOptions(isLeader:Boolean){
		if(isLeader){
			findViewById<FloatingActionButton>(R.id.projectResourceActivityPlusButton).visibility = View.VISIBLE
		}else{
			findViewById<FloatingActionButton>(R.id.projectResourceActivityPlusButton).visibility = View.GONE
		}
	}
	private fun openOptions(path:String? , alt:String?){
		resourceFocusPath = path;
		resourceFocusAlt = alt;
		findViewById<LinearLayout>(R.id.projectResourceActivityOptions).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		findViewById<LinearLayout>(R.id.projectResourceActivityOptions).visibility = View.GONE;
	}
	fun deleteResource(view:View){
		this.showAskDialog(
			"Important!",
			"Do you really want to remove '$resourceFocusAlt' out you project.",
			fun(dialogInterface:DialogInterface, i:Int){
				objectViewModel.deleteResource(projectId,resourceType,resourceFocusPath!!,::deleteResourceOkCallback)
			}
		)
	}
	private fun deleteResourceOkCallback(path:String?){
		runOnUiThread {
			resourcesContainer!!.setHasFixedSize(true)
			resourcesContainerAdapter?.deleteItem(path)
			closeOptions(View(applicationContext))
		}
	}

	fun closeUploadContainer(view:View){
		findViewById<LinearLayout>(R.id.projectResourceActivityUploadContainer).visibility = View.GONE;
	}
	fun openUploadContainer(view:View){
		findViewById<LinearLayout>(R.id.projectResourceActivityUploadContainer).visibility = View.VISIBLE;
		uploadSelectedFilePath("")
		uploadInputAlt!!.setText("")
	}

	private fun uploadSelectedFilePath(path:String?){
		selectedFilePath=path;
		uploadPathView?.text = "Path: $path"
	}
	fun uploadFile(view:View){
		if(resourceType=="video"){
			if(checkPermissionsReadFile()){
				var intent:Intent =  Intent()
					.setType("video/*")
					.setAction(Intent.ACTION_GET_CONTENT)
					.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(Intent.createChooser(intent, "Select video"), CHOOSE_AVATAR_REQUEST_CODE);
			}else{
				requestPermissionsReadFile()
			}
		}else{
			if(checkPermissionsReadFile()){
				var intent:Intent =  Intent()
					.setType("image/*")
					.setAction(Intent.ACTION_GET_CONTENT)
					.addCategory(Intent.CATEGORY_OPENABLE);
				startActivityForResult(Intent.createChooser(intent, "Select image"), CHOOSE_AVATAR_REQUEST_CODE);
			}else{
				requestPermissionsReadFile()
			}
		}
	}

	fun uploadResource(view:View){
		if(selectedFilePath!=null&& selectedFilePath!!.isNotEmpty()){
			val alt = uploadInputAlt!!.text.toString()
			objectViewModel.uploadResource(projectId,resourceType, selectedFilePath!!,alt)
		}else{
			super.showNotificationDialog("Chưa chọn file","Hãy chọn file",null)
		}
	}
}