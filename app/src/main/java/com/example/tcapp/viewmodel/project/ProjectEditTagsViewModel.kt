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
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectEditTagsViewModel (private val context : Context):ViewModel(){
	private var _tags:MutableLiveData<ArrayList<String?>?> = MutableLiveData<ArrayList<String?>?>(null)
	public val tags: LiveData<ArrayList<String?>?>
		get() = _tags

	public fun changeTags(
		projectId:String?,
		tags:ArrayList<String?>,
		okCallback:(ArrayList<String?>?)->Unit
	){
		_isLoading.postValue(true)
		val tagsJson = Gson().toJson(tags)
		Thread {
			changeTagsAPI(
				projectId,
				tagsJson,
				okCallback
			)
		}.start()
	}
	
	private fun changeTagsAPI(
		projectId:String?,
		tags:String?,
		okCallback:(ArrayList<String?>?)->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/EditTags",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to projectId,
						"tags" to tags
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.Tags::class.java)
					_notification.postValue(AlertDialog.Notification("Success!","Edit tags complete.",fun(dia: DialogInterface, i:Int){okCallback(parseOj.tags);}))
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
			loadTagsAPI(idProject)
		}.start()
	}
	private fun loadTagsAPI(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetTags",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.Tags::class.java)
					_tags.postValue(parseOj.tags)
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