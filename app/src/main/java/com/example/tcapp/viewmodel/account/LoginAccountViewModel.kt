package com.example.tcapp.viewmodel.account
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels

class LoginAccountViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun login(email:String?, password:String?){
		_isLoading.postValue(true)
		Thread {
			loginAPI(email,password)
		}.start()
		
//		_isLoading.postValue(false)
	}
	private fun loginAPI(email:String?, password:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Account/Login",
					data = mapOf("email" to email, "password" to password)
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else {
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Login Success"))
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