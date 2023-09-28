package com.example.tcapp.viewmodel.user_profile
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.google.gson.Gson
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MyUserProfileEditSkillsViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	private var _allSkills:MutableLiveData<ArrayList<UserProfileModels.Skill>> = MutableLiveData<ArrayList<UserProfileModels.Skill>>(ArrayList<UserProfileModels.Skill>())
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	public val allSkills:LiveData<ArrayList<UserProfileModels.Skill>>
		get() = _allSkills
	
	public fun saveSkills(skills:ArrayList<String>){
		val skillsJson = Gson().toJson(skills)
		_isLoading.postValue(true)
		Thread {
			saveSkillsAPI(skillsJson)
		}.start()
	}
	
	private fun saveSkillsAPI(skills:String){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/UserProfile/EditSkills",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"skills" to skills,
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
					_notification.postValue(AlertDialog.Notification("Success!","Save skills complete."))
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
	
	public fun getAllSkills(){
		_isLoading.postValue(true)
		Thread {
			getAllSkillsAPI()
		}.start()
	}
	
	private fun getAllSkillsAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url =  API.getBaseUrl() + "/Skill/GetSkills",
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
					_allSkills.postValue(getAllSkillsFromResponse(response.data))
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
	private fun getAllSkillsFromResponse(oj:JSONObject?):ArrayList<UserProfileModels.Skill>{
		var skillsRes:ArrayList<UserProfileModels.Skill> = ArrayList<UserProfileModels.Skill>()
		if(oj!=null){
			if(!oj.isNull("skills")){
				val skillsOj = oj.getJSONArray("skills")
				for (i in 0 until skillsOj.length()) {
					val skill = skillsOj.getJSONObject(i);
					skillsRes.add(UserProfileModels.Skill(
						skill.getString("id"),
						skill.getString("name"),
						skill.getString("image")
					))
				}
				if(skillsRes.size!=0){
					skillsRes.sortWith(compareBy { it.name })
				}
			}
		}
		return skillsRes
	}
	
}