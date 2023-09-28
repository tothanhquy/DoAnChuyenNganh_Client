package com.example.tcapp.viewmodel.user_profile

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.user_profile.UserProfileModels
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File


class MyUserProfileViewModel (private val context : Context){
	private var _avatar:MutableLiveData<String> = MutableLiveData<String>("");
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _myProfile:MutableLiveData<UserProfileModels.MyProfile> = MutableLiveData<UserProfileModels.MyProfile>()
	
	public val myProfile: LiveData<UserProfileModels.MyProfile>
		get() = _myProfile
	public val avatar:LiveData<String>
		get() = _avatar
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	public val userProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
//	public val skillImagesPath:String = API.getBaseUrl()+"/static/images/skills/"
	
	
	
	public fun loadData(){
		_isLoading.postValue(true)
		Thread {
			loadUserProfile()
		}.start()
	}
	private fun loadUserProfile(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/UserProfile/GetMyProfile",
					cookies = mapOf("auth" to API.getAuth(context))
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
					viewProfile(response.data)
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
	private fun  viewProfile(oj: JSONObject?){
		if(oj!=null){
			_avatar.postValue(if(!oj.isNull("avatar"))oj.getString("avatar") else "")
			var profile = UserProfileModels.MyProfile();
			profile.info = UserProfileModels.Info()
			var infoOj = if(!oj.isNull("info"))oj.getJSONObject("info") else null;
			if(infoOj!=null){
				if(!infoOj.isNull("id")){
					profile.info?.id = infoOj.getString("id");
				};
				if(!infoOj.isNull("name")){
					profile.info?.name = infoOj.getString("name");
				};
				if(!infoOj.isNull("birthYear")){
					profile.info?.birthYear = infoOj.getInt("birthYear");
				};
				if(!infoOj.isNull("maxim")){
					profile.info?.maxim = infoOj.getString("maxim");
				};
				if(!infoOj.isNull("contact")){
					profile.info?.contact = infoOj.getString("contact");
				};
				if(!infoOj.isNull("careerTarget")){
					profile.info?.careerTarget = infoOj.getString("careerTarget");
				};
				if(!infoOj.isNull("education")){
					profile.info?.education = infoOj.getString("education");
				};
				if(!infoOj.isNull("workExperience")){
					profile.info?.workExperience = infoOj.getString("workExperience");
				};
				if(!infoOj.isNull("description")){
					profile.info?.description = infoOj.getString("description");
				};
			}
			
			profile.skills = ArrayList<UserProfileModels.Skill>();
			if(!oj.isNull("skills")){
				val skillsOj = oj.getJSONArray("skills")
				for (i in 0 until skillsOj.length()) {
					val skill = skillsOj.getJSONObject(i);
					profile.skills!!.add(UserProfileModels.Skill(
						skill.getString("id"),
						skill.getString("name"),
						if(!skill.isNull("image"))skill.getString("image") else ""
					))
				}
			}
			
			profile.cvs = ArrayList<UserProfileModels.MyCV>();
			if(!oj.isNull("cvs")){
				val cvsOj = oj.getJSONArray("cvs")
				for (i in 0 until cvsOj.length()) {
					val cv = cvsOj.getJSONObject(i);
					profile.cvs!!.add(UserProfileModels.MyCV(
						cv.getString("id"),
						cv.getString("name"),
						cv.getBoolean("isActive")
					))
				}
			}
			_myProfile.postValue(profile)
		}
	}
	public fun changeAvatar(imagePath:String?){
		if(imagePath!=null){
			_isLoading.postValue(true)
			Thread {
				changeAvatarAPI(imagePath!!)
			}.start()
		}
	}
	
	private fun changeAvatarAPI(imagePath:String){
		try{
			val avatarImage = File(imagePath)
//			val  response : API.ResponseAPI = API.getResponse(context,
//				khttp.post(
//					url = API.getBaseUrl() + "/UserProfile/EditAvatar",
//					cookies = mapOf("auth" to API.getAuth(context)) ,
//					files = listOf(
//						"avatar" to (avatarImage,API.getMimeType(avatarImage))
//					)
//
//				)
//			)
			val requestBody : RequestBody = MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addFormDataPart(
					"avatar" , avatarImage.name,
					RequestBody.create(
						MediaType.parse(API.getMimeType(avatarImage)) ,
						avatarImage
					)
				)
				.build()
			val response = API.requestPost(context , "/UserProfile/EditAvatar",requestBody)
			
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					_notification.postValue(AlertDialog.Notification("Success!","Avatar was Updated."))
					_avatar.postValue(if(!response.data!!.isNull("new_avatar"))response.data!!.getString("new_avatar") else "")
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