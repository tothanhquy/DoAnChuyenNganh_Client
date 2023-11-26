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

class ProjectEditCategoryKeywordsViewModel(private val context : Context):ViewModel(){
	private var _allKeywords:MutableLiveData<ArrayList<ProjectModels.CategoryKeyword>> = MutableLiveData<ArrayList<ProjectModels.CategoryKeyword>>(ArrayList())
	private var _keywordsOfProject:MutableLiveData<ArrayList<ProjectModels.CategoryKeyword>> = MutableLiveData<ArrayList<ProjectModels.CategoryKeyword>>(ArrayList())

	public val allKeywords:LiveData<ArrayList<ProjectModels.CategoryKeyword>>
		get() = _allKeywords
	public val keywordsOfProject:LiveData<ArrayList<ProjectModels.CategoryKeyword>>
		get() = _keywordsOfProject

	public fun saveKeywords(projectId:String?,keywordsId:ArrayList<String>){
		val keywordsIdJson = Gson().toJson(keywordsId)
		_isLoading.postValue(true)
		Thread {
			saveKeywordsAPI(projectId,keywordsIdJson)
		}.start()
	}
	
	private fun saveKeywordsAPI(projectId:String?,keywords:String){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/Project/UpdateCategoryKeywordsOfProject",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"keywords" to keywords,
						"id" to (projectId?:""),
					),
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
					_notification.postValue(AlertDialog.Notification("Success!","Save Keywords complete."))
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
	
	public fun getAllKeywords(){
		_isLoading.postValue(true)
		Thread {
			getAllKeywordsAPI()
		}.start()
	}
	
	private fun getAllKeywordsAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url =  API.getBaseUrl() + "/CategoryKeyword/GetList",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.CategoryKeywords::class.java)
					_allKeywords.postValue(parseOj.keywords)
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

	public fun getKeywordsOfProject(projectId:String?){
		_isLoading.postValue(true)
		Thread {
			getKeywordsOfProjectAPI(projectId)
		}.start()
	}

	private fun getKeywordsOfProjectAPI(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetCategoryKeywordsOfProject",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.CategoryKeywords::class.java)
					_keywordsOfProject.postValue(parseOj.keywords)
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