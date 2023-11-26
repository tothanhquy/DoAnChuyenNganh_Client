package com.example.tcapp.viewmodel.project

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class ProjectViewMembersHistoryViewModel (private val context : Context): ViewModel(){
	private var _membersHistory:MutableLiveData<ProjectModels.MembersHistory?> = MutableLiveData<ProjectModels.MembersHistory?>(null)

	public val membersHistory: LiveData<ProjectModels.MembersHistory?>
		get() = _membersHistory

	
	public fun loadMember(projectId:String?){
		_isLoading.postValue(true)
		Thread {
			loadMembersAPI(projectId)
		}.start()
	}
	private fun loadMembersAPI(projectId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetMembersHistory",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (projectId?:"")
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.MembersHistory::class.java)
					_membersHistory.postValue(parseOj);
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