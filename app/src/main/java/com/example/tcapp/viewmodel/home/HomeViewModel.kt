package com.example.tcapp.viewmodel.home
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.google.firebase.annotations.concurrent.Background

class HomeViewModel (private val context : Context){
	private var _user:MutableLiveData<HomeModels.UserData> = MutableLiveData<HomeModels.UserData>();
	private var _isAuthen:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val user: LiveData<HomeModels.UserData>
		get() = _user
	public val isAuthen:LiveData<Boolean>
		get() = _isAuthen
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	constructor(){
//		_myArrayLiveData.postValue(myArray)
//	}
	
	public fun loadData(){
		_isLoading.postValue(true)
		Thread {
			checkAndLoadUser()
		}.start()
	}
	private fun checkAndLoadUser(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Account/GetBasicDataUser",
//					url = "https://ktor.io/docs/getting-started-ktor-client.html#make-request",
					cookies = mapOf("auth" to API.getAuth(context))
				)
			)
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_isAuthen.postValue(false)
			}else{
				if(response.status=="Success"){
					//authen
					_isAuthen.postValue(true)
					_user.postValue( HomeModels.UserData(
						response.data?.getString("name")?:"",
						response.data?.getString("avatar")?:"",
						response.data?.getBoolean("isVerifyEmail")?:false
					))
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
//			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
	}
	public fun requestVerifyEmail(callback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			requestVerifyEmailAPI(callback)
		}.start()
	}
	private fun requestVerifyEmailAPI(callback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Account/RequestVerifyEmail",
//					url = "https://ktor.io/docs/getting-started-ktor-client.html#make-request",
					cookies = mapOf("auth" to API.getAuth(context))
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
					_notification.postValue(AlertDialog.Notification("Success!","Complete. Let check your email.",fun(dia: DialogInterface , i:Int){callback();}))
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
//			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
	}
}