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
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class FriendRequestsListRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<FriendModels.RequestsListItem>
	) : RecyclerView.Adapter<FriendRequestsListRecyclerAdapter.ViewHolder>() {
	
	private lateinit var method:FriendModels.RequestMethod;

	public var timePrevious:Long=0;
	public var wasLoaded:Boolean=false;
	
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	fun setMethod(method:FriendModels.RequestMethod){
		this.method = method;
	}
	fun setInitList(list:ArrayList<FriendModels.RequestsListItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addList(list:ArrayList<FriendModels.RequestsListItem>){
		val previousSize = itemList.size
		itemList.addAll(previousSize,list);
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar: ImageView = itemView.findViewById(R.id.componentFriendRequestsListItemAvatar)
		val name: TextView = itemView.findViewById(R.id.componentFriendRequestsListItemName)
		val time: TextView = itemView.findViewById(R.id.componentFriendRequestsListItemDate)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_friend_requests_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val request = itemList[position];
		try{
			holder.time.text = Genaral.getDateTimeByUTC(request.time);
			
			if(request.wasLoadData)return;

			Genaral.setUserAvatarImage(context , request.userAvatar , holder.avatar)
			holder.name.text = request.userName;

			holder.item.setOnClickListener {
				callback(request.id!!)
			}
			request.wasLoadData = true;
		}catch(e:Exception){
			println(e.toString())
		}
	}
	override fun getItemCount() = itemList.size
}