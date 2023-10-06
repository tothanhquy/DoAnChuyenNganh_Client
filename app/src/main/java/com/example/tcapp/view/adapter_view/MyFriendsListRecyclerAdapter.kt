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
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class MyFriendsListRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<FriendModels.MyFriendsListItem>
	) : RecyclerView.Adapter<MyFriendsListRecyclerAdapter.ViewHolder>() {

	public var wasLoaded:Boolean=false;

	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	
	fun setInitList(list:ArrayList<FriendModels.MyFriendsListItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentMyFriendsListItemName)
		val avatar: ImageView = itemView.findViewById(R.id.componentMyFriendsListItemAvatar)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_my_friends_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val user = itemList[position];
		
		try{
			Genaral.setUserAvatarImage(context , user.userAvatar , holder.avatar)
			holder.name.text = user.userName;

			holder.name.setOnClickListener {
				callbackOfViewUser(user.userId!!)
			}
			holder.avatar.setOnClickListener {
				callbackOfViewUser(user.userId!!)
			}
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}