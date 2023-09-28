package com.example.tcapp.viewmodel.account
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels

class SettingAccountViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	private var _isLogout:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val isLogout:LiveData<Boolean>
		get() = _isLogout
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun logout(){
		_isLoading.postValue(true)
		Thread {
			logoutAPI(false)
		}.start()
	}
	public fun logoutAll(){
		_isLoading.postValue(true)
		Thread {
			logoutAPI(true)
		}.start()
	}
	private fun logoutAPI(isLogoutAll:Boolean){
		try{
			val path = if(isLogoutAll)"/Account/LogoutAll" else "/Account/Logout";
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + path,
					cookies = mapOf("auth" to API.getAuth(context))
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else {
				if(response.status=="Success"){
					_isLogout.postValue(true);
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