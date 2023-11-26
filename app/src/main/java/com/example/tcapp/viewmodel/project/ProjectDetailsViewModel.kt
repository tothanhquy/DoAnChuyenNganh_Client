package com.example.tcapp.viewmodel.project

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.notification.NotificationModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ProjectDetailsViewModel(private val context : Context): ViewModel() {
	private var _projectDetails:MutableLiveData<ProjectModels.ProjectDetails> = MutableLiveData<ProjectModels.ProjectDetails>()
	private var _projectAvatar:MutableLiveData<String?> = MutableLiveData<String?>(null);
//	private var _fullMembers:MutableLiveData<ArrayList<ProjectModels.Member>?> = MutableLiveData<ArrayList<ProjectModels.Member>?>(null)
	
	public val projectDetails: LiveData<ProjectModels.ProjectDetails>
		get() = _projectDetails
	public val projectAvatar:LiveData<String?>
		get() = _projectAvatar

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
					url = API.getBaseUrl() + "/Project/Details",
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
					val project = Gson().fromJson(response.data.toString(), ProjectModels.ProjectDetails::class.java)
					_projectDetails.postValue(project)
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
	public fun changeAvatar(imagePath:String?,projectId:String?){
		if(imagePath!=null){
			_isLoading.postValue(true)
			Thread {
				changeAvatarAPI(imagePath!!,projectId)
			}.start()
		}
	}

	private fun changeAvatarAPI(imagePath:String,projectId:String?){
		try{
			val avatarImage = File(imagePath)
			val requestBody : RequestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("id",projectId)
				.addFormDataPart(
					"avatar" , avatarImage.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(avatarImage)) ,
						avatarImage
					)
				)
				.build()
			val response = API.requestPost(context , "/Project/EditAvatar",requestBody)

			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Avatar was Updated."))
					_projectAvatar.postValue(
						if(!response.data!!.isNull("new_avatar"))
							response.data!!.getString("new_avatar")
						else null
					)
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
	
	public fun exitProject(
		projectId:String?,
		newLeaderId:String?,
		okCallback:()->Unit
	){
		_isLoading.postValue(true)
		Thread {
			exitProjectAPI(
				projectId,
				newLeaderId,
				okCallback
			)
		}.start()
	}
	
	private fun exitProjectAPI(
		projectId:String?,
		newLeaderId:String?,
		okCallback:()->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/ExitProject",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_project" to projectId,
						"id_new_leader" to newLeaderId,
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
					_notification.postValue(AlertDialog.Notification("Success!","Exit Project complete.",fun(dia: DialogInterface , i:Int){okCallback();}))
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

	public fun toggleFollow(
		projectId:String?,
		okCallback:(Int,Boolean)->Unit
	){
		Thread {
			toggleFollowAPI(
				projectId,
				okCallback
			)
		}.start()
	}

	private fun toggleFollowAPI(
		projectId:String?,
		okCallback:(Int,Boolean)->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/ToggleFollow",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to projectId,
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
					okCallback(response.data!!.getInt("totalFollows"),response.data!!.getBoolean("isFollow"));
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
	}
}