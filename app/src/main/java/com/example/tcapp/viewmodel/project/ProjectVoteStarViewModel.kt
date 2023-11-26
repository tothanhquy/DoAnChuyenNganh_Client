package com.example.tcapp.viewmodel.project
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectVoteStarViewModel (private val context : Context):ViewModel(){
	public fun voteStar(
		projectId:String?,
		star:Int,
		callback: () -> Unit
	){
		_isLoading.postValue(true)
		Thread {
			voteStarAPI(
				projectId,
				star,
				callback
			)
		}.start()
	}
	
	private fun voteStarAPI(
		projectId:String?,
		star:Int,
		callback: () -> Unit
	){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Project/VoteStar",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to projectId,
						"star" to star,
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
					_notification.postValue(AlertDialog.Notification("Success!","Vote star complete."))
					callback()
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

	public fun getMyVoteStar(idProject:String?,callback:(Int)->Unit){
		_isLoading.postValue(true)
		Thread {
			getMyVoteStarAPI(idProject,callback)
		}.start()
	}
	private fun getMyVoteStarAPI(idProject:String?,callback:(Int)->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Project/GetMyVoteStar",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf(
						"id" to (idProject?:"")
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
					callback(response.data!!.getInt("myVoteStar"))
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