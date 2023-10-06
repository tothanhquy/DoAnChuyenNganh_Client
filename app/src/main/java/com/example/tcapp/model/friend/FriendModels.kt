package com.example.tcapp.model.friend



class FriendModels {
	companion object{
	
	}
	enum class RequestMethod{
		SEND,RECEIVE
	};
	enum class ListLayoutStatus{
		REQUESTS_SEND,REQUESTS_RECEIVE,MY_FRIENDS
	};
	class RequestsListStatus{
		public lateinit var method:RequestMethod;
		public var isFinish:Boolean=false;
		public var timePrevious:Long = 0;
		constructor(){}
	}
	class Request{
//		public var requestMethod:RequestMethod=RequestMethod.RECEIVE;
		public var content:String?=null;
		public var userId:String?=null;
		public var userName:String?=null;
		public var userAvatar:String?=null;
		public var time:Long=0;
		constructor(){}
	}
	class RequestsListItem{
		public var id:String?=null;
		public var userName:String?=null;
		public var userId:String?=null;
		public var userAvatar:String?=null;
		public var time:Long=0;
		//in adapter
		public var wasLoadData:Boolean=false;
		constructor(){}
	}
	class MyFriendsListItem{
		public var userId:String?=null;
		public var userName:String?=null;
		public var userAvatar:String?=null;
		constructor(){}
	}
}