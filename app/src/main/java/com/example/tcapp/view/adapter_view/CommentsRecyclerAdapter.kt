package com.example.tcapp.view.adapter_view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.core.Genaral.Static.getDateTimeByUTC
import com.example.tcapp.core.Genaral.Static.setPostImage
import com.example.tcapp.core.Genaral.Static.setPostImageWithPlaceholder
import com.example.tcapp.model.comment.CommentModels
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class CommentsRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<CommentModels.Comment>
	) : RecyclerView.Adapter<CommentsRecyclerAdapter.ViewHolder>() {
	
	public var isActionale:Boolean = false;
	public var postId:String? = null;
	public var isPostAuthor:Boolean = false;

	private lateinit var callbackLongTouchComment:(ViewHolder,String?,String?)->Unit
	public fun setCallbackLongTouchComment(a:(ViewHolder,String?,String?)->Unit){
		callbackLongTouchComment = a
	}
	private lateinit var callbackReplyComment:(String?,String?)->Unit
	public fun setCallbackReplyComment(a:(String?,String?)->Unit){
		callbackReplyComment = a
	}
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	private lateinit var callbackOfUserInteractComment:(ViewHolder,String,CommentModels.CommentUpdateInteractRequestStatus)->Unit
	public fun setCallbackOfUserInteractComment(a:(ViewHolder,String,CommentModels.CommentUpdateInteractRequestStatus)->Unit){
		callbackOfUserInteractComment = a
	}
	private lateinit var callbackLoadMore:(String?,Long)->Unit
	public fun setCallbackLoadMore(a:(String?,Long)->Unit){
		callbackLoadMore = a
	}

	fun setInitList(list:ArrayList<CommentModels.Comment>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addItems(list:ArrayList<CommentModels.Comment>,isCreate:Boolean=false){
		var isReset=false;
		var firstResetPosition = itemCount;
		list.forEach {
			if(it.postId==this.postId&&itemList.indexOfFirst { a->a.commentId==it.commentId }==-1){
				if(it.replyId==null||it.replyId==""){
					//level 0
					if(isCreate){
						//add first
						itemList.add(0,it);
						isReset=true;
					}else{
						//add last
						itemList.add(it)
					}
				}else{
					val lastBrotherPosition = itemList.indexOfLast { a->a.replyId==it.replyId&&it.time>a.time }
					if(lastBrotherPosition==-1){
						//not brother
						val parentReplyPosition = itemList.indexOfFirst { a->a.commentId==it.replyId }
						if(parentReplyPosition==-1){
							//miss parent - not work
						}else{
							itemList.add(parentReplyPosition+1,it)
							firstResetPosition=parentReplyPosition+1;
						}
					}else{
						itemList.add(lastBrotherPosition+1,it)
						if(firstResetPosition>lastBrotherPosition+1){
							firstResetPosition=lastBrotherPosition+1;
						}
					}
				}
			}
		}
		if(isReset){
			this.notifyDataSetChanged()
		}else if(firstResetPosition!=itemCount){
			this.notifyItemRangeChanged(firstResetPosition,itemCount-1)
		}
	}
	
	fun updateInteractStatus(holder: ViewHolder,commentId:String,status:CommentModels.CommentUpdateInteractResponse){
		val position = itemList.indexOfFirst { it.commentId==commentId };
		if(position!=-1){
			when (status.status) {
				CommentModels.CommentUpdateInteractResponseStatus.LIKED -> {
					itemList[position].wasLike=true;
					itemList[position].likeNumber = status.totalNumber
				}
				CommentModels.CommentUpdateInteractResponseStatus.UNLIKED -> {
					itemList[position].wasLike=false;
					itemList[position].likeNumber = status.totalNumber
				}
				else -> itemList[position].wasDeleted=true;
			}
			setInteractStatus(holder,itemList[position]);
		}
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar:ImageView = itemView.findViewById(R.id.componentCommentsItemAvatar)
		val name:TextView = itemView.findViewById(R.id.componentCommentsItemName)
		val time: TextView = itemView.findViewById(R.id.componentCommentsItemTime)
		val content:TextView = itemView.findViewById(R.id.componentCommentsItemContent)

		val likeNumber: TextView = itemView.findViewById(R.id.componentCommentsItemLikeNumber)
		val likeIcon:ImageView = itemView.findViewById(R.id.componentCommentsItemLikeIcon)
		val replyNumber: TextView = itemView.findViewById(R.id.componentCommentsItemReplyNumber)
		val replyButton: TextView = itemView.findViewById(R.id.componentCommentsItemReplyButton)
		val loadMore: TextView = itemView.findViewById(R.id.componentCommentsItemLoadMore)

		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_comments_item, parent, false)
		return ViewHolder(itemView)
	}
	private fun getCommentPaddingLeft(root:Int=30,level:Int=0):Int{
		if(level<0)return 0;
		if(level<=3)return root*level;
		var resPadding:Int=root*level;
		var rootRound:Int = root;
		for(i in 4 .. level){
			rootRound = (rootRound*2/3)
			resPadding+rootRound;
		}
		return resPadding;
	}
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];

		try{
			//set padding
			var isFarBeforeItem:Boolean = position!=0//not first
			if(isFarBeforeItem){
				//parent or brother
				isFarBeforeItem=itemList[position-1].commentId!=item.replyId&&itemList[position-1].replyId!=item.replyId
			}

			val paddingTop = if(isFarBeforeItem)Genaral.convertDpToPx(context,10F).toInt() else 0;
			val paddingLeft = Genaral.convertDpToPx(context,getCommentPaddingLeft(50,item.level).toFloat()).toInt()
			holder.item.setPadding(paddingLeft,paddingTop,holder.item.paddingRight,holder.item.paddingBottom);

			Genaral.setUserAvatarImage(context , item.authorAvatar , holder.avatar)
			holder.name.text = item.authorName;
			holder.name.setOnClickListener {
				callbackOfViewUser(item.authorId!!);
			}
			holder.avatar.setOnClickListener {
				callbackOfViewUser(item.authorId!!);
			}
			holder.time.text = Genaral.getSpaceTimeWithNow(item.time);

			holder.likeIcon.setOnClickListener {
				callbackOfUserInteractComment(holder,item.commentId!!,CommentModels.CommentUpdateInteractRequestStatus.LIKE);
			}
			holder.replyButton.setOnClickListener {
				callbackReplyComment(item.commentId!!,item.content);
			}

			if(item.replyNumber!=0){
				holder.replyNumber.setOnClickListener {
					callbackLoadMore(item.commentId,item.time);
				}
			}

			if(item.isLoadMore){
				holder.loadMore.visibility=View.VISIBLE
				holder.loadMore.setOnClickListener {
					callbackLoadMore(item.replyId,item.time)
					item.isLoadMore=false
					holder.loadMore.visibility=View.GONE
				}
			}else{
				holder.loadMore.visibility=View.GONE
			}

			setInteractStatus(holder,item);

		}catch(e:Exception){}
	}
	
	private fun setInteractStatus(holder: ViewHolder, item:CommentModels.Comment){
		holder.replyNumber.text = ""+if(item.replyNumber>0L)""+item.replyNumber+" Bình luận" else ""
		holder.likeNumber.text = ""+if(item.likeNumber>0L)(if(item.likeNumber==1)""+item.likeNumber+" Like" else ""+item.likeNumber+" Likes") else ""
		if(item.wasLike){
			holder.likeIcon.setImageResource(R.drawable.like_icon);
		}else{
			holder.likeIcon.setImageResource(R.drawable.like_blank_icon);
		}
		if(item.wasDeleted){
			holder.content.text="Bình luận đã bị thu hồi."
		}else{
			holder.content.text=item.content
		}
		if(!isActionale||item.wasDeleted){
			holder.likeIcon.visibility = View.GONE;
			holder.replyButton.visibility = View.GONE;
		}else{
			holder.likeIcon.visibility = View.VISIBLE;
			holder.replyButton.visibility = View.VISIBLE;
		}
		if((isPostAuthor||item.isAuthor)&&!item.wasDeleted){
			holder.item.setOnLongClickListener {
				callbackLongTouchComment(holder,item.commentId!!,item.content);
				true
			}
		}else{
			holder.item.setOnLongClickListener(null)
		}
	}
	
	override fun getItemCount() = itemList.size
}