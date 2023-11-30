package com.example.tcapp.viewmodel.post

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class CreatePostViewModel (private val context : Context):ViewModel(){
	public fun createPost(creatorType:PostModels.CreatorType,authorId:String?,content:String?,categoryKeywordsId:ArrayList<String?>,filesPath:ArrayList<String?>,okCallback:(String?,String?)->Unit){
		_isLoading.postValue(true)
		val keywordsIdJson = Gson().toJson(categoryKeywordsId);
		val createTypeString = PostModels.convertCreatorToString(creatorType);
		Thread {
			createPostAPI(createTypeString,authorId,content,keywordsIdJson,filesPath,okCallback)
		}.start()
	}

	private fun createPostAPI(creatorType:String,authorId:String?,content:String?,categoryKeywordsIdString:String?,filesPath:ArrayList<String?>,okCallback:(String?,String?)->Unit){
		try{
			val requestBody  = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("creator_type",creatorType)
				.addFormDataPart("creator_id",authorId?:"")
				.addFormDataPart("content",content?:"")
				.addFormDataPart("keywords",categoryKeywordsIdString?:"[]");

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
					val newPostId = if(!response.data!!.isNull("newPostId"))response.data!!.getString("newPostId") else null;
					val uploadImageResult = if(!response.data!!.isNull("uploadImageResult"))response.data!!.getString("uploadImageResult") else null;
					_notification.postValue(AlertDialog.Notification("Success!","You created a post.",fun(dia: DialogInterface, i:Int){okCallback(newPostId,uploadImageResult);}))
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