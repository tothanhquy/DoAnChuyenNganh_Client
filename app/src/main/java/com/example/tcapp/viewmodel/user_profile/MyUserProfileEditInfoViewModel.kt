package com.example.tcapp.viewmodel.user_profile
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

class MyUserProfileEditInfoViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun changeInfo(info:UserProfileModels.Info){
		_isLoading.postValue(true)
		Thread {
			changeInfoAPI(info)
		}.start()
	}
	
	private fun changeInfoAPI(info:UserProfileModels.Info){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/UserProfile/EditInfo",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"name" to info.name,
						"birth_year" to info.birthYear,
						"maxim" to info.maxim,
						"contact" to info.contact,
						"career_target" to info.careerTarget,
						"education" to info.education,
						"work_experience" to info.workExperience,
						"description" to info.description
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