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
import com.example.tcapp.model.chanel_chat.ChanelChatModels


class MyChanelChatsRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<ChanelChatModels.ChanelChat>
	) : RecyclerView.Adapter<MyChanelChatsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<ChanelChatModels.ChanelChat>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun add(chanelChat:ChanelChatModels.ChanelChat){
		itemList.add(chanelChat)
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar: ImageView = itemView.findViewById(R.id.componentMyChanelChatsItemAvatar)
		val name: TextView = itemView.findViewById(R.id.componentMyChanelChatsItemName)
		val lastMessageContent: TextView = itemView.findViewById(R.id.componentMyChanelChatsItemLastMessageContent)
		val lastMessageTime: TextView = itemView.findViewById(R.id.componentMyChanelChatsItemLastMessageTime)
		val checkSeen: ImageView = itemView.findViewById(R.id.componentMyChanelChatsItemSeen)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_my_chanel_chats_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		if(!item.isLoadAvatar){
			when (item.type) {
				ChanelChatModels.Type.Team -> {
					Genaral.setTeamAvatarImage(context , item.avatar , holder.avatar)
				}
				ChanelChatModels.Type.User -> {
					Genaral.setUserAvatarImage(context , item.avatar , holder.avatar)
				}
				else -> {
					Genaral.setGroupChatAvatarImage(context , item.avatar , holder.avatar)
				}
			}
			holder.name.text = item.name;
			item.isLoadAvatar = true;
		}
		if(item.numberOfNewMessages==0L){
			holder.checkSeen.visibility = View.GONE
		}else{
			holder.checkSeen.visibility = View.VISIBLE
		}
		if(item.lastMessageTime==0L){
			holder.lastMessageContent.text=""
			holder.lastMessageTime.text=""
		}else{
			holder.lastMessageContent.text=item.lastMessageContent
			holder.lastMessageTime.text=Genaral.getMinimizeDateTimeByUTC(item.lastMessageTime)
		}
		holder.item.setOnClickListener {
			callback(item.id!!)
		}
	}
	
	override fun getItemCount() = itemList.size
}