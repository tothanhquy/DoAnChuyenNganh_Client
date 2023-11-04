package com.example.tcapp.model.team_profile



class TeamProfileModels {
	companion object{
		val MAX_LENGTH_OF_MEMBERS_CONTAINER_VIEW = 5;
		
	}
	class MyTeamItem{
		public var teamName:String?=null;
		public var teamId:String?=null;
		public var teamAvatar:String?=null;
		public var leaderName:String?=null;
		public var leaderId:String?=null;
		public var leaderAvatar:String?=null;
		public var memberNumber:Int=0;
		public var isLeader:Boolean=false;
		constructor(
			teamName:String?,
			teamId:String?,
			teamAvatar:String?,
			leaderName:String?,
			leaderId:String?,
			leaderAvatar:String?,
			memberNumber:Int=1,
			isLeader:Boolean=false,
		){
			this.teamName=teamName;
			this.teamId=teamId;
			this.teamAvatar=teamAvatar;
			this.leaderName=leaderName;
			this.leaderId=leaderId;
			this.leaderAvatar=leaderAvatar;
			this.memberNumber=memberNumber;
			this.isLeader=isLeader;
		}
	}
	
	class TeamsOfLeaderItem{
		public var teamName:String?=null;
		public var teamId:String?=null;
		public var teamAvatar:String?=null;
		constructor(
			teamId:String?,
			teamName:String?,
			teamAvatar:String?
		){
			this.teamName=teamName;
			this.teamId=teamId;
			this.teamAvatar=teamAvatar;
		}
	}
	class TeamProfileMembers{
		public var name:String?=null;
		public var avatar:String?=null;
		public var otherNumber:Int=0;
		constructor(name:String?,avatar:String?){
			this.name = name;
			this.avatar = avatar;
			this.otherNumber  = 0;
		}
		constructor(otherNumber:Int){
			this.name = null;
			this.avatar = null;
			this.otherNumber  = otherNumber;
		}
	}
	
	class TeamProfileSkills{
		public var id:String?=null;
		public var name:String?=null;
		public var image:String?=null;
		public var number:Int=0;
		constructor(id:String?,name:String?,image:String?,number:Int){
			this.id = id;
			this.name = name;
			this.image = image;
			this.number  = number;
		}
	}
	class Member{
		public var id:String?=null;
		public var name:String?=null;
		public var avatar:String?=null;
		public var isLeader:Boolean= false;
		constructor(id:String?,name:String?,avatar:String?,isLeader:Boolean=false){
			this.id = id;
			this.name = name;
			this.avatar = avatar;
			this.isLeader  = isLeader;
		}
	}
	class TeamProfile{
		var teamId:String? = null;
		var teamName:String? = null;
		var teamAvatar:String? = null;
		var leaderId:String? = null;
		var leaderName:String? = null;
		var leaderAvatar:String? = null;
		var maxim:String? = null;
		var description:String? = null;
		var internalInfo:String? = null;
		var relationship:String? = null;
		var members:ArrayList<TeamProfileMembers> = arrayListOf();
		var skills:ArrayList<TeamProfileSkills> = arrayListOf();
		public var teamChanelChatId:String? = null;
		constructor(){}
	}
	class TeamProfileMembersList{
		public var members:ArrayList<Member>?=null;
		public var isLeader:Boolean=false;
		constructor()
	}
}