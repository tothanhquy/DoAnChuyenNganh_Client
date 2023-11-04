package com.example.tcapp.model.user_profile

class UserProfileModels {
	class Info{
		public var id:String?=null;
		public var name:String?=null;
		public var birthYear:Int=0;
		public var maxim:String?=null;
		public var contact:String?=null;
		public var careerTarget:String?=null;
		public var education:String?=null;
		public var workExperience:String?=null;
		public var description:String?=null;
		
		
		constructor() {}
		constructor(
			id : String? ,
			name : String? ,
			birthYear : Int ,
			maxim : String? ,
			contact :String?,
			careerTarget :String?,
			education :String?,
			workExperience :String?,
			description : String?
		) {
			this.id = id
			this.name = name
			this.birthYear = birthYear
			this.maxim = maxim
			this.contact = contact
			this.careerTarget = careerTarget
			this.education = education
			this.workExperience = workExperience
			this.description = description
		}
	}
	class Skill{
		public var id:String?=null;
		public var name:String?=null;
		public var image:String?=null;
		
		constructor(id : String? , name : String? , image : String?) {
			this.id = id
			this.name = name
			this.image = image
		}
		constructor() {}
	}
	class MyCV{
		public var id:String?=null;
		public var name:String?=null;
		public var isActive:Boolean=false;
		
		constructor(id : String? , name : String? , isActive : Boolean) {
			this.id = id
			this.name = name
			this.isActive = isActive
		}
	}
	class GuestCV{
		public var id:String?=null;
		public var name:String?=null;
		
		constructor(id : String?, name : String?) {
			this.id = id
			this.name = name
		}
		constructor(){}
	}
	class MyProfile{
		public var avatar:String? = null;
		public var info:Info? = null;
		public var skills:ArrayList<Skill>? = null;
		public var cvs:ArrayList<MyCV>? = null;
		constructor(){}
		constructor(
			avatar : String ,
			info : Info? ,
			skills : ArrayList<Skill>? ,
			cvs : ArrayList<MyCV>?
		) {
			this.avatar = avatar
			this.info = info
			this.skills = skills
			this.cvs = cvs
		}
		
	}
	class GuestProfile{
		public var relationship:String = "guest";
		public var avatar:String? = null;
		public var info:Info? = null;
		public var skills:ArrayList<Skill>? = null;
		public var cvs:ArrayList<GuestCV>? = null;
		public var friendChanelChatId:String? = null;
		constructor(){}
		constructor(
			relationship : String ,
			avatar : String ,
			info : Info? ,
			skills : ArrayList<Skill>? ,
			cvs : ArrayList<GuestCV>?,
			friendChanelChatId : String
		) {
			this.relationship = relationship
			this.avatar = avatar
			this.info = info
			this.skills = skills
			this.cvs = cvs
			this.friendChanelChatId = friendChanelChatId
		}
		
	}
}