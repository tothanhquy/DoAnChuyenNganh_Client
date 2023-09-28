package com.example.tcapp.viewmodel.post

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.post.PostModels.Static.convertStatusUpdateToString
import com.example.tcapp.model.request.RequestModels
import org.json.JSONObject


class PostsListViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _isLoadingMore:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _resListStatus:MutableLiveData<PostModels.PostsListStatus?> = MutableLiveData<PostModels.PostsListStatus?>(null)
	
	public var posts: ArrayList<PostModels.Post>? = null;
	
	public val resListStatus:LiveData<PostModels.PostsListStatus?>
		get() = _resListStatus
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val isLoadingMore:LiveData<Boolean>
		get() = _isLoadingMore
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
	public fun loadPosts(filter:PostModels.Filter,timePrevious:Long,teamId:String?){
		val filterString = PostModels.convertFilterToString(filter);
		if(isLoadingMore.value==true)return;
		_isLoadingMore.postValue(true)
		Thread {
			loadPostsAPI(filterString,timePrevious,teamId)
		}.start()
	}
	private fun loadPostsAPI(filter:String,timePrevious:Long,teamId:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Post/GetList",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"filter" to filter,
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
		val posts = ArrayList<PostModels.Post>();
		val status = PostModels.PostsListStatus();
		if(oj!=null){
			if(!oj.isNull("posts")){
				val postsObject = oj.getJSONArray("posts")
				for (i in 0 until postsObject.length()) {
					val post = postsObject.getJSONObject(i);
					posts!!.add(getPostFromObject(post));
				}
			}
			status.isActionable = oj.getBoolean("isActionable");
			status.isFinish = oj.getBoolean("isFinish");
			status.timePrevious = oj.getLong("timePrevious");
			this.posts=posts;
			_resListStatus.postValue(status);
		}
	}
	
	public fun updatePost(postId:String , status: PostModels.StatusUpdate , okCallback:(PostModels.StatusUpdate)->Unit){
		val statusString = convertStatusUpdateToString(status);
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			updatePostAPI(postId,statusString,{okCallback(status)})
		}.start()
	}
	private fun updatePostAPI(postId:String,status:String,okCallback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Post/Update",
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
					okCallback();
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
	
	private fun  getPostFromObject(postObject: JSONObject):PostModels.Post{
		val item = PostModels.Post();
		
		item.postId =  postObject.getString("postId");
		item.content =  postObject.getString("content");
		item.authorType =  PostModels.convertStringToAuthorType(postObject.getString("authorType"));
		item.authorId =  postObject.getString("authorId");
		item.authorName =  postObject.getString("authorName");
		item.authorAvatar =  postObject.getString("authorAvatar");
		item.postTime =  postObject.getLong("postTime");
		item.saveTime =  postObject.getLong("saveTime");
		item.images =  Genaral.convertObjectJsonArrayToArrayOfString(postObject.getJSONArray("images"));
		item.isActive =  postObject.getBoolean("isActive");
		item.wasSaved =  postObject.getBoolean("wasSaved");
		item.relationship =  PostModels.convertStringToRelationship(postObject.getString("relationship"));
		
		return item;
	}
}