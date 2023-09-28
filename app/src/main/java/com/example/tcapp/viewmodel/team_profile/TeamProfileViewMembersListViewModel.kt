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


class TeamProfileViewMembersListViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _membersView:MutableLiveData<TeamProfileModels.TeamProfileMembersList> = MutableLiveData<TeamProfileModels.TeamProfileMembersList>(null)
	private var _memberDeletedId:MutableLiveData<String?> = MutableLiveData<String?>(null)
	
	public val membersView: LiveData<TeamProfileModels.TeamProfileMembersList?>
		get() = _membersView
	public val memberDeletedId: LiveData<String?>
		get() = _memberDeletedId
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	public val userProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
	
	
	public fun loadData(teamId:String?){
		_isLoading.postValue(true)
		Thread {
			loadMembersProfile(teamId)
		}.start()
	}
	private fun loadMembersProfile(teamId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/TeamProfile/GetMembers",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (teamId?:"")
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
					handleResMembers(response.data)
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
	private fun  handleResMembers(oj: JSONObject?){
		if(oj!=null){
			val memberView = TeamProfileModels.TeamProfileMembersList();
			memberView.isLeader=
				!oj.isNull("isLeader")&&oj.getBoolean("isLeader")
			
			val members = ArrayList<TeamProfileModels.Member>();
			if(!oj.isNull("members")){
				val membersObject = oj.getJSONArray("members")
				for (i in 0 until membersObject.length()) {
					val member = membersObject.getJSONObject(i);
					members!!.add(
						TeamProfileModels.Member(
							member.getString("id"),
							member.getString("name"),
							if(!member.isNull("avatar"))member.getString("avatar") else null,
							member.getBoolean("isLeader")
						)
					);
				}
			}
			memberView.members=members
			_membersView.postValue(memberView)
		}
	}
	
	public fun deleteMember(memberId:String?,teamId:String?){
		_isLoading.postValue(true)
		Thread {
			createNewTeamAPI(memberId,teamId)
		}.start()
	}
	
	private fun createNewTeamAPI(memberId:String?,teamId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/TeamProfile/DeleteMember",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_member" to memberId,
						"id_team" to teamId,
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
					_notification.postValue(AlertDialog.Notification("Success!","Created new team."))
					_memberDeletedId.postValue(memberId)
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