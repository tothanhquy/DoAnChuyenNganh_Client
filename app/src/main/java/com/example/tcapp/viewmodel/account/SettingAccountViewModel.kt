package com.example.tcapp.viewmodel.account
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.account.SettingAccountModels
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File

class SettingAccountViewModel (private val context : Context): ViewModel(){
	private var _isLogout:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

	private var _registerReceiveEmailResponse:MutableLiveData<SettingAccountModels.RegisterReceiveEmailResponse?> = MutableLiveData<SettingAccountModels.RegisterReceiveEmailResponse?>(null)

	public val isLogout:LiveData<Boolean>
		get() = _isLogout
	public val registerReceiveEmailResponse:LiveData<SettingAccountModels.RegisterReceiveEmailResponse?>
		get() = _registerReceiveEmailResponse

	public fun logout(okCallback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			logoutAPI(false,okCallback)
		}.start()
	}
	public fun logoutAll(okCallback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			logoutAPI(true,okCallback)
		}.start()
	}
	private fun logoutAPI(isLogoutAll:Boolean,okCallback:()->Unit){
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
					okCallback()
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

	public fun updateRegisterReceiveEmail(status:ArrayList<String>,okCallback:()->Unit){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		val statusJson = Gson().toJson(status);
		Thread {
			updateRegisterReceiveEmailAPI(statusJson,okCallback)
		}.start()
	}

	private fun updateRegisterReceiveEmailAPI(statusJson:String,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Account/EditRegisterReceiveEmail",
					cookies = mapOf("auth" to API.getAuth(context)),
					data= mapOf(
						"status" to statusJson
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
					_notification.postValue(AlertDialog.Notification("Success!","You updated complete.",fun(dia: DialogInterface, i:Int){okCallback();}))
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

	public fun loadRegisterReceiveEmail(){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadRegisterReceiveEmailAPI()
		}.start()
	}
	private fun loadRegisterReceiveEmailAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Account/GetRegisterReceiveEmail",
					cookies = mapOf("auth" to API.getAuth(context)),
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
					_registerReceiveEmailResponse.postValue(Gson().fromJson(response.data.toString(), SettingAccountModels.RegisterReceiveEmailResponse::class.java));
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