package com.example.tcapp.viewmodel.chanel_chat

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.chanel_chat.MessageModels
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ChanelChatDetailsViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _chanelChat:MutableLiveData<ChanelChatModels.ChanelChatDetails> = MutableLiveData<ChanelChatModels.ChanelChatDetails>()
	private var _chanelChatAvatar:MutableLiveData<String?> = MutableLiveData<String?>(null);
	private var _fullMembers:MutableLiveData<MessageModels.Messages?> = MutableLiveData<MessageModels.Messages?>(null)
	
	public val chanelChat: LiveData<ChanelChatModels.ChanelChatDetails>
		get() = _chanelChat
	public val chanelChatAvatar:LiveData<String?>
		get() = _chanelChatAvatar
	public var chanelChatType:ChanelChatModels.Type? = null

	public val fullMembers: LiveData<MessageModels.Messages?>
		get() = _fullMembers
//	public var members: ArrayList<ChanelChatModels.Member>? = null;
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification

	public var reShowChangeGroupChatName:Int = 0;
	
	public fun loadData(idChanelChat:String?){
		_isLoading.postValue(true)
		Thread {
			loadChanelChatDetails(idChanelChat)
		}.start()
	}
	private fun loadChanelChatDetails(idChanelChat:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/ChanelChat/Details",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id_chanel_chat" to (idChanelChat?:"")
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
					handleDetails(response.data)
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
	private fun  handleDetails(ojRes: JSONObject?){
		if(ojRes!=null){
			val oj = ojRes.getJSONObject("resChanelChat")
			val typeString = if(!oj.isNull("type"))oj.getString("type") else null
			val type = if(typeString.equals("team")) ChanelChatModels.Type.Team else
				if(typeString.equals("friend")) ChanelChatModels.Type.Friend else
					ChanelChatModels.Type.Group;

			var chanelChat = ChanelChatModels.ChanelChatDetails(type);
			chanelChat.id = if(!oj.isNull("id"))oj.getString("id") else null;
			chanelChat.name = if(!oj.isNull("name"))oj.getString("name") else null;
			chanelChat.avatar = if(!oj.isNull("avatar"))oj.getString("avatar") else null;
			chanelChatType = type
			_chanelChatAvatar.postValue(chanelChat.avatar)
			chanelChat.isGroupOwner = if(!oj.isNull("isGroupOwner"))oj.getBoolean("isGroupOwner") else false;
			chanelChat.friendId = if(!oj.isNull("friendId"))oj.getString("friendId") else null;
			chanelChat.teamId = if(!oj.isNull("teamId"))oj.getString("teamId") else null;
			chanelChat.accountId = if(!oj.isNull("accountId"))oj.getString("accountId") else null;

			if(!oj.isNull("members")){
				val membersOj = oj.getJSONArray("members")
				val length = membersOj.length();
				for (i in 0 until length) {
					val member = membersOj.getJSONObject(i);
					chanelChat.members!!.add(ChanelChatModels.Member(
						if(!member.isNull("id"))member.getString("id") else null,
						if(!member.isNull("name"))member.getString("name") else null,
						if(!member.isNull("avatar"))member.getString("avatar") else null,
					))
				}
			}
			if(!oj.isNull("lastTimeMemberSeen")){
				val membersSeenOj = oj.getJSONArray("lastTimeMemberSeen")
				val length = membersSeenOj.length();
				for (i in 0 until length) {
					val membersSeen = membersSeenOj.getJSONObject(i);
					chanelChat.lastTimeMemberSeen!!.add(ChanelChatModels.LastTimeMemberSeen(
						if(!membersSeen.isNull("userId"))membersSeen.getString("userId") else null,
						if(!membersSeen.isNull("messageId"))membersSeen.getString("messageId") else null,
					))
				}
			}
			_chanelChat.postValue(chanelChat)
		}
	}
	public fun changeAvatar(imagePath:String?,chanelChatId:String?){
		if(imagePath!=null){
			_isLoading.postValue(true)
			Thread {
				changeAvatarAPI(imagePath!!,chanelChatId)
			}.start()
		}
	}

	private fun changeAvatarAPI(imagePath:String,chanelChatId:String?){
		try{
			val avatarImage = File(imagePath)
			val requestBody : RequestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("id_chanel_chat",chanelChatId)
				.addFormDataPart(
					"avatar" , avatarImage.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(avatarImage)) ,
						avatarImage
					)
				)
				.build()
			val response = API.requestPost(context , "/ChanelChat/EditGroupChatImage",requestBody)

			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Avatar was Updated."))
					_chanelChatAvatar.postValue(
						if(!response.data!!.isNull("new_avatar"))
							response.data!!.getString("new_avatar")
						else null
					)
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

	public fun changeNewNameGroupChat(chanelChatId:String?,newGroupChatName:String?,okCallback:(String?)->Unit){
		_isLoading.postValue(true)
		Thread {
			changeNewNameGroupChatAPI(chanelChatId,newGroupChatName,okCallback)
		}.start()
	}

	private fun changeNewNameGroupChatAPI(chanelChatId:String?,newGroupChatName:String?,okCallback:(String?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/ChanelChat/EditGroupChatName",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_chanel_chat" to chanelChatId,
						"new_name" to newGroupChatName
					)
				)
			)

			if(response.code==1||response.code==404){
				reShowChangeName()
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				reShowChangeName()
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Changed name of group chat.",fun(dia: DialogInterface , i:Int){okCallback(newGroupChatName);}))
				}else{
					reShowChangeName()
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			reShowChangeName()
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)

	}

	private fun reShowChangeName(){
		reShowChangeGroupChatName += 1
	}

	public fun exitChanelChat(
		chanelChatId:String?,
		newGroupOwnerId:String?,
		okCallback:()->Unit
	){
		_isLoading.postValue(true)
		Thread {
			exitChanelChatAPI(
				chanelChatId,
				newGroupOwnerId,
				okCallback
			)
		}.start()
	}
	
	private fun exitChanelChatAPI(
		chanelChatId:String?,
		newGroupOwnerId:String?,
		okCallback:()->Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/ChanelChat/ExitGroupChat",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_chanel_chat" to chanelChatId,
						"id_new_group_owner" to newGroupOwnerId,
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
					_notification.postValue(AlertDialog.Notification("Success!","Exit Chanel Chat complete.",fun(dia: DialogInterface , i:Int){okCallback();}))
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

	//message
	public fun getMessagesHistory(chanelChatId:String?,lastTime:Long,okCallback:(messages:MessageModels.Messages?)->Unit){
		_isLoading.postValue(true)
		Thread {
			getMessagesHistoryAPI(chanelChatId,lastTime,okCallback)
		}.start()
	}

	private fun getMessagesHistoryAPI(chanelChatId:String?,lastTime:Long,okCallback:(messages:MessageModels.Messages?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Message/GetMessagesOfChanelChat",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					params = mapOf(
						"id_chanel_chat" to (chanelChatId?:""),
						"last_time" to (""+lastTime)
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
//					_fullMembers.postValue(getMessagesFromOj(response.data))
					okCallback(getMessagesFromOj(response.data))
				}else{
					reShowChangeName()
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
	}
	private fun  getMessagesFromOj(ojRes: JSONObject?): MessageModels.Messages {
		var messages = MessageModels.Messages();
		if(ojRes!=null){
			val oj = ojRes.getJSONObject("messages")
			messages.isFinish = if(!oj.isNull("isFinish"))oj.getBoolean("isFinish") else false;

			if(!oj.isNull("messages")){
				val messagesOj = oj.getJSONArray("messages")
				val length = messagesOj.length();
				for (i in 0 until length) {
					val message = messagesOj.getJSONObject(i);
					messages.messages!!.add(MessageModels.Message(
						if(!message.isNull("id"))message.getString("id") else null,
						if(!message.isNull("content"))message.getString("content") else null,
						if(!message.isNull("userId"))message.getString("userId") else null,
						if(!message.isNull("time"))message.getLong("time") else 0L,
						if(!message.isNull("replyContent"))message.getString("replyContent") else null,
						if(!message.isNull("replyTime"))message.getLong("replyTime") else 0L,
						if(!message.isNull("replyId"))message.getString("replyId") else null,
					))
				}
			}
		}
		return messages;
	}

	public fun getMessagesBetweenTime(chanelChatId:String?,startTime:Long,lastTime:Long,okCallback:(messages:MessageModels.Messages?)->Unit){
		_isLoading.postValue(true)
		Thread {
			getMessagesBetweenTimeAPI(chanelChatId,startTime,lastTime,okCallback)
		}.start()
	}

	private fun getMessagesBetweenTimeAPI(chanelChatId:String?,startTime:Long,lastTime:Long,okCallback:(messages:MessageModels.Messages?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Message/GetMessagesOfChanelChatBetweenTime",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					params = mapOf(
						"id_chanel_chat" to (chanelChatId?:""),
						"last_time" to (""+lastTime),
						"start_time" to (""+startTime),
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
					okCallback(getMessagesFromOj(response.data))
				}else{
					reShowChangeName()
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoading.postValue(false)
	}

	public fun insert(chanelChatId:String?,idReply:String?,message:String,insertOkCallback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			insertAPI(chanelChatId,idReply,message,insertOkCallback)
		}.start()
	}

	private fun insertAPI(chanelChatId:String?,idReply:String?,message:String,insertOkCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/Message/CreateMessage",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_chanel_chat" to (chanelChatId?:""),
						"content" to message,
						"id_reply" to (idReply?:""),
					),
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
					insertOkCallback()
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

	public fun userSeen(chanelChatId:String?,okCallback:()->Unit){
		_isLoading.postValue(true)
		Thread {
			userSeenAPI(chanelChatId,okCallback)
		}.start()
	}

	private fun userSeenAPI(chanelChatId:String?,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/ChanelChat/UserSeen",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_chanel_chat" to (chanelChatId?:""),
					),
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


}