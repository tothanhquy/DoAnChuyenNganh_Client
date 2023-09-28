package com.example.tcapp.model.post

import com.example.tcapp.model.request.RequestModels


class PostModels {
	companion object Static{
		fun convertRelationshipToString(relationship : Relationship):String{
			return if(relationship==Relationship.OWNER) "owner" else "guest";
		}
		fun convertStringToRelationship(relationship : String):Relationship{
			return if(relationship=="owner")  Relationship.OWNER else  Relationship.GUEST;
		}
		fun convertAuthorTypeToString(authorType : AuthorType):String{
			return if(authorType==AuthorType.TEAM) "team" else "user";
		}
		fun convertStringToAuthorType(authorType : String):AuthorType{
			return if(authorType=="team")  AuthorType.TEAM else  AuthorType.USER;
		}
		fun convertStatusUpdateToString(status : StatusUpdate):String{
			return if(status==StatusUpdate.ACTIVE) "active" else "save";
		}
		fun convertStringToStatusUpdate(status : String):StatusUpdate{
			return if(status=="active")  StatusUpdate.ACTIVE else  StatusUpdate.SAVE;
		}
		fun convertFilterToString(filter : Filter):String{
			return when(filter){
				PostModels.Filter.USER_SAVE -> {
					"user_save";
				}
				PostModels.Filter.TEAM-> {
					"team";
				}
				PostModels.Filter.USER_OWN -> {
					"user_own";
				}
				else -> {
					"guest";
				}
			}
		}
		fun convertStringToFilter(filter : String):Filter{
			return when (filter) {
				"user_save" -> {
					PostModels.Filter.USER_SAVE;
				}
				"team" -> {
					PostModels.Filter.TEAM;
				}
				"user_own" -> {
					PostModels.Filter.USER_OWN;
				}
				else -> {
					PostModels.Filter.GUEST;
				}
			}
		}
	}
	enum class Creator{
		LEADER,USER
	};
	enum class AuthorType{
		TEAM,USER
	};
	enum class Filter{
		TEAM,GUEST,USER_OWN,USER_SAVE
	};
	enum class Relationship{
		OWNER,GUEST
	};
	enum class StatusUpdate{
		ACTIVE,SAVE
	};
	
	class Post{
		var postId:String?=null;
		var content:String?=null;
		var authorType:AuthorType = AuthorType.USER;
		var authorId:String?=null;
		var authorName:String?=null;
		var authorAvatar:String?=null;
		var postTime:Long=0;
		var saveTime:Long=0;
		var images:ArrayList<String?> = arrayListOf();
		var isActive:Boolean=true;
		var wasSaved:Boolean=false;
		var relationship:Relationship=Relationship.GUEST;
		//for adapter
		var wasLoaded:Boolean=false;
		constructor();
		constructor(
			postId : String? ,
			content : String? ,
			authorType : AuthorType ,
			authorId : String? ,
			authorName : String? ,
			authorAvatar : String? ,
			postTime : Long ,
			saveTime : Long ,
			images : ArrayList<String?> ,
			isActive : Boolean,
			wasSaved : Boolean,
			relationship:Relationship
		) {
			this.postId = postId
			this.content = content
			this.authorType = authorType
			this.authorId = authorId
			this.authorName = authorName
			this.authorAvatar = authorAvatar
			this.postTime = postTime
			this.saveTime = saveTime
			this.images = images
			this.isActive = isActive
			this.wasSaved = wasSaved
			this.relationship = relationship
		}
	}
	class PostsListStatus{
		public var isFinish:Boolean=false;
		public var isActionable:Boolean=false;
		public var timePrevious:Long = 0;
		constructor(){}
	}
}