package com.example.tcapp.viewmodel.request

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.request.RequestModels
import org.json.JSONObject


class RequestsListViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _resListStatus:MutableLiveData<RequestModels.RequestsListStatus?> = MutableLiveData<RequestModels.RequestsListStatus?>(null)
	
	public var requests: ArrayList<RequestModels.RequestsListItem>? = null;
	
	public val resListStatus:LiveData<RequestModels.RequestsListStatus?>
		get() = _resListStatus
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadRequests(viewer:String,method:String,timePrevious:Long,teamId:String?){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadRequestsAPI(viewer,method,timePrevious,teamId)
		}.start()
	}
	private fun loadRequestsAPI(viewer:String,method:String,timePrevious:Long,teamId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Request/GetList",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"viewer" to viewer,
						"method" to method,
						"time" to timePrevious.toString(),
						"team_id" to (teamId?:""),
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
					handleResRequests(response.data)
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
	private fun  handleResRequests(oj: JSONObject?){
		val requests = ArrayList<RequestModels.RequestsListItem>();
		val status = RequestModels.RequestsListStatus();
		if(oj!=null){
			if(!oj.isNull("requests")){
				val requestsObject = oj.getJSONArray("requests")
				for (i in 0 until requestsObject.length()) {
					val request = requestsObject.getJSONObject(i);
					requests!!.add(getRequestFromObject(request));
				}
			}
			if(oj.isNull("method"))return;
			status.method =
				if(oj.getString("method")=="send")
					RequestModels.RequestMethod.SEND
				else RequestModels.RequestMethod.RECEIVE;
			if(oj.isNull("viewer"))return;
			status.viewer =
				if(oj.getString("viewer")=="leader")
					RequestModels.RequestViewer.LEADER
				else RequestModels.RequestViewer.USER;
			status.isFinish = oj.getBoolean("isFinish");
			status.timePrevious = oj.getLong("timePrevious");
			this.requests=requests;
			_resListStatus.postValue(status);
		}
	}
	
	
	private fun  getRequestFromObject(requestObject: JSONObject):RequestModels.RequestsListItem{
		val item = RequestModels.RequestsListItem();
		
		item.id = requestObject.getString("_id");
		item.title = requestObject.getString("Title");
		item.isWaiting = requestObject.getBoolean("IsWaiting");
		item.wasResponsed = requestObject.getBoolean("WasResponsed");
		item.wasReaded = requestObject.getBoolean("WasReaded");
		item.isImportant = requestObject.getBoolean("IsImportant");
		val user = requestObject.getJSONObject("User")
		item.userName = if(!user.isNull("Name"))user.getString("Name") else null;
		item.userAvatar = if(!user.isNull("Avatar"))user.getString("Avatar") else null;
		val team = requestObject.getJSONObject("Team")
		item.teamName = if(!team.isNull("Name"))team.getString("Name") else null;
		item.teamAvatar = if(!team.isNull("Avatar"))team.getString("Avatar") else null;
		
		item.requestTime = requestObject.getLong("RequestTime");
		
		return item;
	}
}