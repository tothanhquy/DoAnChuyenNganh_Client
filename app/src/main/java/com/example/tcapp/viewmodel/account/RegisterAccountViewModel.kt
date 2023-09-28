package com.example.tcapp.viewmodel.account
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels

class RegisterAccountViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun register(email:String?, password:String?, confirmPassword:String?,name:String?){
		if(password!=confirmPassword){
			_error.postValue(AlertDialog.Error("Error!","Confirm password is not right."))
			return
		}
		_isLoading.postValue(true)
		Thread {
			registerAPI(email,password,name)
		}.start()
		
//		_isLoading.postValue(false)
	}
	private fun registerAPI(email:String?, password:String?,name:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Account/Register",
					data = mapOf("email" to email, "password" to password, "name" to name)
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else {
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Register Account Success"))
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