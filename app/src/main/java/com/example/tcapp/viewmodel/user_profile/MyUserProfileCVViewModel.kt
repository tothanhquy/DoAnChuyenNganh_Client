package com.example.tcapp.viewmodel.user_profile
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.user_profile.UserProfileModels
import khttp.structures.files.FileLike
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class MyUserProfileCVViewModel (private val context : Context){
	private var _isLoading:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _error:MutableLiveData<AlertDialog.Error?> = MutableLiveData<AlertDialog.Error?>(null)
	private var _notification:MutableLiveData<AlertDialog.Notification?> = MutableLiveData<AlertDialog.Notification?>(null)
	private var _wasDelete:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	private var _viewPDFAction:MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
	
	public val isLoading:LiveData<Boolean>
		get() = _isLoading
	public val error:LiveData<AlertDialog.Error?>
		get() = _error
	public val notification:LiveData<AlertDialog.Notification?>
		get() = _notification
	public val wasDelete:LiveData<Boolean>
		get() = _wasDelete
	public val viewPDFAction:LiveData<Boolean>
		get() = _viewPDFAction
	public var isActive:Boolean = false;
	public var ownerToken:String = "";
	
	public fun createOrEditCV(method:String?,cv:UserProfileModels.MyCV,pdfPath:String?){
		if(method==null||method==""){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}else if(pdfPath==null&&method=="create"){
			_error.postValue(AlertDialog.Error("Error!","Please choose pdf cv."))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			createOrEditCVAPI(method,cv,pdfPath)
		}.start()
	}
	
	private fun createOrEditCVAPI(method:String?,cv:UserProfileModels.MyCV,pdfPath:String?){
		try{
			var url=
			if(method=="edit"){
				"/UserProfile/EditCV";
			}else{
				"/UserProfile/InsertCV";
			}
			val pdfFile = if(pdfPath===null)null else File(pdfPath)
//			val  response : API.ResponseAPI = API.getResponse(context,
//				khttp.post(
//					url = url,
//					cookies = mapOf("auth" to API.getAuth(context)) ,
//					data = mapOf(
//						"name" to cv.name,
//						"is_active" to cv.isActive.toString(),
//						"id" to cv.id,
//					),
//					files = listOf(
//						FileLike("cv_pdf", File(pdfUri.path))
//					)
//				)
//			)
			
			val requestBody : RequestBody =
			if(pdfFile==null){
				MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("name", cv.name)
					.addFormDataPart("is_active", cv.isActive.toString())
					.addFormDataPart("id", cv.id)
					.build()
			}else{
				MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("name", cv.name)
					.addFormDataPart("id", cv.id?:"")
					.addFormDataPart("is_active", cv.isActive.toString())
					.addFormDataPart(
						"cv_pdf" , pdfFile!!.name,
						RequestBody.create(
							MediaType.parse(API.getMimeType(pdfFile)) ,
							pdfFile!!
						)
					)
					.build()
			}
			
			val response = API.requestPost(context , url,requestBody)
			
			if(response.code==1||response.code==404){
				//system error
				_error.postValue(AlertDialog.Error("Error!","System error"))
			}else if(response.code==403){
				//not authen
				_error.postValue(AlertDialog.Error("Error!","You are logout."))
			}else{
				if(response.status=="Success"){
					if(method=="edit"){
						_notification.postValue(AlertDialog.Notification("Success!","Update CV complete."))
						isActive = cv.isActive;
					}else{
						_notification.postValue(AlertDialog.Notification("Success!","Create CV complete."))
					}
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
	
	public fun deleteCV(idCV:String?){
		if(idCV==null||idCV==""){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			deleteCVAPI(idCV)
		}.start()
	}
	
	private fun deleteCVAPI(idCV:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/UserProfile/DeleteCV",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to idCV,
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
					_notification.postValue(AlertDialog.Notification("Success!","Delete CV complete."))
					_wasDelete.postValue(true)
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
	public fun requestViewPDF(idCV:String?){
		if(idCV==null||idCV==""){
			_error.postValue(AlertDialog.Error("Error!","System error"))
			return;
		}
		_isLoading.postValue(true)
		Thread {
			requestViewPDFAPI(idCV)
		}.start()
	}
	
	private fun requestViewPDFAPI(idCV:String?){
		try{
			_viewPDFAction.postValue(false)
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.post(
					url =  API.getBaseUrl() + "/UserProfile/OwnerRequestViewPDFCV",
					cookies = mapOf("auth" to API.getAuth(context)) ,
					data = mapOf(
						"id" to idCV
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
					ownerToken = if(!response.data!!.isNull("owner_token"))response.data!!.getString("owner_token") else ""
					_viewPDFAction.postValue(true)
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