package com.example.tcapp.viewmodel.post

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.view.adapter_view.PostsListRecyclerAdapter
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class PostsListViewModel (private val context : Context): ViewModel(){
	private var _isLoadingMore:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)

	private var _postsList:MutableLiveData<PostModels.PostsList?> = MutableLiveData<PostModels.PostsList?>(null)
	private var _postDetails:MutableLiveData<PostModels.PostsList?> = MutableLiveData<PostModels.PostsList?>(null)

	public val postsList:LiveData<PostModels.PostsList?>
		get() = _postsList
	public val postDetails:LiveData<PostModels.PostsList?>
		get() = _postDetails
	public val isLoadingMore:LiveData<Boolean>
		get() = _isLoadingMore
	public fun loadPosts(filter:PostModels.Filter,timePrevious:Long,authorId:String?){
		val filterString = PostModels.convertFilterToString(filter);
		if(isLoadingMore.value==true)return;
		_isLoadingMore.postValue(true)
		Thread {
			loadPostsAPI(filterString,timePrevious,authorId)
		}.start()
	}
	private fun loadPostsAPI(filter:String,timePrevious:Long,authorId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Post/GetList",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"filter" to filter,
						"time" to timePrevious.toString(),
						"author_id" to (authorId?:""),
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
					handleResPosts(response.data)
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
		_isLoadingMore.postValue(false)
	}
	private fun  handleResPosts(oj: JSONObject?){
		if(oj!=null){
			val postsListCrude = Gson().fromJson(oj.toString(), PostModels.PostsListCrude::class.java)
			_postsList.postValue(PostModels.PostsList(postsListCrude));
		}
	}
	
	public fun userInteract(holder: PostsListRecyclerAdapter.ViewHolder, postId:String, status: PostModels.UserUpdateInteractStatus, okCallback:(PostsListRecyclerAdapter.ViewHolder,String, PostModels.PostUpdateInteractResponse)->Unit){
		val statusString = PostModels.convertUserUpdateInteractStatusToString(status);
		Thread {
			userInteractAPI(holder,postId,statusString,okCallback)
		}.start()
	}
	private fun userInteractAPI(holder:PostsListRecyclerAdapter.ViewHolder,postId:String,status:String,okCallback:(PostsListRecyclerAdapter.ViewHolder,String,PostModels.PostUpdateInteractResponse)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Post/UserInterRact",
					cookies = mapOf("auth" to API.getAuth(context)),
					data= mapOf(
						"post_id" to postId,
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
					val resCrude = Gson().fromJson(response.data.toString(), PostModels.PostUpdateInteractResponseCrude::class.java)
					okCallback(holder,postId,PostModels.PostUpdateInteractResponse(resCrude));
				}else{
					_error.postValue(AlertDialog.Error("Error!",response.error?:""))
				}
			}
		}catch(err:Exception){
			println(err.toString())
			_error.postValue(AlertDialog.Error("Error!","System error"))
		}
	}

	public fun loadPostDetails(postId:String?){
		if(isLoadingMore.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadPostDetailsAPI(postId)
		}.start()
	}
	private fun loadPostDetailsAPI(postId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Post/Details",
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
					handleResPostDetails(response.data)
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
	private fun  handleResPostDetails(oj: JSONObject?){
		if(oj!=null){
			val postsListCrude = Gson().fromJson(oj.toString(), PostModels.PostsListCrude::class.java)
			_postDetails.postValue(PostModels.PostsList(postsListCrude));
		}
	}


}