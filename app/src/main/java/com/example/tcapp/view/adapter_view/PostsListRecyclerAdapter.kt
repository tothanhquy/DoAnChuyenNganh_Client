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
import com.example.tcapp.model.post.PostModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class PostsListRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<PostModels.PostListItem>
	) : RecyclerView.Adapter<PostsListRecyclerAdapter.ViewHolder>() {
	
	public var wasLoaded:Boolean = false;
	public var isFinish:Boolean = false;
	public var timePrevious:Long = 0;
	public var isActionale:Boolean = false;
	
	private lateinit var callbackOfOpenSlideImages:(ArrayList<String>)->Unit
	public fun setCallbackOfOpenSlideImages(a:(ArrayList<String>)->Unit){
		callbackOfOpenSlideImages = a
	}
	
	private lateinit var callbackOfOpenOptions:(Int,String)->Unit
	public fun setCallbackOfOpenOptions(a:(Int,String)->Unit){
		callbackOfOpenOptions = a
	}
	
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	private lateinit var callbackOfViewTeam:(String)->Unit
	public fun setCallbackOfViewTeam(a:(String)->Unit){
		callbackOfViewTeam = a
	}
	private lateinit var callbackOfViewProject:(String)->Unit
	public fun setCallbackOfViewProject(a:(String)->Unit){
		callbackOfViewProject = a
	}
	private lateinit var callbackOfUserInteractPost:(ViewHolder,String,PostModels.UserUpdateInteractStatus)->Unit
	public fun setCallbackOfUserInteractPost(a:(ViewHolder,String,PostModels.UserUpdateInteractStatus)->Unit){
		callbackOfUserInteractPost = a
	}
	private lateinit var callbackOfOpenComment:(String,Boolean)->Unit
	public fun setCallbackOfOpenComment(a:(String,Boolean)->Unit){
		callbackOfOpenComment = a
	}
	
	fun setInitList(list:ArrayList<PostModels.PostListItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addItems(list:ArrayList<PostModels.PostListItem>){
		list.forEach {
			if(itemList.indexOfFirst { a->a.postId==it.postId }==-1){
				itemList.add(it);
//				this.notifyItemChanged(itemCount-1)
			}
		}
		this.notifyDataSetChanged()
	}
	fun updateInteractStatus(holder: ViewHolder,postId:String,status:PostModels.PostUpdateInteractResponse){
		val position = itemList.indexOfFirst { it.postId==postId };
		if(position!=-1){
			println(position)
			when (status.status) {
				PostModels.PostUpdateInteractResponseStatus.LIKED -> {
					itemList[position].wasLiked=true;
					itemList[position].likeNumber = status.totalNumber
				}
				PostModels.PostUpdateInteractResponseStatus.UNLIKED -> {
					itemList[position].wasLiked=false;
					itemList[position].likeNumber = status.totalNumber
				}
				PostModels.PostUpdateInteractResponseStatus.SAVED -> {
					itemList[position].wasSaved=true;
				}
				PostModels.PostUpdateInteractResponseStatus.UNSAVED -> {
					itemList[position].wasSaved=false;
				}
				PostModels.PostUpdateInteractResponseStatus.FOLLOWED -> {
					itemList[position].wasFollowed=true;
				}
				else -> itemList[position].wasFollowed=false;
			}
			setInteractStatus(holder,itemList[position]);
		}
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar:ImageView = itemView.findViewById(R.id.componentListPostsItemAvatar)
		val name:TextView = itemView.findViewById(R.id.componentListPostsItemName)
		val postTime: TextView = itemView.findViewById(R.id.componentListPostsItemTime)
		val inactive: ImageView = itemView.findViewById(R.id.componentListPostsItemInactive)
		val option:ImageView = itemView.findViewById(R.id.componentListPostsItemOption)

		val content:TextView = itemView.findViewById(R.id.componentListPostsItemContent)
		val imagesContainer:ViewGroup = itemView.findViewById(R.id.componentListPostsItemImagesContainer)
		val image1:ImageView = itemView.findViewById(R.id.componentListPostsItemImage1)
		val image2:ImageView = itemView.findViewById(R.id.componentListPostsItemImage2)
		val image3:ImageView = itemView.findViewById(R.id.componentListPostsItemImage3)
		val image4Block:ViewGroup = itemView.findViewById(R.id.componentListPostsItemImage4Block)
		val image4:ImageView = itemView.findViewById(R.id.componentListPostsItemImage4)
		val image4Text:TextView = itemView.findViewById(R.id.componentListPostsItemImage4Text)

		val likeNumber: TextView = itemView.findViewById(R.id.componentListPostsItemLikeNumber)

		val commentNumber: TextView = itemView.findViewById(R.id.componentListPostsItemCommentNumber)
		val interactLine: View = itemView.findViewById(R.id.componentListPostsItemInteractLine)

		val saveContainer: LinearLayout = itemView.findViewById(R.id.componentListPostsItemSaveContainer)
		val saveIcon: ImageView = itemView.findViewById(R.id.componentListPostsItemSaveIcon)
		val saveText: TextView = itemView.findViewById(R.id.componentListPostsItemSaveText)
		val likeContainer: LinearLayout = itemView.findViewById(R.id.componentListPostsItemLikeContainer)
		val likeIcon: ImageView = itemView.findViewById(R.id.componentListPostsItemLikeIcon)
		val likeText: TextView = itemView.findViewById(R.id.componentListPostsItemLikeText)
		val commentContainer: LinearLayout = itemView.findViewById(R.id.componentListPostsItemCommentContainer)
		val followContainer: LinearLayout = itemView.findViewById(R.id.componentListPostsItemFollowContainer)
		val followIcon: ImageView = itemView.findViewById(R.id.componentListPostsItemFollowIcon)
		val followText: TextView = itemView.findViewById(R.id.componentListPostsItemFollowText)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_list_posts_view_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val post = itemList[position];
		try{
			if(!post.wasLoaded){
				val type = when(post.authorType){
					PostModels.AuthorType.PROJECT->"Dự án - "
					PostModels.AuthorType.TEAM->"Team - "
					else -> "Người dùng - "
				}
				if(post.lastEditTime!=0L){
					holder.postTime.text = type+"Sửa đổi gần nhất:\n"+getDateTimeByUTC(post.lastEditTime);
				}else{
					holder.postTime.text = type+getDateTimeByUTC(post.postTime);
				}
				holder.name.text = post.authorName;
				when (post.authorType) {
					PostModels.AuthorType.TEAM -> {
						Genaral.setTeamAvatarImage(context , post.authorAvatar , holder.avatar)
						holder.name.setOnClickListener {
							callbackOfViewTeam(post.authorId!!);
						}
						holder.avatar.setOnClickListener {
							callbackOfViewTeam(post.authorId!!);
						}
					}
					PostModels.AuthorType.PROJECT -> {
						Genaral.setProjectImageWithPlaceholder(context , post.authorAvatar , holder.avatar)
						holder.name.setOnClickListener {
							callbackOfViewProject(post.authorId!!);
						}
						holder.avatar.setOnClickListener {
							callbackOfViewProject(post.authorId!!);
						}
					}
					else -> {
						Genaral.setUserAvatarImage(context , post.authorAvatar , holder.avatar)
						holder.name.setOnClickListener {
							callbackOfViewUser(post.authorId!!);
						}
						holder.avatar.setOnClickListener {
							callbackOfViewUser(post.authorId!!);
						}
					}
				}
				if(!isActionale){
					holder.saveContainer.visibility = View.GONE;
					holder.likeContainer.visibility = View.GONE;
					holder.followContainer.visibility = View.GONE;
					holder.commentContainer.visibility = View.GONE;
					holder.interactLine.visibility = View.GONE;
				}
				if(!isActionale||!post.isOwner){
					holder.option.visibility = View.GONE;
					holder.inactive.visibility = View.GONE;
				}else{
					holder.option.visibility = View.VISIBLE;
					holder.inactive.visibility = if(!post.isActive)View.VISIBLE else View.GONE;
					holder.option.setOnClickListener {
						callbackOfOpenOptions(position,post.postId!!)
					}
				}

				holder.content.text = post.content;
				
				//set image
				holder.image1.visibility = View.GONE;
				holder.image2.visibility = View.GONE;
				holder.image3.visibility = View.GONE;
				holder.image4Block.visibility = View.GONE;
				
				if(post.images==null||post.images!!.size==0) {
					holder.imagesContainer.visibility = View.GONE;
				}
				else{
					if(post.images!!.size>=1){
						holder.image1.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images!![0],holder.image1);
					}
					if(post.images!!.size>=2){
						holder.image2.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images!![1],holder.image2);
					}
					if(post.images!!.size>=3){
						holder.image3.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images!![2],holder.image3);
					}
					if(post.images!!.size>=4){
						holder.image4Block.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images!![3],holder.image4);
						holder.image4Text.text="+"+(post.images!!.size-3)
					}else{
						holder.image4Text.visibility = View.GONE;
					}
					holder.imagesContainer.setOnClickListener {
						callbackOfOpenSlideImages(post.images!!)
					}
				}

				holder.likeContainer.setOnClickListener {
					callbackOfUserInteractPost(holder,post.postId!!,PostModels.UserUpdateInteractStatus.LIKE);
				}
				holder.saveContainer.setOnClickListener {
					callbackOfUserInteractPost(holder,post.postId!!,PostModels.UserUpdateInteractStatus.SAVE);
				}
				holder.followContainer.setOnClickListener {
					callbackOfUserInteractPost(holder,post.postId!!,PostModels.UserUpdateInteractStatus.FOLLOW);
				}
				holder.commentContainer.setOnClickListener {
					callbackOfOpenComment(post.postId!!,post.isOwner);
				}
				holder.commentNumber.setOnClickListener {
					callbackOfOpenComment(post.postId!!,post.isOwner);
				}
				itemList[position].wasLoaded = true;
			}
			setInteractStatus(holder,post);

		}catch(e:Exception){}
	}
	private fun setInteractStatus(holder: ViewHolder, post:PostModels.PostListItem){
		holder.commentNumber.text = ""+if(post.commentsNumber>0L)""+post.commentsNumber+" Bình luận" else ""
		holder.likeNumber.text = ""+if(post.likeNumber>0L)(if(post.likeNumber==1L)""+post.likeNumber+" Like" else ""+post.likeNumber+" Likes") else ""
		if(post.wasLiked){
			holder.likeIcon.setImageResource(R.drawable.like_icon);
			holder.likeText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2))
		}else{
			holder.likeIcon.setImageResource(R.drawable.like_blank_icon);
			holder.likeText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2_dark))
		}
		if(post.wasSaved){
			holder.saveIcon.setImageResource(R.drawable.save_icon);
			holder.saveText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2))
		}else{
			holder.saveIcon.setImageResource(R.drawable.save_blank_icon);
			holder.saveText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2_dark))
		}
		if(post.wasFollowed){
			holder.followIcon.setImageResource(R.drawable.follow_icon);
			holder.followText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2))
		}else{
			holder.followIcon.setImageResource(R.drawable.follow_blank_icon);
			holder.followText.setTextColor(ContextCompat.getColor(context,R.color.theme_color_2_dark))
		}
	}
	
	override fun getItemCount() = itemList.size
}