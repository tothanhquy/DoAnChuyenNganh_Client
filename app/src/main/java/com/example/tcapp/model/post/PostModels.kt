package com.example.tcapp.model.post

import com.example.tcapp.model.request.RequestModels


class PostModels {
	companion object Static{
		fun convertRelationshipToString(relationship : Relationship):String{
			return if(relationship==Relationship.OWNER) "owner" else "guest";
		}
		fun convertStringToRelationship(relationship : String?):Relationship{
			return if(relationship=="owner")  Relationship.OWNER else  Relationship.GUEST;
		}
		fun convertAuthorTypeToString(authorType : AuthorType):String{
			return when (authorType) {
				AuthorType.TEAM -> "team"
				AuthorType.PROJECT -> "project"
				else -> "user"
			};
		}
		fun convertStringToAuthorType(authorType : String?):AuthorType{
			return when (authorType) {
				"team"-> AuthorType.TEAM
				"project"-> AuthorType.PROJECT
				else -> AuthorType.USER
			};
		}
		fun convertCreatorToString(relationship : CreatorType):String{
			return when (relationship) {
				CreatorType.PROJECT -> "project"
				CreatorType.TEAM -> "team"
				else -> "user"
			};
		}
		fun convertStringToCreator(relationship : String?):CreatorType{
			return when (relationship) {
				"project" -> CreatorType.PROJECT
				"team" -> CreatorType.TEAM
				else -> CreatorType.USER
			};
		}
		fun convertPostUpdateInteractResponseStatusToString(status : PostUpdateInteractResponseStatus):String{
			return when (status) {
				PostUpdateInteractResponseStatus.SAVED -> "saved"
				PostUpdateInteractResponseStatus.UNSAVED -> "unsaved"
				PostUpdateInteractResponseStatus.LIKED -> "liked"
				PostUpdateInteractResponseStatus.UNLIKED -> "unliked"
				PostUpdateInteractResponseStatus.FOLLOWED -> "followed"
				else -> "unfollowed"
			};
		}
		fun convertStringToPostUpdateInteractResponseStatus(status : String?):PostUpdateInteractResponseStatus{
			return when (status) {
				"saved" -> PostUpdateInteractResponseStatus.SAVED
				"unsaved" -> PostUpdateInteractResponseStatus.UNSAVED
				"liked" -> PostUpdateInteractResponseStatus.LIKED
				"unliked" -> PostUpdateInteractResponseStatus.UNLIKED
				"followed" -> PostUpdateInteractResponseStatus.FOLLOWED
				else -> PostUpdateInteractResponseStatus.UNFOLLOWED
			};
		}
		fun convertUserUpdateInteractStatusToString(status : UserUpdateInteractStatus):String{
			return when (status) {
				UserUpdateInteractStatus.FOLLOW -> "follow"
				UserUpdateInteractStatus.SAVE -> "save"
				else -> "like"
			};
		}
		fun convertStringToUserUpdateInteractStatus(status : String?):UserUpdateInteractStatus{
			return when (status) {
				"follow"-> UserUpdateInteractStatus.FOLLOW
				"save"-> UserUpdateInteractStatus.SAVE
				else -> UserUpdateInteractStatus.LIKE
			};
		}
		fun convertFilterToString(filter : Filter):String{
			return when(filter){
				Filter.PROJECT-> {
					"project";
				}
				Filter.TEAM-> {
					"team";
				}
				Filter.USER -> {
					"user";
				}
				Filter.USER_LIKED -> {
					"user_liked";
				}
				Filter.USER_SAVED -> {
					"user_saved";
				}
				Filter.USER_FOLLOWED -> {
					"user_followed";
				}
				else -> {
					"guest";
				}
			}
		}
		fun convertStringToFilter(filter : String?):Filter{
			return when (filter) {

				"project" -> {
					Filter.PROJECT;
				}
				"team" -> {
					Filter.TEAM;
				}
				"user" -> {
					Filter.USER;
				}
				"user_liked" -> {
					Filter.USER_LIKED;
				}
				"user_saved" -> {
					Filter.USER_SAVED;
				}
				"user_followed" -> {
					Filter.USER_FOLLOWED;
				}
				else -> {
					Filter.GUEST;
				}
			}
		}
	}
	enum class CreatorType{
		TEAM,USER,PROJECT
	};
	enum class AuthorType{
		TEAM,USER,PROJECT
	};
	enum class Filter{
		TEAM,GUEST,USER,PROJECT,USER_SAVED,USER_LIKED,USER_FOLLOWED
	};
	enum class Relationship{
		OWNER,GUEST
	};
	enum class PostUpdateInteractResponseStatus{
		FOLLOWED,UNFOLLOWED,SAVED,UNSAVED,LIKED,UNLIKED
	}
	enum class UserUpdateInteractStatus{
		SAVE,LIKE,FOLLOW
	};
	
