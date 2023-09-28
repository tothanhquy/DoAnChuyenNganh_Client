package com.example.tcapp.viewmodel.team_profile

import android.content.Context
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


class MyTeamsViewModel (private val context : Context){
//	private var _avatar:MutableLiveData<String> = MutableLiveData<String>("");
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _myTeams:MutableLiveData<ArrayList<TeamProfileModels.MyTeamItem>> = MutableLiveData<ArrayList<TeamProfileModels.MyTeamItem>>()
	private var _newTeam:MutableLiveData<TeamProfileModels.MyTeamItem?> = MutableLiveData<TeamProfileModels.MyTeamItem?>(null)
//	private var _reShowCreateNewTeam:MutableLiveData<Int> = MutableLiveData<Int>(0)
	
	public val myTeams: LiveData<ArrayList<TeamProfileModels.MyTeamItem>>
		get() = _myTeams
	public val newTeam: LiveData<TeamProfileModels.MyTeamItem?>
		get() = _newTeam
//	public val reShowCreateNewTeam:LiveData<Int>
//		get() = _reShowCreateNewTeam
	public var reShowCreateNewTeam:Int = 0;
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	public val teamProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/teams_avatar/"
//	public val userProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
//	public val skillImagesPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
	
	
	
	public fun loadData(){
		_isLoading.postValue(true)
		Thread {
			loadUserProfile()
		}.start()
	}
	private fun loadUserProfile(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/TeamProfile/GetMyTeams",
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
		val teams = ArrayList<TeamProfileModels.MyTeamItem>();
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
	public fun createNewTeam(newTeamName:String?){
		_isLoading.postValue(true)
		Thread {
			createNewTeamAPI(newTeamName)
		}.start()
	}
	
	private fun createNewTeamAPI(newTeamName:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/TeamProfile/Create",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"name" to newTeamName
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
					_newTeam.postValue(getTeamFromObject(response.data!!.getJSONObject("newTeam")))
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
		reShowCreateNewTeam += 1
	}
	
	private fun  getTeamFromObject(teamObject: JSONObject):TeamProfileModels.MyTeamItem{
		return TeamProfileModels.MyTeamItem(
			teamObject.getString("teamName"),
			teamObject.getString("teamId"),
			if(!teamObject.isNull("teamAvatar"))teamObject.getString("teamAvatar") else null,
			teamObject.getString("leaderName"),
			teamObject.getString("leaderId"),
			if(!teamObject.isNull("leaderAvatar"))teamObject.getString("leaderAvatar") else null,
			teamObject.getInt("memberNumber"),
			teamObject.getBoolean("isLeader")
		)
	}
}