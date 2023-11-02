package com.example.tcapp.model.chanel_chat



class MessageModels {
	companion object{
	
	}
	class Message{
		public var id:String?=null;
		public var content:String?=null;
		public var userId:String?=null;
		public var time:Long=0L;
		public var replyContent:String?=null;
		public var replyTime:Long=0;
		public var replyId:String?=null;
		public var isLoaded:Boolean=false;


		constructor(
			id:String?=null,
			content:String?=null,
			userId:String?=null,
			time:Long=0L,
			replyContent:String?=null,
			replyTime:Long=0,
			replyId:String?=null,
		){
			this.id=id
			this.content=content
			this.userId=userId
			this.time=time
			this.replyContent=replyContent
			this.replyTime=replyTime
			this.replyId=replyId
		}
	}
	class Messages{
		public var messages:ArrayList<Message>?=null;
		public var isFinish:Boolean=false;

		constructor(
			messages:ArrayList<Message>?= ArrayList(),
			isFinish:Boolean=false,
		){
			this.messages = messages
			this.isFinish = isFinish
		}
	}
}