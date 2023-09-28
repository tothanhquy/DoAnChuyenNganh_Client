package com.example.tcapp.viewmodel.account
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog

class ForgetPasswordAccountViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _wasSent:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val wasSent:LiveData<Boolean>
		get() = _wasSent
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun request(email:String?){
		_isLoading.postValue(true)
		Thread {
			requestAPI(email)
		}.start()
		
//		_isLoading.postValue(false)
	}
	private fun requestAPI(email:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Account/RequestResetPassword",
					data = mapOf("email" to email)
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else {
				if(response.status=="Success"){
					_wasSent.postValue(true)
					_notification.postValue(AlertDialog.Notification("Success!","Reset password email was sent."))
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