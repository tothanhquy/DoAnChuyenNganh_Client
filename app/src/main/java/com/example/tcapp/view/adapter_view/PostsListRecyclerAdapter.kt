package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
	private var itemList: ArrayList<PostModels.Post>
	) : RecyclerView.Adapter<PostsListRecyclerAdapter.ViewHolder>() {
	
	public var filter: PostModels.Filter = PostModels.Filter.GUEST;
	public var wasLoaded:Boolean = false;
	public var isFinish:Boolean = false;
	public var timePrevious:Long = 0;
	public var isActionale:Boolean = false;
	
	private lateinit var callbackOfOpenSlideImages:(ArrayList<String?>)->Unit
	public fun setCallbackOfOpenSlideImages(a:(ArrayList<String?>)->Unit){
		callbackOfOpenSlideImages = a
	}
	
	private lateinit var callbackOfOpenOptions:(Int,String , PostModels.Relationship, Boolean, Boolean)->Unit
	public fun setCallbackOfOpenOptions(a:(Int,String, PostModels.Relationship, Boolean, Boolean)->Unit){
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
	
	fun setInitList(list:ArrayList<PostModels.Post>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addItems(list:ArrayList<PostModels.Post>){
		val previousSize = itemList.size;
		itemList.addAll(list)
		this.notifyDataSetChanged()
//		this.notifyItemRangeInserted(previousSize,list.size)
	}
	fun toggleSaveStatus(position : Int){
		itemList[position].wasSaved = !itemList[position].wasSaved;
		this.notifyDataSetChanged()
//		this.notifyItemChanged(position)
	}
	fun toggleActiveStatus(position : Int){
		itemList[position].isActive = !itemList[position].isActive;
		this.notifyDataSetChanged()
//		this.notifyItemChanged(position)
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val postTime: TextView = itemView.findViewById(R.id.componentListPostsItemPostTime)
		val inactive: ImageView = itemView.findViewById(R.id.componentListPostsItemInactive)
		val save: ImageView = itemView.findViewById(R.id.componentListPostsItemSave)
		val avatar:ImageView = itemView.findViewById(R.id.componentListPostsItemAvatar)
		val name:TextView = itemView.findViewById(R.id.componentListPostsItemName)
		val option:ImageView = itemView.findViewById(R.id.componentListPostsItemOption)
		val content:TextView = itemView.findViewById(R.id.componentListPostsItemContent)
		val imagesContainer:ViewGroup = itemView.findViewById(R.id.componentListPostsItemImagesContainer)
		val image1:ImageView = itemView.findViewById(R.id.componentListPostsItemImage1)
		val image2:ImageView = itemView.findViewById(R.id.componentListPostsItemImage2)
		val image3:ImageView = itemView.findViewById(R.id.componentListPostsItemImage3)
		val image4Block:ViewGroup = itemView.findViewById(R.id.componentListPostsItemImage4Block)
		val image4:ImageView = itemView.findViewById(R.id.componentListPostsItemImage4)
		val image4Text:TextView = itemView.findViewById(R.id.componentListPostsItemImage4Text)
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
				holder.postTime.text = getDateTimeByUTC(post.postTime);
				holder.name.text = post.authorName;
				if(post.authorType==PostModels.AuthorType.TEAM){
					Genaral.setTeamAvatarImage(context , post.authorAvatar , holder.avatar)
					holder.name.setOnClickListener {
						callbackOfViewTeam(post.authorId!!);
					}
					holder.avatar.setOnClickListener {
						callbackOfViewTeam(post.authorId!!);
					}
				}else{
					Genaral.setUserAvatarImage(context , post.authorAvatar , holder.avatar)
					holder.name.setOnClickListener {
						callbackOfViewUser(post.authorId!!);
					}
					holder.avatar.setOnClickListener {
						callbackOfViewUser(post.authorId!!);
					}
				}
				if(!isActionale){
					holder.option.visibility = View.GONE;
					holder.inactive.visibility = View.GONE;
					holder.save.visibility = View.GONE;
				}
				holder.content.text = post.content;
				
				//set image
				holder.image1.visibility = View.GONE;
				holder.image2.visibility = View.GONE;
				holder.image3.visibility = View.GONE;
				holder.image4Block.visibility = View.GONE;
				
				if(post.images.size==0){
					holder.imagesContainer.visibility = View.GONE;
				}else{
					if(post.images.size>=1){
						holder.image1.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images[0],holder.image1);
					}
					if(post.images.size>=2){
						holder.image2.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images[1],holder.image2);
					}
					if(post.images.size>=3){
						holder.image3.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images[2],holder.image3);
					}
					if(post.images.size>=4){
						holder.image4Block.visibility = View.VISIBLE;
						setPostImageWithPlaceholder(context,post.images[3],holder.image4);
						holder.image4Text.text="+"+(post.images.size-3)
					}else{
						holder.image4Text.visibility = View.GONE;
					}
					
					holder.imagesContainer.setOnClickListener {
						callbackOfOpenSlideImages(post.images)
					}
				}
				
				itemList[position].wasLoaded = true;
			}
			
			if(isActionale){
				holder.option.visibility = View.VISIBLE;
				holder.save.visibility =
					if(post.wasSaved) View.VISIBLE else View.GONE;
				holder.inactive.visibility =
					if(!post.isActive&&post.relationship==PostModels.Relationship.OWNER) View.VISIBLE else View.GONE;
				
				holder.option.setOnClickListener {
					callbackOfOpenOptions(position,post.postId!!,post.relationship,post.isActive,post.wasSaved)
				}
			}
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}