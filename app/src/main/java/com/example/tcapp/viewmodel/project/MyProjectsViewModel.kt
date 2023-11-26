package com.example.tcapp.viewmodel.project

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class MyProjectsViewModel (private val context : Context):ViewModel(){
	private var _myProjects:MutableLiveData<ArrayList<ProjectModels.ProjectListItem>> = MutableLiveData<ArrayList<ProjectModels.ProjectListItem>>()
	private var _newProject:MutableLiveData<ProjectModels.ProjectListItem?> = MutableLiveData<ProjectModels.ProjectListItem?>(null)
	private var _invitingRequestNumber:MutableLiveData<Int> = MutableLiveData<Int>(0)

	public val myProjects: LiveData<ArrayList<ProjectModels.ProjectListItem>>
		get() = _myProjects
	public val newProject: LiveData<ProjectModels.ProjectListItem?>
		get() = _newProject
	public val invitingRequestNumber: LiveData<Int>
		get() = _invitingRequestNumber
//	public val reShowCreateNewProject:LiveData<Int>
//		get() = _reShowCreateNewProject
	public var reShowCreateNewProject:Int = 0;

	public fun loadData(){
		_isLoading.postValue(true)
		Thread {
			loadDataAPI()
		}.start()
	}
	private fun loadDataAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetMyProjectsAndInvitingRequest",
					cookies = mapOf("auth" to API.getAuth(context))
				)
			)
			
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					handleResLoadData(response.data)
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
	}
	private fun  handleResLoadData(oj: JSONObject?){
		val parseOj = Gson().fromJson(oj.toString(), ProjectModels.MyProjectsAndRequest::class.java)
		_myProjects.postValue(parseOj.projects);
		_invitingRequestNumber.postValue(parseOj.invitingRequestNumber);
	}
	public fun createNewProject(newProjectName:String?){
		_isLoading.postValue(true)
		Thread {
			createNewProjectAPI(newProjectName)
		}.start()
	}
	
	private fun createNewProjectAPI(newProjectName:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/Create",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"name" to newProjectName
					)
				)
			)
			
			if(response.code==1||response.code==404){
				reShowCreate()
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				reShowCreate()
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Created new team."))
					_newProject.postValue(getProjectFromObject(response.data!!.getJSONObject("newProject")))
				}else{
					reShowCreate()
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			reShowCreate()
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
		
	}
	
	private fun reShowCreate(){
		reShowCreateNewProject += 1
	}
	
	private fun  getProjectFromObject(projectObject: JSONObject):ProjectModels.ProjectListItem{
		return Gson().fromJson(projectObject.toString(), ProjectModels.ProjectListItem::class.java)
	}
}