package com.example.tcapp.model.chanel_chat



class ChanelChatModels {
	companion object{
	
	}
	enum class Type{
		Friend, Team, Group
	};
	class ChanelChat{
		public lateinit var type:Type;
		public var id:String?=null;
		public var name:String?=null;
		public var avatar:String?=null;
		public var lastTimeAction:Long=0;
		public var lastMessageTime:Long=0;
		public var lastMessageContent:String?=null;
		public var numberOfNewMessages:Long=0;
		public var isLoadAvatar:Boolean=false;//for adapter

		constructor(
			type:Type,
			id:String?,
			name:String?,
			avatar:String?,
			lastTimeAction:Long=0,
			lastMessageTime:Long=0,
			lastMessageContent:String?,
			numberOfNewMessages:Long=0,
		){
			this.type = type;
			this.id = id;
			this.name = name;
			this.avatar = avatar;
			this.lastMessageContent = lastMessageContent;
			this.lastMessageTime = lastMessageTime;
			this.lastTimeAction = lastTimeAction;
			this.numberOfNewMessages = numberOfNewMessages;
		}
	}
	class ChanelChatDetails{
		public lateinit var type:Type;
		public var id:String?=null;
		public var name:String?=null;
		public var avatar:String?=null;
		public var isGroupOwner:Boolean=false;
		public var teamId:String?=null;
		public var friendId:String?=null;
		public var accountId:String?=null;
		public var members:ArrayList<Member>? = ArrayList();
		public var lastTimeMemberSeen:ArrayList<LastTimeMemberSeen>? = ArrayList();
		constructor(
			type:Type,
			id:String?=null,
			name:String?=null,
			avatar:String?=null,
			isGroupOwner:Boolean=false,
			teamId:String?=null,
			friendId:String?=null,
			accountId:String?=null,
			members:ArrayList<Member>? = ArrayList(),
			lastTimeMemberSeen:ArrayList<LastTimeMemberSeen>? = ArrayList()
		){
			this.type = type;
			this.id = id;
			this.name = name;
			this.avatar = avatar;
			this.isGroupOwner=isGroupOwner;
			this.teamId=teamId;
			this.friendId=friendId;
			this.accountId=accountId;
			this.members=members;
			this.lastTimeMemberSeen=lastTimeMemberSeen;
		}
	}
	class Member{
		public var id:String?=null;
		public var name:String?=null;
		public var avatar:String?=null;
		constructor(
			id:String?,
			name:String?,
			avatar:String?,
		){
			this.id = id;
			this.name = name;
			this.avatar = avatar;
		}
	}
	class InsertMemberAdapterItem{
		public var id:String?=null;
		public var name:String?=null;
		public var avatar:String?=null;
		public var wasExist:Boolean = false;
		public var isCheck:Boolean = false;
		public var isLoadImage:Boolean = false;
		constructor(
			id:String?,
			name:String?,
			avatar:String?,
			wasExist:Boolean=false
		){
			this.id = id;
			this.name = name;
			this.avatar = avatar;
			this.wasExist = wasExist;
		}
	}
	class LastTimeMemberSeen{
		public var userId:String?=null;
		public var messageId:String?=null;
		constructor(
			userId:String?,
			messageId:String?,
		){
			this.userId = userId;
			this.messageId = messageId;
		}
	}
	class ChanelChatMembersList{
		public lateinit var type:Type;
		public var isGroupOwner:Boolean=false;
		public var accountId:String?=null;
		public var members:ArrayList<Member>? = ArrayList();
		constructor(
			type:Type,
			isGroupOwner:Boolean=false,
			accountId:String?=null,
			members:ArrayList<Member>? = ArrayList(),
		){
			this.type = type;
			this.isGroupOwner=isGroupOwner;
			this.accountId=accountId;
			this.members=members;
		}
	}
	class LastNewMessageSocket{
		public var content:String?=null;
		public var time:Long=0L;
		public var idChanelChat:String?=null;
		public var idReceiveUser:String?=null;
		public var numberOfNewMessages:Long=0;
		constructor(
			content: String?,
			time:Long=0L,
			idChanelChat: String?,
			idReceiveUser: String?,
			numberOfNewMessages: Long
		) {
			this.content = content
			this.time = time
			this.idChanelChat = idChanelChat
			this.idReceiveUser = idReceiveUser
			this.numberOfNewMessages = numberOfNewMessages
		}
		constructor()
	}
	class UserSeenSocket{
		public var idChanelChat:String?=null;
		public var idUserSeen:String?=null;
		public var idMessage:String?=null;
		constructor(idChanelChat: String?, idUserSeen: String?, idMessage: String?) {
			this.idChanelChat = idChanelChat
			this.idUserSeen = idUserSeen
			this.idMessage = idMessage
		}

		constructor()


	}
}