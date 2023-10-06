package com.example.tcapp.viewmodel.friend

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.request.RequestModels
import org.json.JSONObject


class MyFriendAndRequestsListViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _resRequestsListStatus:MutableLiveData<FriendModels.RequestsListStatus?> = MutableLiveData<FriendModels.RequestsListStatus?>(null)
	private var _resMyFriendsStatus:MutableLiveData<Long> = MutableLiveData<Long>(0)

	public var requests: ArrayList<FriendModels.RequestsListItem>? = null;
	public var myFriends: ArrayList<FriendModels.MyFriendsListItem>? = null;

	public val resRequestsListStatus:LiveData<FriendModels.RequestsListStatus?>
		get() = _resRequestsListStatus
	public val resMyFriendsStatus:LiveData<Long>
		get() = _resMyFriendsStatus

	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadRequests(method:String,timePrevious:Long){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadRequestsAPI(method,timePrevious)
		}.start()
	}
	private fun loadRequestsAPI(method:String,timePrevious:Long){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Friend/GetFriendRequests",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"method" to method,
						"time" to timePrevious.toString(),
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
					handleResRequests(method,response.data)
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
	private fun  handleResRequests(method:String,oj: JSONObject?){
		val requests = ArrayList<FriendModels.RequestsListItem>();
		val status = FriendModels.RequestsListStatus();
		if(oj!=null){
			if(!oj.isNull("requests")){
				val requestsObject = oj.getJSONArray("requests")
				for (i in 0 until requestsObject.length()) {
					val request = requestsObject.getJSONObject(i);
					requests!!.add(getRequestFromObject(request));
				}
			}
			status.method =
				if(method=="send")
					FriendModels.RequestMethod.SEND
				else FriendModels.RequestMethod.RECEIVE;

			status.isFinish = oj.getBoolean("isFinish");
			status.timePrevious = oj.getLong("timePrevious");
			this.requests=requests;
			_resRequestsListStatus.postValue(status);
		}
	}
	private fun  getRequestFromObject(requestObject: JSONObject):FriendModels.RequestsListItem{
		val item = FriendModels.RequestsListItem();
		item.id = requestObject.getString("id");
		item.userId = requestObject.getString("userId");
		item.userName = requestObject.getString("userName");
		item.userAvatar = requestObject.getString("userAvatar");
		item.time = requestObject.getLong("time");
		
		return item;
	}

	public fun loadMyFriends(){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadMyFriendsAPI()
		}.start()
	}
	private fun loadMyFriendsAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Friend/GetFriendsOfUser",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf()
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
					handleResMyFriends(response.data)
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
	private fun  handleResMyFriends(oj: JSONObject?){
		val myFriends = ArrayList<FriendModels.MyFriendsListItem>();
		if(oj!=null){
			if(!oj.isNull("friends")){
				val myFriendsObject = oj.getJSONArray("friends")
				for (i in 0 until myFriendsObject.length()) {
					val myFriend = myFriendsObject.getJSONObject(i);
					myFriends!!.add(getMyFriendFromObject(myFriend));
				}
			}
			this.myFriends=myFriends;
			_resMyFriendsStatus.postValue(System.currentTimeMillis());
		}
	}
	private fun  getMyFriendFromObject(requestObject: JSONObject):FriendModels.MyFriendsListItem{
		val item = FriendModels.MyFriendsListItem();
		item.userId = requestObject.getString("id");
		item.userName = requestObject.getString("name");
		item.userAvatar = requestObject.getString("avatar");
		return item;
	}

}