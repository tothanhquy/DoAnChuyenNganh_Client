package com.example.tcapp.viewmodel.search

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tcapp.api.API
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.AlertDialog
import com.example.tcapp.model.search.SearchModels
import com.example.tcapp.viewmodel.ViewModel
import com.google.gson.Gson
import org.json.JSONObject


class SearchViewModel (private val context : Context): ViewModel(){
private var _searchObjects:MutableLiveData<SearchModels.SearchItems?> = MutableLiveData<SearchModels.SearchItems?>(null)

	public val searchObjects:LiveData<SearchModels.SearchItems?>
		get() = _searchObjects
	public fun loadObjects(type:SearchModels.ObjectType,search:String?){
		val typeString = SearchModels.convertObjectTypeToString(type);
		if(isLoading.value==true)return;
		_isLoading.postValue(true)
		Thread {
			loadObjectsAPI(typeString,search)
		}.start()
	}
	private fun loadObjectsAPI(type:String,search:String?){
		try{
			val  response : API.ResponseAPI = API.getResponse(context,
				khttp.get(
					url = API.getBaseUrl() + "/Search/SearchUserTeamProject",
					cookies = mapOf("auth" to API.getAuth(context)),
					params= mapOf(
						"type" to type,
						"search" to (search?:"")
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
		_isLoading.postValue(false)
	}
	private fun  handleResPosts(oj: JSONObject?){
		if(oj!=null){
			val crude = Gson().fromJson(oj.toString(), SearchModels.SearchItemsCrude::class.java)
			_searchObjects.postValue(SearchModels.SearchItems(crude));
		}
	}
}