	class PostListItem{
		public var authorAvatar:String?="";
		public var authorName:String?="";
		public var authorId:String?="";
		public var authorType:AuthorType=AuthorType.USER;
		public var isOwner:Boolean=false;
		public var postId:String?="";
		public var postTime:Long=0L;
		public var lastEditTime:Long=0L;
		public var content:String?="";
		public var images:ArrayList<String>?= arrayListOf();
		public var isActive:Boolean=true;
		public var relationship:Relationship=Relationship.GUEST;
		public var wasSaved:Boolean=false;
		public var savedTime:Long=0L;
		public var likeNumber:Long=0L;
		public var wasLiked:Boolean=false;
		public var likedTime:Long=0L;
		public var wasFollowed:Boolean=false;
		public var followedTime:Long=0L;
		public var commentsNumber:Long=0L;
		//for adapter
		var wasLoaded:Boolean=false;
		constructor()
		constructor(
			authorAvatar: String?,
			authorName: String?,
			authorId: String?,
			authorType: AuthorType,
			isOwner: Boolean,
			postId: String?,
			postTime: Long,
			lastEditTime: Long,
			content: String?,
			images: ArrayList<String>?,
			isActive: Boolean,
			relationship: Relationship,
			wasSaved: Boolean,
			savedTime: Long,
			likeNumber: Long,
			wasLiked: Boolean,
			likedTime: Long,
			wasFollowed: Boolean,
			followedTime: Long,
			commentsNumber: Long,
			wasLoaded: Boolean=false
		) {
			this.authorAvatar = authorAvatar
			this.authorName = authorName
			this.authorId = authorId
			this.authorType = authorType
			this.isOwner = isOwner
			this.postId = postId
			this.postTime = postTime
			this.lastEditTime = lastEditTime
			this.content = content
			this.images = images
			this.isActive = isActive
			this.relationship = relationship
			this.wasSaved = wasSaved
			this.savedTime = savedTime
			this.likeNumber = likeNumber
			this.wasLiked = wasLiked
			this.likedTime = likedTime
			this.wasFollowed = wasFollowed
			this.followedTime = followedTime
			this.commentsNumber = commentsNumber
			this.wasLoaded = wasLoaded
		};
		constructor(
			crude:PostListItemCrude
		) {
			this.authorAvatar = crude.authorAvatar
			this.authorName = crude.authorName
			this.authorId = crude.authorId
			this.authorType = convertStringToAuthorType(crude.authorType)
			this.isOwner = crude.isOwner
			this.postId = crude.postId
			this.postTime = crude.postTime
			this.lastEditTime = crude.lastEditTime
			this.content = crude.content
			this.images = crude.images
			this.isActive = crude.isActive
			this.relationship = convertStringToRelationship(crude.relationship)
			this.wasSaved = crude.wasSaved
			this.savedTime = crude.savedTime
			this.likeNumber = crude.likeNumber
			this.wasLiked = crude.wasLiked
			this.likedTime = crude.likedTime
			this.wasFollowed = crude.wasFollowed
			this.followedTime = crude.followedTime
			this.commentsNumber = crude.commentsNumber
			this.wasLoaded = false
		};

	}
	class PostListItemCrude{
		public var authorAvatar:String?="";
		public var authorName:String?="";
		public var authorId:String?="";
		public var authorType:String?="";
		public var isOwner:Boolean=false;
		public var postId:String?="";
		public var postTime:Long=0L;
		public var lastEditTime:Long=0L;
		public var content:String?="";
		public var images:ArrayList<String>?= arrayListOf();
		public var isActive:Boolean=true;
		public var relationship:String="";
		public var wasSaved:Boolean=false;
		public var savedTime:Long=0L;
		public var likeNumber:Long=0L;
		public var wasLiked:Boolean=false;
		public var likedTime:Long=0L;
		public var wasFollowed:Boolean=false;
		public var followedTime:Long=0L;
		public var commentsNumber:Long=0L;
		constructor()
		constructor(
			authorAvatar: String?,
			authorName: String?,
			authorId: String?,
			authorType: String?,
			isOwner: Boolean,
			postId: String?,
			postTime: Long,
			lastEditTime: Long,
			content: String?,
			images: ArrayList<String>?,
			isActive: Boolean,
			relationship: String,
			wasSaved: Boolean,
			savedTime: Long,
			likeNumber: Long,
			wasLiked: Boolean,
			likedTime: Long,
			wasFollowed: Boolean,
			followedTime: Long,
			commentsNumber: Long
		) {
			this.authorAvatar = authorAvatar
			this.authorName = authorName
			this.authorId = authorId
			this.authorType = authorType
			this.isOwner = isOwner
			this.postId = postId
			this.postTime = postTime
			this.lastEditTime = lastEditTime
			this.content = content
			this.images = images
			this.isActive = isActive
			this.relationship = relationship
			this.wasSaved = wasSaved
			this.savedTime = savedTime
			this.likeNumber = likeNumber
			this.wasLiked = wasLiked
			this.likedTime = likedTime
			this.wasFollowed = wasFollowed
			this.followedTime = followedTime
			this.commentsNumber = commentsNumber
		}


	}
	class PostUpdateInteractResponse{
		public var isComplete:Boolean=false;
		public var status:PostUpdateInteractResponseStatus=PostUpdateInteractResponseStatus.LIKED;
		public var totalNumber:Long=0L;
		constructor(){}
		constructor(
			isComplete: Boolean,
			status: PostUpdateInteractResponseStatus,
			totalNumber: Long
		) {
			this.isComplete = isComplete
			this.status = status
			this.totalNumber = totalNumber
		}
		constructor(
			crude:PostUpdateInteractResponseCrude
		) {
			this.isComplete = crude.isComplete
			this.status = convertStringToPostUpdateInteractResponseStatus(crude.status)
			this.totalNumber = crude.totalNumber
		}
	}
	class PostUpdateInteractResponseCrude{
		public var isComplete:Boolean=false;
		public var status:String?="";
		public var totalNumber:Long=0L;
		constructor(){}
		constructor(isComplete: Boolean, status: String?, totalNumber: Long) {
			this.isComplete = isComplete
			this.status = status
			this.totalNumber = totalNumber
		}

	}
	class PostsList{
		public var isFinish:Boolean=false;
		public var isActionable:Boolean=false;
		public var timePrevious:Long = 0;
		public var posts:ArrayList<PostListItem> = arrayListOf();
		constructor(){}
		constructor(
			isFinish: Boolean,
			isActionable: Boolean,
			timePrevious: Long,
			posts: ArrayList<PostListItem>
		) {
			this.isFinish = isFinish
			this.isActionable = isActionable
			this.timePrevious = timePrevious
			this.posts = posts
		}
		constructor(
			crude: PostsListCrude
		) {
			this.isFinish = crude.isFinish
			this.isActionable = crude.isActionable
			this.timePrevious = crude.timePrevious
			this.posts = ArrayList(crude.posts.map { PostListItem(it) })
		}
	}
	class PostsListCrude{
		public var isFinish:Boolean=false;
		public var isActionable:Boolean=false;
		public var timePrevious:Long = 0;
		public var posts:ArrayList<PostListItemCrude> = arrayListOf();
		constructor(){}
		constructor(
			isFinish: Boolean,
			isActionable: Boolean,
			timePrevious: Long,
			posts: ArrayList<PostListItemCrude>
		) {
			this.isFinish = isFinish
			this.isActionable = isActionable
			this.timePrevious = timePrevious
			this.posts = posts
		}

	}
	class PostOwnerDetail{
		public var authorAvatar:String?="";
		public var authorName:String?="";
		public var authorId:String?="";
		public var postId:String?="";
		public var authorType:AuthorType=AuthorType.USER;
		public var postTime:Long=0L;
		public var lastEditTime:Long=0L;
		public var content:String?="";
		public var images:ArrayList<String?>?=arrayListOf();
		public var isActive:Boolean=true;
		public var categoryKeywordsId:ArrayList<String?>?=arrayListOf();
		constructor(){}
		constructor(
			authorAvatar: String?,
			authorName: String?,
			authorId: String?,
			postId: String?,
			authorType: AuthorType,
			postTime: Long,
			lastEditTime: Long,
			content: String?,
			images: ArrayList<String?>?,
			isActive: Boolean,
			categoryKeywordsId: ArrayList<String?>?
		) {
			this.authorAvatar = authorAvatar
			this.authorName = authorName
			this.authorId = authorId
			this.postId = postId
			this.authorType = authorType
			this.postTime = postTime
			this.lastEditTime = lastEditTime
			this.content = content
			this.images = images
			this.isActive = isActive
			this.categoryKeywordsId = categoryKeywordsId
		}
		constructor(
			crude:PostOwnerDetailCrude
		) {
			this.authorAvatar = crude.authorAvatar
			this.authorName = crude.authorName
			this.authorId = crude.authorId
			this.postId = crude.postId
			this.authorType = convertStringToAuthorType(crude.authorType)
			this.postTime = crude.postTime
			this.lastEditTime = crude.lastEditTime
			this.content = crude.content
			this.images = crude.images
			this.isActive = crude.isActive
			this.categoryKeywordsId = crude.categoryKeywordsId
		}
	}
	class PostOwnerDetailCrude{
		public var authorAvatar:String?="";
		public var authorName:String?="";
		public var authorId:String?="";
		public var postId:String?="";
		public var authorType:String="";
		public var postTime:Long=0L;
		public var lastEditTime:Long=0L;
		public var content:String?="";
		public var images:ArrayList<String?>?=arrayListOf();
		public var isActive:Boolean=true;
		public var categoryKeywordsId:ArrayList<String?>?=arrayListOf();
		constructor(){}
		constructor(
			authorAvatar: String?,
			authorName: String?,
			authorId: String?,
			postId: String?,
			authorType: String,
			postTime: Long,
			lastEditTime: Long,
			content: String?,
			images: ArrayList<String?>?,
			isActive: Boolean,
			categoryKeywordsId: ArrayList<String?>?
		) {
			this.authorAvatar = authorAvatar
			this.authorName = authorName
			this.authorId = authorId
			this.postId = postId
			this.authorType = authorType
			this.postTime = postTime
			this.lastEditTime = lastEditTime
			this.content = content
			this.images = images
			this.isActive = isActive
			this.categoryKeywordsId = categoryKeywordsId
		}

	}
}