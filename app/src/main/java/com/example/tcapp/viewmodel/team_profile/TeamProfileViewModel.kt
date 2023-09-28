package com.example.tcapp.viewmodel.team_profile

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


class TeamProfileViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _teamProfile:MutableLiveData<TeamProfileModels.TeamProfile> = MutableLiveData<TeamProfileModels.TeamProfile>()
	private var _teamAvatar:MutableLiveData<String?> = MutableLiveData<String?>(null);
//	private var _fullMembers:MutableLiveData<ArrayList<TeamProfileModels.Member>?> = MutableLiveData<ArrayList<TeamProfileModels.Member>?>(null)
	
	public val teamProfile: LiveData<TeamProfileModels.TeamProfile>
		get() = _teamProfile
	public val teamAvatar:LiveData<String?>
		get() = _teamAvatar
//	public val fullMembers: LiveData<ArrayList<TeamProfileModels.Member>?>
//		get() = _fullMembers
	public var fullMembers: ArrayList<TeamProfileModels.Member>? = null;
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	public val userProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
//	public val teamProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/teams_avatar/"
//	public val skillImagesPath:String = API.getBaseUrl()+"/static/images/skills/"
	
	
	public fun loadData(idTeam:String?){
		_isLoading.postValue(true)
		Thread {
			loadTeamProfile(idTeam)
		}.start()
	}
	private fun loadTeamProfile(idTeam:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/TeamProfile/DetailsTeam",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (idTeam?:"")
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
					viewProfile(response.data)
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
	private fun  viewProfile(oj: JSONObject?){
		if(oj!=null){
			var profile = TeamProfileModels.TeamProfile();
			profile.teamId = if(!oj.isNull("teamId"))oj.getString("teamId") else null;
			profile.teamName = if(!oj.isNull("teamName"))oj.getString("teamName") else null;
			profile.teamAvatar = if(!oj.isNull("teamAvatar"))oj.getString("teamAvatar") else null;
			_teamAvatar.postValue(profile.teamAvatar)
			profile.leaderId = if(!oj.isNull("leaderId"))oj.getString("leaderId") else null;
			profile.leaderName = if(!oj.isNull("leaderName"))oj.getString("leaderName") else null;
			profile.leaderAvatar = if(!oj.isNull("leaderAvatar"))oj.getString("leaderAvatar") else null;
			
			profile.maxim = if(!oj.isNull("maxim"))oj.getString("maxim") else null;
			profile.description = if(!oj.isNull("description"))oj.getString("description") else null;
			profile.internalInfo = if(!oj.isNull("internalInfo"))oj.getString("internalInfo") else null;
			profile.relationship = if(!oj.isNull("relationship"))oj.getString("relationship") else null;
			
			profile.skills = ArrayList<TeamProfileModels.TeamProfileSkills>();
			if(!oj.isNull("skills")){
				val skillsOj = oj.getJSONArray("skills")
				for (i in 0 until skillsOj.length()) {
					val skill = skillsOj.getJSONObject(i);
					profile.skills!!.add(TeamProfileModels.TeamProfileSkills(
						skill.getString("id"),
						skill.getString("name"),
						if(!skill.isNull("image"))skill.getString("image") else null,
						skill.getInt("number")
					))
				}
			}
			
			profile.members = ArrayList<TeamProfileModels.TeamProfileMembers>();
			if(!oj.isNull("members")){
				val membersOj = oj.getJSONArray("members")
				val length = membersOj.length();
				var lengthFor = TeamProfileModels.MAX_LENGTH_OF_MEMBERS_CONTAINER_VIEW;
				if(length < TeamProfileModels.MAX_LENGTH_OF_MEMBERS_CONTAINER_VIEW)lengthFor = length;
				for (i in 0 until lengthFor) {
					val member = membersOj.getJSONObject(i);
					profile.members!!.add(TeamProfileModels.TeamProfileMembers(
						member.getString("name"),
						if(!member.isNull("avatar"))member.getString("avatar") else null,
					))
				}
				if(lengthFor!=length)
					profile.members!!.add(TeamProfileModels.TeamProfileMembers(
						length - lengthFor
					))
				
				//create full members
				if(profile.relationship!=null&& profile.relationship !!.contains("leader")){
					fullMembers = ArrayList<TeamProfileModels.Member>();
					for (i in 0 until length) {
						val member = membersOj.getJSONObject(i);
						if(!member.isNull("isLeader") && member.getBoolean("isLeader"))continue;
						fullMembers!!.add(TeamProfileModels.Member(
							if(!member.isNull("id"))member.getString("id") else null,
							if(!member.isNull("name"))member.getString("name") else null,
							if(!member.isNull("avatar"))member.getString("avatar") else null,
							if(!member.isNull("isLeader"))member.getBoolean("isLeader") else false,
						))
					}
				}
				
			}
			_teamProfile.postValue(profile)
		}
	}
	public fun changeAvatar(imagePath:String?,teamId:String?){
		if(imagePath!=null){
			_isLoading.postValue(true)
			Thread {
				changeAvatarAPI(imagePath!!,teamId)
			}.start()
		}
	}

	private fun changeAvatarAPI(imagePath:String,teamId:String?){
		try{
			val avatarImage = File(imagePath)
			val requestBody : RequestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("id",teamId)
				.addFormDataPart(
					"avatar" , avatarImage.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(avatarImage)) ,
						avatarImage
					)
				)
				.build()
			val response = API.requestPost(context , "/TeamProfile/EditAvatar",requestBody)

			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Avatar was Updated."))
					_teamAvatar.postValue(
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
	
	public fun exitTeam(
		teamId:String?,
		newLeaderId:String?,
		okCallback:()->Unit
	){
		_isLoading.postValue(true)
		Thread {
			exitTeamAPI(
				teamId,
				newLeaderId,
				okCallback
			)
		}.start()
	}
	
	private fun exitTeamAPI(
		teamId:String?,
		newLeaderId:String?,
		okCallback:()->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/TeamProfile/ExitTeam",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_team" to teamId,
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
					_notification.postValue(AlertDialog.Notification("Success!","Exit Team complete.",fun(dia: DialogInterface , i:Int){okCallback();}))
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