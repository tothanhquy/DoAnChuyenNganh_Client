package com.example.tcapp.model.comment

import com.example.tcapp.model.post.PostModels

class CommentModels {
    companion object Static {
        fun convertCommentUpdateInteractResponseStatusToString(status: CommentUpdateInteractResponseStatus): String {
            return when (status) {
                CommentUpdateInteractResponseStatus.LIKED -> "liked"
                CommentUpdateInteractResponseStatus.UNLIKED -> "unliked"
                else -> "deleted"
            };
        }
        fun convertStringToCommentUpdateInteractResponseStatus(status: String?): CommentUpdateInteractResponseStatus {
            return when (status) {
                "liked" -> CommentUpdateInteractResponseStatus.LIKED
                "unliked" -> CommentUpdateInteractResponseStatus.UNLIKED
                else -> CommentUpdateInteractResponseStatus.DELETED
            };
        }

        fun convertCommentUpdateInteractRequestStatusToString(status: CommentUpdateInteractRequestStatus): String {
            return when (status) {
                CommentUpdateInteractRequestStatus.LIKE -> "like"
                else -> "delete"
            };
        }
        fun convertStringToCommentUpdateInteractRequestStatus(status: String?): CommentUpdateInteractRequestStatus {
            return when (status) {
                "like" -> CommentUpdateInteractRequestStatus.LIKE
                else -> CommentUpdateInteractRequestStatus.DELETE
            };
        }
    }
    class Comment{
        public var commentId:String?="";
        public var wasDeleted:Boolean=false;
        public var content:String?="";
        public var postId:String?="";
        public var authorId:String?="";
        public var authorName:String?="";
        public var authorAvatar:String?="";
        public var time:Long=0L;
        public var replyId:String?="";
        public var level:Int=0;
        public var likeNumber:Int=0;
        public var wasLike:Boolean=false;
        public var replyNumber:Int=0;
        public var isLoadMore:Boolean=false;//not load more
        public var isAuthor:Boolean=false;
        constructor(
            commentId:String?="",
            wasDeleted:Boolean=false,
            content:String?="",
            postId:String?="",
            authorId:String?="",
            authorName:String?="",
            authorAvatar:String?="",
            time:Long=0L,
            replyId:String?="",
            level:Int=0,
            likeNumber:Int=0,
            wasLike:Boolean=false,
            replyNumber:Int=0,
            isLoadMore:Boolean=false,
            isAuthor:Boolean=false,
        ){
            this.commentId=commentId;
            this.wasDeleted=wasDeleted;
            this.content=content;
            this.postId=postId;
            this.authorId=authorId;
            this.authorName=authorName;
            this.authorAvatar=authorAvatar;
            this.time=time;
            this.replyId=replyId;
            this.level=level;
            this.likeNumber=likeNumber;
            this.wasLike=wasLike;
            this.replyNumber=replyNumber;
            this.isLoadMore=isLoadMore;
            this.isAuthor=isAuthor;
        }
    }
    class Comments{
        public var comments:ArrayList<Comment>?= arrayListOf();
        public var isActionable:Boolean=false;
        constructor(){}
        constructor(comments: ArrayList<Comment>?, isActionable: Boolean) {
            this.comments = comments
            this.isActionable = isActionable
        }
    }
    class CommentUpdateInteractResponse{
        public var isComplete:Boolean=false;
        public var status:CommentUpdateInteractResponseStatus=CommentUpdateInteractResponseStatus.LIKED;
        public var totalNumber:Int=0;
        constructor(){}
        constructor(
            isComplete: Boolean,
            status: CommentUpdateInteractResponseStatus,
            totalNumber: Int
        ) {
            this.isComplete = isComplete
            this.status = status
            this.totalNumber = totalNumber
        }
        constructor(
            crude:CommentUpdateInteractResponseCrude
        ) {
            this.isComplete = crude.isComplete
            this.status = convertStringToCommentUpdateInteractResponseStatus(crude.status)
            this.totalNumber = crude.totalNumber
        }
    }
    class CommentUpdateInteractResponseCrude{
        public var isComplete:Boolean=false;
        public var status:String?="";
        public var totalNumber:Int=0;
        constructor(){}
        constructor(isComplete: Boolean, status: String?, totalNumber: Int) {
            this.isComplete = isComplete
            this.status = status
            this.totalNumber = totalNumber
        }
    }
    enum class CommentUpdateInteractResponseStatus{
        DELETED,LIKED,UNLIKED
    }
    enum class CommentUpdateInteractRequestStatus{
        DELETE,LIKE
    }
}