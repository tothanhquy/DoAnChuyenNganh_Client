package com.example.tcapp.viewmodel.project

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class ProjectViewMembersNowViewModel (private val context : Context): ViewModel(){
	private var _membersNow:MutableLiveData<ProjectModels.MembersNow?> = MutableLiveData<ProjectModels.MembersNow?>(null)

	public val membersNow: LiveData<ProjectModels.MembersNow?>
		get() = _membersNow

	
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
					url = API.getBaseUrl() + "/Project/GetMembersNow",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.MembersNow::class.java)
					_membersNow.postValue(parseOj);
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
	
	public fun deleteMember(projectId:String?,memberId:String?,okCallback:(String?)->Unit){
		_isLoading.postValue(true)
		Thread {
			deleteMemberAPI(projectId,memberId,okCallback)
		}.start()
	}
	private fun deleteMemberAPI(projectId:String?,memberId:String?,okCallback:(String?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/DeleteMember",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_member" to memberId,
						"id_project" to projectId,
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
					okCallback(memberId)
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

	public fun updateRoleMember(projectId:String?,memberId:String?,role:String?,okCallback:(String?,String?)->Unit){
		_isLoading.postValue(true)
		Thread {
			updateRoleMemberAPI(projectId,memberId,role,okCallback)
		}.start()
	}
	private fun updateRoleMemberAPI(projectId:String?,memberId:String?,role:String?,okCallback:(String?,String?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/UpdateMemberRole",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_member" to memberId,
						"id_project" to projectId,
						"role" to role,
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
					okCallback(memberId,role)
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

	public fun inviteMember(projectId:String?,emailMember:String?,role:String?,okCallback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			inviteMemberAPI(projectId,emailMember,role,okCallback)
		}.start()
	}
	private fun inviteMemberAPI(projectId:String?,emailMember:String?,role:String?,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/InviteNewMember",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_project" to projectId,
						"role" to role,
						"email_new_member" to emailMember,
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
					_notification.postValue(AlertDialog.Notification("Success!","Invite member complete.",fun(dia: DialogInterface , i:Int){okCallback();}))
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