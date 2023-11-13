package com.example.tcapp.viewmodel.chanel_chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class ChanelChatViewMembersListViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _membersView:MutableLiveData<ChanelChatModels.ChanelChatMembersList> = MutableLiveData<ChanelChatModels.ChanelChatMembersList>(null)
	private var _memberDeletedId:MutableLiveData<String?> = MutableLiveData<String?>(null)
	
	public val membersView: LiveData<ChanelChatModels.ChanelChatMembersList?>
		get() = _membersView
	public val memberDeletedId: LiveData<String?>
		get() = _memberDeletedId
	
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
			val typeString = if(!oj.isNull("type"))oj.getString("type") else null
			val type = if(typeString.equals("team")) ChanelChatModels.Type.Team else
				if(typeString.equals("friend")) ChanelChatModels.Type.Friend else
					ChanelChatModels.Type.Group;

			val memberView = ChanelChatModels.ChanelChatMembersList(type);
			memberView.isGroupOwner=
				if(!oj.isNull("isGroupOwner"))oj.getBoolean("isGroupOwner") else false;
			memberView.accountId=
				if(!oj.isNull("accountId"))oj.getString("accountId") else null;

			val members = ArrayList<ChanelChatModels.Member>();
			if(!oj.isNull("members")){
				val membersObject = oj.getJSONArray("members")
				for (i in 0 until membersObject.length()) {
					val member = membersObject.getJSONObject(i);
					members!!.add(
						ChanelChatModels.Member(
							if(!member.isNull("id"))member.getString("id") else null,
							if(!member.isNull("name"))member.getString("name") else null,
							if(!member.isNull("avatar"))member.getString("avatar") else null,
						)
					);
				}
			}
			memberView.members=members
			_membersView.postValue(memberView)
		}
	}
	
	public fun deleteMember(memberId:String?,chanelChatId:String?){
		_isLoading.postValue(true)
		Thread {
			deleteMemberAPI(memberId,chanelChatId)
		}.start()
	}
	
	private fun deleteMemberAPI(memberId:String?,chanelChatId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/ChanelChat/DeleteMemberOfGroup",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_member" to memberId,
						"id_chanel_chat" to chanelChatId,
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
					_notification.postValue(AlertDialog.Notification("Success!","Deleted a member."))
					_memberDeletedId.postValue(memberId)
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