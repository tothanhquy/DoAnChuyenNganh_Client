package com.example.tcapp.viewmodel.project
import android.content.Context
import android.content.DialogInterface
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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectResourceViewModel (private val context : Context):ViewModel(){
	private var _resources:MutableLiveData<ProjectModels.Resources?> = MutableLiveData<ProjectModels.Resources?>(null)
	private var _newResource:MutableLiveData<ProjectModels.Resource?> = MutableLiveData<ProjectModels.Resource?>(null)

	public val resources: LiveData<ProjectModels.Resources?>
		get() = _resources
	public val newResource: LiveData<ProjectModels.Resource?>
		get() = _newResource

	public fun deleteResource(
		projectId:String?,
		type:String,
		path:String,
		okCallback:(String)->Unit
	){
		_isLoading.postValue(true)
		Thread {
			deleteResourceAPI(
				projectId,
				type,path,
				okCallback
			)
		}.start()
	}
	
	private fun deleteResourceAPI(
		projectId:String?,
		type:String,
		path:String,
		okCallback:(String)->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/DeleteResource",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to projectId,
						"type" to type,
						"path" to path
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
					_notification.postValue(AlertDialog.Notification("Success!","Delete complete.",fun(dia: DialogInterface, i:Int){okCallback(path);}))
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

	public fun loadData(idProject:String?,type:String){
		_isLoading.postValue(true)
		Thread {
			loadResourceAPI(idProject,type)
		}.start()
	}
	private fun loadResourceAPI(idProject:String?,type:String){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetResources",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (idProject?:""),
						"type" to type
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.Resources::class.java)
					_resources.postValue(parseOj)
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

	public fun uploadResource(
		projectId:String?,
		type:String,
		path:String,
		alt:String?
	){
		_isLoading.postValue(true)
		Thread {
			uploadResourceAPI(
				projectId,
				type,path,alt
			)
		}.start()
	}

	private fun uploadResourceAPI(
		projectId:String?,
		type:String,
		path:String,
		alt:String?
	){
		try{
			val fileUpload = File(path)
			val requestBody : RequestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("id",projectId)
				.addFormDataPart("type",type)
				.addFormDataPart("alt",alt)
				.addFormDataPart(
					type , fileUpload.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(fileUpload)) ,
						fileUpload
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
					_notification.postValue(AlertDialog.Notification("Success!","Resource was Uploaded."))
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.Resource::class.java)
					_newResource.postValue(parseOj)
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