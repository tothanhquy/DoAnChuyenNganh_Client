package com.example.tcapp.viewmodel.friend

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ViewFriendRequestViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _request:MutableLiveData<FriendModels.Request> = MutableLiveData<FriendModels.Request>(null)
	
	public val request: LiveData<FriendModels.Request>
		get() = _request
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadRequest(method:String?,requestId:String?){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadRequestAPI(method,requestId)
		}.start()
	}
	private fun loadRequestAPI(method:String?,requestId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Friend/Details",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"request_id" to (requestId?:""),
						"method" to (method?:""),
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
					viewRequest(response.data)
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
	private fun  viewRequest(oj: JSONObject?){
		if(oj!=null){
//			val methodString = oj.getString("requestMethod");

			var request = FriendModels.Request();

//			if(methodString=="send") {
//				request.requestMethod = FriendModels.RequestMethod.SEND;
//			}else {
//				request.requestMethod = FriendModels.RequestMethod.RECEIVE;
//			}
			
			request.content = if(!oj.isNull("content"))oj.getString("content") else null;
			request.userId = if(!oj.isNull("userId"))oj.getString("userId") else null;
			request.userName = if(!oj.isNull("userName"))oj.getString("userName") else null;
			request.userAvatar = if(!oj.isNull("userAvatar"))oj.getString("userAvatar") else null;
			request.time = oj.getLong("time")
			_request.postValue(request)
		}
	}
	
	public fun responseRequest(
		method:String?,
		requestId:String?,
		response:String,
		okCallback:()->Unit
	){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			responseRequestAPI(
				method,
				requestId,
				response,
				okCallback
			)
		}.start()
	}
	
	private fun responseRequestAPI(
		method:String?,
		requestId:String?,
		response:String,
		okCallback:()->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Friend/Response",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"request_id" to requestId,
						"method" to method,
						"response" to response,
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
					_notification.postValue(AlertDialog.Notification("Success!","Response friend request complete.",fun(dia: DialogInterface , i:Int){okCallback();}))
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