package com.example.tcapp.api

import android.content.Context
import android.webkit.MimeTypeMap
import com.example.tcapp.core.LocalData
import khttp.responses.Response
import okhttp3.*
import org.json.JSONObject
import java.io.File


const val AUTH_LOCAL_DATA_NAME = "auth"


class API {
	class ResponseAPI{
		var code:Int;
		var status:String?;
		var data:JSONObject?;
		var error:String?;
		constructor(code:Int?,status:String?,data:JSONObject?,error:String?){
			this.code = code?:1;
			this.status = status?:"Fail";
			this.data = data;
			this.error = error?:"client_system_error";
		}
	}
	companion object{
		fun getBaseUrl():String { return "http://192.168.5.159:3000"}
		
		fun getAuth(context : Context):String {
			val localData = LocalData(context);
			return localData.getString(
				AUTH_LOCAL_DATA_NAME
			) ?: "";
		}
		private fun setAuth(context : Context,response : Response){
//			println("asd"+response.cookies)
			if(response.cookies["auth"]!=null){
				val auth = response.cookies["auth"]
				val localData = LocalData(context);
				localData.setString(
					AUTH_LOCAL_DATA_NAME,auth
				);
			}
		}
		
		fun getResponse(context: Context ,response : Response):ResponseAPI{
			try {
				println(response.text)
				if(response.text==="error"){
					return ResponseAPI(404,"Fail",null,"System Error")
				}
				setAuth(context,response)
				val obj : JSONObject = response.jsonObject
				return ResponseAPI(
					if(!obj.isNull("code"))obj.getInt("code")else null,
					if(!obj.isNull("status"))obj.getString("status")else null,
					if(!obj.isNull("data"))obj.getJSONObject("data") else null,
					if(!obj.isNull("error"))obj.getString("error")else null)
			}catch(err:Exception){
				println(err.toString())
				return return ResponseAPI(1,"Fail",null,"system_error")
			}
		}
		
		fun getMimeType(file: File?):String?{
			if(file==null)return null
			return MimeTypeMap.getSingleton().getMimeTypeFromExtension(file?.extension)
		}
		
		fun requestPost(context : Context,url:String,requestBody : RequestBody):ResponseAPI{
			try{
				val requestOj : Request = Request.Builder()
					.url(getBaseUrl() + url)
					.header("Cookie", "auth="+getAuth(context))
					.post(requestBody)
					.build()
				
//				val client = OkHttpClient().cookieJar()
//				val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(applicationContext))

				val client = OkHttpClient.Builder()
//					.cookieJar(MyCookieJar())
					.build()
				
				val response : okhttp3.Response = client.newCall(requestOj).execute()
				val obj : JSONObject = JSONObject(response.body()!!.string())
				return ResponseAPI(
					if(!obj.isNull("code"))obj.getInt("code")else null,
					if(!obj.isNull("status"))obj.getString("status")else null,
					if(!obj.isNull("data"))obj.getJSONObject("data") else null,
					if(!obj.isNull("error"))obj.getString("error")else null)
					
			}catch(err:Exception){
				println(err.toString())
				return return ResponseAPI(1,"Fail",null,"system_error")
			}
		}
		
		class MyCookieJar : CookieJar {
			
			private var cookies : List<Cookie>? = null
			override fun saveFromResponse(
				url : HttpUrl? ,
				cookies : List<Cookie>?
			) {
				this.cookies = cookies
				
			}
			
			override fun loadForRequest(url : HttpUrl?) : List<Cookie> {
				return listOf()
				return cookies ?: ArrayList()
			}
		}
	
	}
	
}