package com.example.tcapp.viewmodel.user_profile
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.user_profile.UserProfileModels
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class GuestUserProfileViewModel (private val context : Context){
	private var _avatar:MutableLiveData<String> = MutableLiveData<String>("");
	private var _relationship:MutableLiveData<String> = MutableLiveData<String>("guest");
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	
	private var _guestProfile:MutableLiveData<UserProfileModels.GuestProfile> = MutableLiveData<UserProfileModels.GuestProfile>()
	
	public val guestProfile: LiveData<UserProfileModels.GuestProfile>
		get() = _guestProfile
	public val avatar:LiveData<String>
		get() = _avatar
	public val relationship:LiveData<String>
		get() = _relationship
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	
//	public val userProfileAvatarsPath:String = API.getBaseUrl()+"/static/images/users_avatar/"
//	public val skillImagesPath:String = API.getBaseUrl()+"/static/images/skills/"
	
	
	
	public fun loadData(id:String?){
		if(id==null){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			loadUserProfile(id!!)
		}.start()
	}
	private fun loadUserProfile(id:String){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/UserProfile/GetGuestProfile",
					cookies = mapOf("auth" to API.getAuth(context)),
					params = mapOf("id" to id)
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
			_relationship.postValue(if(!oj.isNull("relationship"))oj.getString("relationship") else "guest")
			_avatar.postValue(if(!oj.isNull("avatar"))oj.getString("avatar") else "")
			var profile = UserProfileModels.GuestProfile();
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
			if(!oj.isNull("chanelChat")){
				profile.friendChanelChatId = oj.getString("chanelChat");
			};

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
			
			profile.cvs = ArrayList<UserProfileModels.GuestCV>();
			if(!oj.isNull("cvs")){
				val cvsOj = oj.getJSONArray("cvs")
				for (i in 0 until cvsOj.length()) {
					val cv = cvsOj.getJSONObject(i);
					profile.cvs!!.add(UserProfileModels.GuestCV(
						cv.getString("id"),
						cv.getString("name")
					))
				}
			}
			_guestProfile.postValue(profile)
		}
	}

	public fun cancelFriend(friendId:String?,callback:()->Unit){
		if(friendId==null){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			cancelFriendPost(friendId!!,callback)
		}.start()
	}
	private fun cancelFriendPost(friendId:String,callback:()->Unit){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url = API.getBaseUrl() + "/Friend/CancelFriend",
					cookies = mapOf("auth" to API.getAuth(context)),
					data = mapOf("friend_id" to friendId)
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
					_notification.postValue(AlertDialog.Notification("Success!","Cancel friend complete.",fun(dia: DialogInterface, i:Int){callback();}))
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