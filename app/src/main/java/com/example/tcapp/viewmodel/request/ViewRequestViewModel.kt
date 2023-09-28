package com.example.tcapp.viewmodel.request

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ViewRequestViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _request:MutableLiveData<RequestModels.Request> = MutableLiveData<RequestModels.Request>(null)
	
	public val request: LiveData<RequestModels.Request>
		get() = _request
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadRequest(viewer:String?,requestId:String?,teamId:String?){
		if(viewer!="leader"&&viewer!="user"){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			loadRequestAPI(viewer,requestId,teamId)
		}.start()
	}
	private fun loadRequestAPI(viewer:String?,requestId:String?,teamId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Request/Details",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"team_id" to (teamId?:""),
						"request_id" to (requestId?:""),
						"viewer" to (viewer?:""),
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
					viewRequest(viewer,response.data)
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
	private fun  viewRequest(viewer:String?,oj: JSONObject?){
		if(oj!=null){
			val methodString = oj.getString("requestMethod");
			val requestOj = oj.getJSONObject("request");
			
			var request = RequestModels.Request();
			
			if(viewer=="leader"){
				request.requestViewer = RequestModels.RequestViewer.LEADER;
			}else{
				request.requestViewer = RequestModels.RequestViewer.USER;
			}
			if(methodString=="send") {
				request.requestMethod = RequestModels.RequestMethod.SEND;
			}else {
				request.requestMethod = RequestModels.RequestMethod.RECEIVE;
			}
			
			request.title = if(!requestOj.isNull("Title"))requestOj.getString("Title") else null;
			request.content = if(!requestOj.isNull("Content"))requestOj.getString("Content") else null;
			request.isWaiting = requestOj.getBoolean("IsWaiting");
			request.wasReaded = requestOj.getBoolean("WasReaded");
			request.wasResponsed = requestOj.getBoolean("WasResponsed");
			request.isAgree = requestOj.getBoolean("IsAgree");
			request.isImportant = requestOj.getBoolean("IsImportant");
			val user = requestOj.getJSONObject("User")
			request.userId = if(!user.isNull("_id"))user.getString("_id") else null;
			request.userName = if(!user.isNull("Name"))user.getString("Name") else null;
			request.userAvatar = if(!user.isNull("Avatar"))user.getString("Avatar") else null;
			val team = requestOj.getJSONObject("Team")
			request.teamId = if(!team.isNull("_id"))team.getString("_id") else null;
			request.teamName = if(!team.isNull("Name"))team.getString("Name") else null;
			request.teamAvatar = if(!team.isNull("Avatar"))team.getString("Avatar") else null;
			
			val leader = if(!requestOj.isNull("TeamLeader"))requestOj.getJSONObject("TeamLeader")else null;
			if(leader!=null){
				request.teamLeaderId = leader.getString("_id")
				request.teamLeaderName =	leader.getString("Name")
				request.teamLeaderAvatar = leader.getString("Avatar")
			}
			request.requestTime = requestOj.getLong("RequestTime")
			request.responseTime = requestOj.getLong("ResponseTime")
			_request.postValue(request)
		}
	}
	
	public fun updateRequest(
		viewer:String?,
		teamId:String?,
		requestId:String?,
		status:String,
		okCallback:(String)->Unit
	){
		if(viewer!="leader"&&viewer!="user"){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			updateRequestAPI(
				viewer,
				teamId,
				requestId,
				status,
				okCallback
			)
		}.start()
	}
	
	private fun updateRequestAPI(
		viewer:String?,
		teamId:String?,
		requestId:String?,
		status:String,
		okCallback:(String)->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Request/Update",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"team_id" to teamId,
						"request_id" to requestId,
						"viewer" to viewer,
						"status" to status,
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
					_notification.postValue(AlertDialog.Notification("Success!","Update request complete.",fun(dia: DialogInterface , i:Int){okCallback(status);}))
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