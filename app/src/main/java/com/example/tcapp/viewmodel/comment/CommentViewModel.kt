package com.example.tcapp.viewmodel.comment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.comment.CommentModels
import com.example.tcapp.view.adapter_view.CommentsRecyclerAdapter
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson


class CommentViewModel (private val context : Context): ViewModel(){

	public fun getComments(postId:String?,replyId:String?,lastTime:Long,okCallback:(comments: CommentModels.Comments?)->Unit){
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			getCommentsAPI(postId,replyId,lastTime,okCallback)
		}.start()
	}

	private fun getCommentsAPI(postId:String?,replyId:String?,lastTime:Long,okCallback:(comments: CommentModels.Comments?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Comment/GetComments",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					params = mapOf(
						"id_post" to (postId?:""),
						"id_reply" to (replyId?:""),
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
					val parseRes = Gson().fromJson(response.data.toString(), CommentModels.Comments::class.java)
					okCallback(parseRes)
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

	public fun create(postId:String?,replyId:String?,content:String,okCallback:(CommentModels.Comment?)->Unit){
		_isLoading.postValue(true)
		Thread {
			createAPI(postId,replyId,content,okCallback)
		}.start()
	}

	private fun createAPI(postId:String?,replyId:String?,content:String,okCallback:(CommentModels.Comment?)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/Comment/CreateComment",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id_post" to (postId?:""),
						"content" to content,
						"id_reply" to (replyId?:""),
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
					val parseRes = Gson().fromJson(response.data.toString(), CommentModels.Comment::class.java)
					okCallback(parseRes)
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

	public fun userInteract(holder: CommentsRecyclerAdapter.ViewHolder, commentId:String, status: CommentModels.CommentUpdateInteractRequestStatus, okCallback:(CommentsRecyclerAdapter.ViewHolder, String, CommentModels.CommentUpdateInteractResponse)->Unit){
		val statusString = CommentModels.convertCommentUpdateInteractRequestStatusToString(status);
		Thread {
			userInteractAPI(holder,commentId,statusString,okCallback)
		}.start()
	}
	private fun userInteractAPI(holder:CommentsRecyclerAdapter.ViewHolder,commentId:String,status:String,okCallback:(CommentsRecyclerAdapter.ViewHolder,String, CommentModels.CommentUpdateInteractResponse)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Comment/UserInteract",
					cookies = mapOf("auth" to API.getAuth(context)),
					data= mapOf(
						"comment_id" to commentId,
						"status" to status
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
					val resCrude = Gson().fromJson(response.data.toString(), CommentModels.CommentUpdateInteractResponseCrude::class.java)
					okCallback(holder,commentId,CommentModels.CommentUpdateInteractResponse(resCrude));
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
	}

}