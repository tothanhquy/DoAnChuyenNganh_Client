package com.example.tcapp.viewmodel.chanel_chat

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.friend.FriendModels
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ChanelChatInsertMembersViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _insertMembersView:MutableLiveData<ArrayList<ChanelChatModels.InsertMemberAdapterItem>?> = MutableLiveData<ArrayList<ChanelChatModels.InsertMemberAdapterItem>?>(null)

	public val insertMembersView: LiveData<ArrayList<ChanelChatModels.InsertMemberAdapterItem>?>
		get() = _insertMembersView
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification

	public fun loadData(chanelChatId:String?){
		_isLoading.postValue(true)
		Thread {
			loadMembers(chanelChatId)
		}.start()
	}
	private fun loadMembers(chanelChatId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/ChanelChat/GetMembers",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id_chanel_chat" to (chanelChatId?:"")
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
					handleResMembers(response.data)
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
	private fun  handleResMembers(oj: JSONObject?){
		if(oj!=null){
			val members = ArrayList<ChanelChatModels.Member>();
			if(!oj.isNull("members")){
				val insertMembersObject = oj.getJSONArray("members")
				for (i in 0 until insertMembersObject.length()) {
					val member = insertMembersObject.getJSONObject(i);
					members!!.add(
						ChanelChatModels.Member(
							if(!member.isNull("id"))member.getString("id") else null,
							if(!member.isNull("name"))member.getString("name") else null,
							if(!member.isNull("avatar"))member.getString("avatar") else null,
						)
					);
				}
			}
			loadMyFriendsAPI(members)
		}
	}

	private fun loadMyFriendsAPI(members:ArrayList<ChanelChatModels.Member>){
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
					handleResMyFriends(members,response.data)
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
	private fun  handleResMyFriends(members:ArrayList<ChanelChatModels.Member>,oj: JSONObject?){
		val membersId = members.map{it.id};
		val insertMembers = ArrayList<ChanelChatModels.InsertMemberAdapterItem>();
		if(oj!=null){
			if(!oj.isNull("friends")){
				val myFriendsObject = oj.getJSONArray("friends")
				for (i in 0 until myFriendsObject.length()) {
					val myFriendOj = myFriendsObject.getJSONObject(i);
					val myFriend=getMyFriendFromObject(myFriendOj);
					insertMembers.add(
						ChanelChatModels.InsertMemberAdapterItem(
							myFriend.userId,myFriend.userName,myFriend.userAvatar,
							membersId.indexOf(myFriend.userId)!=-1
							))
				}
			}
			_insertMembersView.postValue(insertMembers);
		}
	}
	private fun  getMyFriendFromObject(requestObject: JSONObject): FriendModels.MyFriendsListItem{
		val item = FriendModels.MyFriendsListItem();
		item.userId = requestObject.getString("id");
		item.userName = requestObject.getString("name");
		item.userAvatar = requestObject.getString("avatar");
		return item;
	}

	public fun insert(chanelChatId:String?,members:ArrayList<String>,insertOkCallback:(a:ArrayList<String>?)->Unit){
		_isLoading.postValue(true)
		Thread {
			insertAPI(chanelChatId,members,insertOkCallback)
		}.start()
	}

	private fun insertAPI(chanelChatId:String?,members:ArrayList<String>,insertOkCallback:(a:ArrayList<String>?)->Unit){
		try{
			val membersJson = Gson().toJson(members)
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/ChanelChat/InsertMembers",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_chanel_chat" to (chanelChatId?:""),
						"members" to membersJson,
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
					_notification.postValue(AlertDialog.Notification("Success!","Insert members complete.", fun(dia: DialogInterface, i:Int){insertOkCallback(members);}))
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