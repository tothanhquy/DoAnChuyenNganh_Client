package com.example.tcapp.viewmodel.friend

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


class CreateFriendRequestViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)

	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification

	public fun createFriendRequest(
		userReceiveId:String?,
		content:String?,
		callback:()->Unit
	){

		_isLoading.postValue(true)
		Thread {
			createFriendRequestAPI(
				userReceiveId,
				content,
				callback
			)
		}.start()
	}
	
	private fun createFriendRequestAPI(
		userReceiveId:String?,
		content:String?,
		callback:()->Unit
	){
		try{
			var dataMap:MutableMap<String, String?> = mutableMapOf(
				"user_receive_id" to userReceiveId,
				"content" to content,
			);
			
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Friend/Create",
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
					_notification.postValue(AlertDialog.Notification("Success!","Created new friend request.",fun(dia: DialogInterface , i:Int){callback();}))
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