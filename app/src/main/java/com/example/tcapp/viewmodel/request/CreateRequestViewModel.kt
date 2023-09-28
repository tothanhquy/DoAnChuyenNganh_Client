package com.example.tcapp.viewmodel.request

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class CreateRequestViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _myTeams:MutableLiveData<ArrayList<TeamProfileModels.TeamsOfLeaderItem>> = MutableLiveData<ArrayList<TeamProfileModels.TeamsOfLeaderItem>>()
	
	public val myTeams: LiveData<ArrayList<TeamProfileModels.TeamsOfLeaderItem>>
		get() = _myTeams
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadTeamsOfLeader(){
		_isLoading.postValue(true)
		Thread {
			loadTeamsOfLeaderProfile()
		}.start()
	}
	private fun loadTeamsOfLeaderProfile(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/TeamProfile/GetTeamsByLeader",
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
					handleResTeams(response.data)
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
	private fun  handleResTeams(oj: JSONObject?){
		val teams = ArrayList<TeamProfileModels.TeamsOfLeaderItem>();
		if(oj!=null){
			if(!oj.isNull("teams")){
				val teamsObject = oj.getJSONArray("teams")
				for (i in 0 until teamsObject.length()) {
					val team = teamsObject.getJSONObject(i);
					teams!!.add(getTeamFromObject(team));
				}
			}
		}
		_myTeams.postValue(teams);
	}
	public fun createRequest(
		creator:String,
		teamId:String?,
		userId:String?,
		title:String?,
		content:String?,
		callback:()->Unit
	){
		if(teamId==null||teamId==""){
			_notification.postValue(AlertDialog.Notification("Less data!","No team was selected."))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			createRequestAPI(
				creator,
				teamId,
				userId,
				title,
				content,
				callback
			)
		}.start()
	}
	
	private fun createRequestAPI(
		creator:String,
		teamId:String,
		userId:String?,
		title:String?,
		content:String?,
		callback:()->Unit
	){
		try{
			var dataMap:MutableMap<String, String?> = mutableMapOf(
				"creator" to creator,
				"team_id" to teamId,
				"title" to title,
				"content" to content,
			);
			if(creator=="leader"){
				dataMap["user_id"] = userId;
			}
			
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Request/Create",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = dataMap
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
					_notification.postValue(AlertDialog.Notification("Success!","Created new request.",fun(dia: DialogInterface , i:Int){callback();}))
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
	
	
	private fun  getTeamFromObject(teamObject: JSONObject):TeamProfileModels.TeamsOfLeaderItem{
		return TeamProfileModels.TeamsOfLeaderItem(
			teamObject.getString("teamId"),
			teamObject.getString("teamName"),
			if(!teamObject.isNull("teamAvatar"))teamObject.getString("teamAvatar") else null,
		)
	}
}