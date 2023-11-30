package com.example.tcapp.viewmodel.post

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class PostEditViewModel (private val context : Context): ViewModel(){
	private var _postEditDetails:MutableLiveData<PostModels.PostOwnerDetail?> = MutableLiveData<PostModels.PostOwnerDetail?>(null)

	public val postEditDetails:LiveData<PostModels.PostOwnerDetail?>
		get() = _postEditDetails

	public fun loadPostEditDetails(postId:String?){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadPostEditDetailsAPI(postId)
		}.start()
	}
	private fun loadPostEditDetailsAPI(postId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Post/OwnerGetEditInfo",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"id" to (postId?:""),
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
					handleResPostEditDetails(response.data)
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
	private fun  handleResPostEditDetails(oj: JSONObject?){
		if(oj!=null){
			val postEditCrude = Gson().fromJson(oj.toString(), PostModels.PostOwnerDetailCrude::class.java)
			_postEditDetails.postValue(PostModels.PostOwnerDetail(postEditCrude));
		}
	}

	public fun updatePost(postId:String,isActive:Boolean,content:String?,categoryKeywordsId:ArrayList<String?>,oldImages:ArrayList<String?>,filesPath:ArrayList<String?>,okCallback:()->Unit){
		_isLoading.postValue(true)
		val keywordsIdJson = Gson().toJson(categoryKeywordsId);
		val oldImagesJson = Gson().toJson(oldImages);
		val isActiveString = if(isActive)"active" else "inactive";
		Thread {
			updatePostAPI(postId,isActiveString,content,keywordsIdJson,oldImagesJson,filesPath,okCallback)
		}.start()
	}

	private fun updatePostAPI(postId:String,activeStatus:String,content:String?,categoryKeywordsIdString:String?,oldImagesString:String?,filesPath:ArrayList<String?>,okCallback:()->Unit){
		try{
			val requestBody  = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart("post_id",postId)
				.addFormDataPart("content",content?:"")
				.addFormDataPart("active_status",activeStatus)
				.addFormDataPart("keywords",categoryKeywordsIdString?:"[]")
				.addFormDataPart("old_images",oldImagesString?:"[]");

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
			val response = API.requestPost(context , "/Post/OwnerUpdate",requestBody.build())

			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","You created a post.",fun(dia: DialogInterface, i:Int){okCallback();}))
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