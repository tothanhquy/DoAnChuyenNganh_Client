package com.example.tcapp.viewmodel.project
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectEditInfoViewModel (private val context : Context):ViewModel(){
	private var _projectEditBasicInfo:MutableLiveData<ProjectModels.ProjectEditBasicInfo> = MutableLiveData<ProjectModels.ProjectEditBasicInfo>()
	public val projectEditBasicInfo: LiveData<ProjectModels.ProjectEditBasicInfo>
		get() = _projectEditBasicInfo

	public fun changeInfo(
		projectId:String?,
		projectName:String?,
		projectSlogan:String?,
		projectDescription:String?,
	){
		_isLoading.postValue(true)
		Thread {
			changeInfoAPI(
				projectId,
				projectName,
				projectSlogan,
				projectDescription,
			)
		}.start()
	}
	
	private fun changeInfoAPI(
		projectId:String?,
		projectName:String?,
		projectSlogan:String?,
		projectDescription:String?,
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/EditBasicInfo",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to projectId,
						"name" to projectName,
						"slogan" to projectSlogan,
						"description" to projectDescription
					)
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
					_notification.postValue(AlertDialog.Notification("Success!","Updated info complete."))
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

	public fun loadData(idProject:String?){
		_isLoading.postValue(true)
		Thread {
			loadProject(idProject)
		}.start()
	}
	private fun loadProject(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetEditBasicInfo",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (idProject?:"")
					)
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
					val project = Gson().fromJson(response.data.toString(), ProjectModels.ProjectEditBasicInfo::class.java)
					_projectEditBasicInfo.postValue(project)
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

}