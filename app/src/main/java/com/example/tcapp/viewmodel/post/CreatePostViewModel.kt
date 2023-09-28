package com.example.tcapp.viewmodel.post

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class CreatePostViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	
	public fun createPost(creator:String,teamId:String?,content:String?,filesPath:ArrayList<String?>){
		_isLoading.postValue(true)
		Thread {
			createPostAPI(creator,teamId,content,filesPath)
		}.start()
	}

	private fun createPostAPI(creator:String,teamId:String?,content:String?,filesPath:ArrayList<String?>){
		try{
			val requestBody  = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("creator",creator)
				.addFormDataPart("team_id",teamId?:"")
				.addFormDataPart("content",content?:"");
			
			filesPath.forEach{
				val image = File(it);
				requestBody.addFormDataPart(
					"images" , image.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(image)) ,
						image
					)
				)
			}
			
//				.build()
			val response = API.requestPost(context , "/Post/Create",requestBody.build())

			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","You created a post."))
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