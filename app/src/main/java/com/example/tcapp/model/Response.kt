package com.example.tcapp.model

class Response {
	data class API(var code:Int,var status:String,var data:Any, var error:String){
	
	}
}