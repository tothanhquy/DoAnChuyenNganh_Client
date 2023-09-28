package com.example.tcapp.viewmodel.team_profile
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.user_profile.UserProfileModels
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TeamProfileEditInfoViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun changeInfo(
		teamId:String?,
		teamName:String?,
		teamMaxim:String?,
		teamDescription:String?,
		teamInternalInfo:String?,
	){
		_isLoading.postValue(true)
		Thread {
			changeInfoAPI(
				teamId,
				teamName,
				teamMaxim,
				teamDescription,
				teamInternalInfo,
			)
		}.start()
	}
	
	private fun changeInfoAPI(
		teamId:String?,
		teamName:String?,
		teamMaxim:String?,
		teamDescription:String?,
		teamInternalInfo:String?,
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/TeamProfile/EditInfo",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to teamId,
						"name" to teamName,
						"internal_info" to teamInternalInfo,
						"maxim" to teamMaxim,
						"description" to teamDescription
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
					_notification.postValue(AlertDialog.Notification("Success!","Updated info complete."))
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