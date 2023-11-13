package com.example.tcapp.viewmodel.chanel_chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class MyChanelChatsViewModel (private val context : Context){
//	private var _avatar:MutableLiveData<String> = MutableLiveData<String>("");
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _myChanelChats:MutableLiveData<ArrayList<ChanelChatModels.ChanelChat>> = MutableLiveData<ArrayList<ChanelChatModels.ChanelChat>>()
	private var _newChanelChat:MutableLiveData<ChanelChatModels.ChanelChat?> = MutableLiveData<ChanelChatModels.ChanelChat?>(null)
//	private var _reShowCreateNewGroupChat:MutableLiveData<Int> = MutableLiveData<Int>(0)
	
	public val myChanelChats: LiveData<ArrayList<ChanelChatModels.ChanelChat>>
		get() = _myChanelChats
	public val newChanelChat: LiveData<ChanelChatModels.ChanelChat?>
		get() = _newChanelChat
//	public val reShowCreateNewGroupChat:LiveData<Int>
//		get() = _reShowCreateNewGroupChat
	public var reShowCreateNewGroupChat:Int = 0;
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification

	public fun loadData(){
		_isLoading.postValue(true)
		Thread {
			loadMyChanelChats()
		}.start()
	}
	private fun loadMyChanelChats(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/ChanelChat/GetChanelChatsOfUser",
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
					handleResChanelChats(response.data)
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
	private fun  handleResChanelChats(oj: JSONObject?){
		val chanelChats = ArrayList<ChanelChatModels.ChanelChat>();
		if(oj!=null){
			if(!oj.isNull("chanelChats")){
				val chanelChatsObject = oj.getJSONArray("chanelChats")
				for (i in 0 until chanelChatsObject.length()) {
					val chanelChat = chanelChatsObject.getJSONObject(i);
					chanelChats!!.add(getChanelChatFromObject(chanelChat));
				}
			}
		}
		_myChanelChats.postValue(chanelChats);
	}
	public fun createNewGroupChat(newGroupChat:String?){
		_isLoading.postValue(true)
		Thread {
			createNewGroupChatAPI(newGroupChat)
		}.start()
	}
	
	private fun createNewGroupChatAPI(newGroupChat:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/ChanelChat/CreateGroup",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"name" to newGroupChat
					)
				)
			)
			
			if(response.code==1||response.code==404){
				reShowCreate()
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				reShowCreate()
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Created new group chat."))
					_newChanelChat.postValue(getChanelChatFromObject(response.data!!.getJSONObject("newChanelChat")))
				}else{
					reShowCreate()
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			reShowCreate()
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
		
	}
	
	private fun reShowCreate(){
		reShowCreateNewGroupChat += 1
	}
	
	private fun  getChanelChatFromObject(chanelChatObject: JSONObject):ChanelChatModels.ChanelChat{
		return ChanelChatModels.ChanelChat(
			getTypeFromString(chanelChatObject.getString("type")),
			chanelChatObject.getString("id"),
			if(!chanelChatObject.isNull("name"))chanelChatObject.getString("name") else null,
			if(!chanelChatObject.isNull("avatar"))chanelChatObject.getString("avatar") else null,
			chanelChatObject.getLong("lastTimeAction"),
			chanelChatObject.getLong("lastMessageTime"),
			if(!chanelChatObject.isNull("lastMessageContent"))chanelChatObject.getString("lastMessageContent") else null,
			chanelChatObject.getLong("numberOfNewMessages"),
		)
	}
	private fun getTypeFromString(type:String?):ChanelChatModels.Type{
		return if(type.equals("team")) ChanelChatModels.Type.Team else
		if(type.equals("friend")) ChanelChatModels.Type.Friend else
			ChanelChatModels.Type.Group;
	}
}