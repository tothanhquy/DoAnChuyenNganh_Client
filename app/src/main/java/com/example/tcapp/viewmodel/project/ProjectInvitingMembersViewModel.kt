package com.example.tcapp.viewmodel.project

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class ProjectInvitingMembersViewModel (private val context : Context):ViewModel(){
	private var _invitingProjects:MutableLiveData<ArrayList<ProjectModels.InvitingProject>> = MutableLiveData<ArrayList<ProjectModels.InvitingProject>>()
	private var _invitingMembers:MutableLiveData<ArrayList<ProjectModels.InvitingMember>> = MutableLiveData<ArrayList<ProjectModels.InvitingMember>>()

	public val invitingProjects: LiveData<ArrayList<ProjectModels.InvitingProject>>
		get() = _invitingProjects
	public val invitingMembers: LiveData<ArrayList<ProjectModels.InvitingMember>>
		get() = _invitingMembers


	public fun loadInvitingMembers(idProject:String?){
		_isLoading.postValue(true)
		Thread {
			loadInvitingMembersAPI(idProject)
		}.start()
	}
	private fun loadInvitingMembersAPI(idProject:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetInvitingMembersOfProject",
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
					val resOj = Gson().fromJson(response.data.toString(), ProjectModels.InvitingMembers::class.java)
					_invitingMembers.postValue(resOj.invitingMembers);
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
	public fun loadInvitingProjects(){
		_isLoading.postValue(true)
		Thread {
			loadInvitingProjectsAPI()
		}.start()
	}
	private fun loadInvitingProjectsAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetInvitingMembersOfUser",
					cookies = mapOf("auth" to API.getAuth(context)),
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
					val resOj = Gson().fromJson(response.data.toString(), ProjectModels.InvitingProjects::class.java)
					_invitingProjects.postValue(resOj.invitingProjects);
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

	public fun update(projectId:String?,idInvitingMember:String?,status:String,callback:(String?,String?)->Unit){
		_isLoading.postValue(true)
		Thread {
			updateAPI(projectId,idInvitingMember,status,callback)
		}.start()
	}
	private fun updateAPI(projectId:String?,idInvitingMember:String?,status:String,callback:(String?,String?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/Create",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_project" to (projectId?:""),
						"id_inviting_member" to (idInvitingMember?:""),
						"status" to status
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
					callback(projectId,idInvitingMember)
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