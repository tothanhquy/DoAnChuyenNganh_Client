package com.example.tcapp.viewmodel.category_keyword
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.GeneralModel
import com.example.tcapp.model.HomeModels
import com.example.tcapp.model.project.ProjectModels
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import khttp.structures.files.FileLike
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class CategoryKeywordsViewModel(private val context : Context):ViewModel(){
	private var _allKeywords:MutableLiveData<GeneralModel.Keywords?> = MutableLiveData<GeneralModel.Keywords?>(null)

	public val allKeywords:LiveData<GeneralModel.Keywords?>
		get() = _allKeywords

	public fun getAllKeywords(){
		_isLoading.postValue(true)
		Thread {
			getAllKeywordsAPI()
		}.start()
	}
	
	private fun getAllKeywordsAPI(){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url =  API.getBaseUrl() + "/CategoryKeyword/GetList",
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
					val parseOj = Gson().fromJson(response.data.toString(), ProjectModels.CategoryKeywords::class.java)
					var resOj = GeneralModel.Keywords(ArrayList(parseOj.keywords!!.map{GeneralModel.Keyword(it.id,it.name)}));
					_allKeywords.postValue(resOj)
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