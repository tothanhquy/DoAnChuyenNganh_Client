package com.example.tcapp.viewmodel.project
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.GeneralModel
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectNegativeReportsViewModel(private val context : Context):ViewModel(){
	private var _allKeywords:MutableLiveData<ArrayList<GeneralModel.Keyword>> = MutableLiveData<ArrayList<GeneralModel.Keyword>>(ArrayList())
	private var _generalKeywordsOfProject:MutableLiveData<ArrayList<ProjectModels.GeneralNegativeReport>> = MutableLiveData<ArrayList<ProjectModels.GeneralNegativeReport>>(ArrayList())
	private var _myKeywordsOfProject:MutableLiveData<ArrayList<ProjectModels.GeneralNegativeReport>> = MutableLiveData<ArrayList<ProjectModels.GeneralNegativeReport>>(ArrayList())

	public val allKeywords:LiveData<ArrayList<GeneralModel.Keyword>>
		get() = _allKeywords
	public val generalKeywordsOfProject:LiveData<ArrayList<ProjectModels.GeneralNegativeReport>>
		get() = _generalKeywordsOfProject
	public val myKeywordsOfProject:LiveData<ArrayList<ProjectModels.GeneralNegativeReport>>
		get() = _myKeywordsOfProject

	public fun saveKeywords(projectId: String?,keywordsId:ArrayList<String>,okCallback:()->Unit){
		val keywordsIdJson = Gson().toJson(keywordsId)
		_isLoading.postValue(true)
		Thread {
			saveKeywordsAPI(projectId,keywordsIdJson,okCallback)
		}.start()
	}
	
	private fun saveKeywordsAPI(projectId: String?,keywords:String,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/Project/UpdateNegativeReports",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"keywords" to keywords,
						"id" to (projectId?:"")
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
					okCallback()
					_notification.postValue(AlertDialog.Notification("Success!","Report complete."))
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
					url =  API.getBaseUrl() + "/NegativeReportKeyword/GetList",
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
					val parseOj = Gson().fromJson(response.data.toString(), GeneralModel.Keywords::class.java)
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

	public fun getMyNegativeReportKeywordsOfProject(projectId:String?){
		_isLoading.postValue(true)
		Thread {
			getMyNegativeReportKeywordsOfProjectAPI(projectId)
		}.start()
	}

	private fun getMyNegativeReportKeywordsOfProjectAPI(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetMyNegativeReports",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.GeneralNegativeReports::class.java)
					_myKeywordsOfProject.postValue(parseOj.reports)
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

	public fun getGeneralNegativeReportKeywordsOfProject(projectId:String?){
		_isLoading.postValue(true)
		Thread {
			getGeneralNegativeReportKeywordsOfProjectAPI(projectId)
		}.start()
	}

	private fun getGeneralNegativeReportKeywordsOfProjectAPI(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetGeneralNegativeReports",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.GeneralNegativeReports::class.java)
					_generalKeywordsOfProject.postValue(parseOj.reports)
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