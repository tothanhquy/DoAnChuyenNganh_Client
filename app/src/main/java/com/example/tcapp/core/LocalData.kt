package com.example.tcapp.core

import android.content.Context
import android.content.SharedPreferences

const val PREFS_NAME = "startup_map_local_data";

class LocalData {
	
	private var prefs: SharedPreferences?=null;
	
	constructor(content:Context) {
		this.prefs = content.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	}
	
	
	public fun getString(key:String):String?{
		return prefs?.getString(key, null)
	}
	public fun setString(key:String,value:String?){
		prefs?.edit()?.putString(key,value)?.apply()
	}
//	public fun getInt(key:String):Int?{
//		return prefs?.getInt(key, 0)
//	}
//	public fun setInt(key:String,value:Int){
//		prefs?.edit()?.putInt(key,value)?.apply()
//	}
//	public fun getBoolean(key:String):Boolean?{
//		return prefs?.getBoolean(key, false)
//	}
//	public fun setBoolean(key:String,value:Boolean){
//		prefs?.edit()?.putBoolean(key,value)?.apply()
//	}
	
}