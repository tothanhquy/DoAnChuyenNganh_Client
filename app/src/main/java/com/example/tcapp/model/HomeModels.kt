package com.example.tcapp.model

class HomeModels {
	data class UserData(
		public var name:String="user name",
		public var avatar:String="user avatar",
		public var id:String="",
		public var isVerifyEmail:Boolean=false,
		public var numberNotReadNotifications:Int=0
	){}
}