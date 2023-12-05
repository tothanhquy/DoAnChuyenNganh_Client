package com.example.tcapp.viewmodel.account
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels

class ChangePasswordAccountViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun changePassword(oldPass:String?, newPass:String?, confirmNewPass:String?,okCallback:()->Unit){
		if(newPass!=confirmNewPass){
			_error.postValue(AlertDialog.Error("Error!","Confirm password is not right."))
			return
		}
		_isLoading.postValue(true)
		Thread {
			changePasswordAPI(oldPass,newPass,okCallback)
		}.start()
	}
	private fun changePasswordAPI(oldPass:String?, newPass:String?,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Account/ChangePassword",
					cookies = mapOf("auth" to API.getAuth(context)),
					data = mapOf("old_password" to oldPass, "new_password" to newPass)
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else {
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Change password complete.",fun(dia: DialogInterface, i:Int){okCallback();}))
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