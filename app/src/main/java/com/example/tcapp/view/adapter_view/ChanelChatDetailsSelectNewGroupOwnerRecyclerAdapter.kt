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


class ChanelChatDetailsSelectNewGroupOwnerRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<ChanelChatModels.Member>
	) : RecyclerView.Adapter<ChanelChatDetailsSelectNewGroupOwnerRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String,String?)->Unit
	public fun setCallback(a:(String,String?)->Unit){
		callback = a
		println("callback")
	}
	
	
	fun setInitList(list:ArrayList<ChanelChatModels.Member>){
		itemList = list
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentChanelChatDetailsSelectNewGroupOwnerItemName)
		val avatar: ImageView = itemView.findViewById(R.id.componentChanelChatDetailsSelectNewGroupOwnerItemAvatar)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_chanel_chat_details_select_new_group_owner_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		try{
			Genaral.setUserAvatarImage(context , member.avatar , holder.avatar)
			holder.name.text = member.name;
			holder.item.setOnClickListener {
				callback(member.id!!,member.name)
			}
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}