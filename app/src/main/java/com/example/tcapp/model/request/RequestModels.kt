package com.example.tcapp.model.request



class RequestModels {
	companion object{
	
	}
	enum class RequestViewer{
		LEADER,USER
	};
	enum class RequestMethod{
		SEND,RECEIVE
	};
	class RequestsListStatus{
		public lateinit var viewer:RequestViewer;
		public lateinit var method:RequestMethod;
		public var isFinish:Boolean=false;
		public var timePrevious:Long = 0;
		constructor(){}
	}
	class Request{
		public var requestViewer:RequestViewer=RequestViewer.USER;
		public var requestMethod:RequestMethod=RequestMethod.RECEIVE;
		public var title:String?=null;
		public var content:String?=null;
		public var isWaiting:Boolean=true;
		public var wasReaded:Boolean=true;
		public var wasResponsed:Boolean=false;
		public var isAgree:Boolean=false;
		public var isImportant:Boolean=false;
		public var userId:String?=null;
		public var userName:String?=null;
		public var userAvatar:String?=null;
		public var teamId:String?=null;
		public var teamName:String?=null;
		public var teamAvatar:String?=null;
		public var teamLeaderId:String?=null;
		public var teamLeaderName:String?=null;
		public var teamLeaderAvatar:String?=null;
		public var requestTime:Long=0;
		public var responseTime:Long=0;
		constructor(){}
	}
	class RequestsListItem{
		public var id:String?=null;
		public var title:String?=null;
		public var isWaiting:Boolean=true;
		public var wasResponsed:Boolean=true;
		public var wasReaded:Boolean=true;
		public var isImportant:Boolean=false;
		public var userName:String?=null;
		public var userAvatar:String?=null;
		public var teamName:String?=null;
		public var teamAvatar:String?=null;
		public var requestTime:Long=0;
		//in adapter
		public var wasLoadData:Boolean=false;
		constructor(){}
	}
}