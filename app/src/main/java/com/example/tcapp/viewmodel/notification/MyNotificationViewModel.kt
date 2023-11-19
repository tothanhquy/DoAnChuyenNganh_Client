package com.example.tcapp.viewmodel.notification

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.notification.NotificationModels
import com.google.gson.Gson
import org.json.JSONObject


class MyNotificationViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _isLoadingMore:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _myNotifications: MutableLiveData<NotificationModels.Notifications?> = MutableLiveData<NotificationModels.Notifications?>(null);
	public val myNotifications:LiveData<NotificationModels.Notifications?>
		get() = _myNotifications

	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val isLoadingMore:LiveData<Boolean>
		get() = _isLoadingMore
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadNotifications(timePrevious:Long){
		if(isLoadingMore.value==true)return;
		_isLoadingMore.postValue(true)
		Thread {
			loadNotificationsAPI(timePrevious)
		}.start()
	}
	private fun loadNotificationsAPI(timePrevious:Long){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Notification/GetNotificationsOfUser",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"last_time" to timePrevious.toString(),
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
					handleResNotifications(response.data)
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoadingMore.postValue(false)
	}
	private fun  handleResNotifications(oj: JSONObject?){
		if(oj!=null){
			if(!oj.isNull("notifications")){
				val notificationsObject = oj.getJSONObject("notifications")
				val notificationsCrude = Gson().fromJson(notificationsObject.toString(), NotificationModels.NotificationsCrude::class.java)
				_myNotifications.postValue(NotificationModels().Notifications(notificationsCrude));
			}
		}
	}

	public fun loadNotification(id:String?,okCallback:(NotificationModels.Notification?)->Unit){
		Thread {
			loadNotificationAPI(id,okCallback)
		}.start()
	}
	private fun loadNotificationAPI(id:String?,okCallback:(NotificationModels.Notification?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Notification/GetNotification",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"id_notification" to (id?:""),
					)
				)
			)

			if(response.code==1||response.code==404){
				//system error
//				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
//				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					val notificationCrude = Gson().fromJson(response.data.toString(), NotificationModels.NotificationCrude::class.java)
					okCallback(NotificationModels().Notification(notificationCrude))
				}else{
//					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
//			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
	}

	public fun updateNotification(notificationId:String?){
		Thread {
			updateNotificationAPI(notificationId);
		}.start();
	}
	private fun updateNotificationAPI(notificationId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Notification/UserRead",
					cookies = mapOf("auth" to API.getAuth(context)),
					data= mapOf(
						"id_notification" to notificationId,
					)
				)
			)
			
			if(response.code==1||response.code==404){
				//system error
//				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
//				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){

				}else{
//					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
//			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
	}
}