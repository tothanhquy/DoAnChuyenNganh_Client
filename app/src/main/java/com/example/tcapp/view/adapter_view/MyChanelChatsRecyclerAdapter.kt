package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.chanel_chat.MessageModels


class MyChanelChatsRecyclerAdapter(
	private var context: Context ,
	private var itemList: SortedList<ChanelChatModels.ChanelChat>
	) : RecyclerView.Adapter<MyChanelChatsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<ChanelChatModels.ChanelChat>){
		//init list
		itemList = SortedList<ChanelChatModels.ChanelChat>(ChanelChatModels.ChanelChat::class.java, object : SortedList.Callback<ChanelChatModels.ChanelChat>() {
			override fun compare(o1: ChanelChatModels.ChanelChat, o2: ChanelChatModels.ChanelChat): Int {
				return o1.lastTimeAction.compareTo(o2.lastTimeAction)
			}

			override fun onChanged(position: Int, count: Int) {
				notifyItemRangeChanged(position, count)
			}

			override fun areContentsTheSame(oldItem: ChanelChatModels.ChanelChat, newItem: ChanelChatModels.ChanelChat): Boolean {
				return oldItem.lastTimeAction.equals(newItem.lastTimeAction)
			}

			override fun areItemsTheSame(item1: ChanelChatModels.ChanelChat, item2: ChanelChatModels.ChanelChat): Boolean {
				return item1.lastTimeAction.equals(item2.lastTimeAction)
			}

			override fun onInserted(position: Int, count: Int) {
				notifyItemRangeInserted(position, count)
			}

			override fun onRemoved(position: Int, count: Int) {
				notifyItemRangeRemoved(position, count)
			}

			override fun onMoved(fromPosition: Int, toPosition: Int) {
				notifyItemMoved(fromPosition, toPosition)
			}
		})
		if(list != null){
			list!!.forEach {
				this.itemList.add(it)
			}
		}
	}
	fun add(chanelChat:ChanelChatModels.ChanelChat){
		itemList.add(chanelChat)
		this.notifyDataSetChanged()
	}
	fun changeOrAdd(chanelChat:ChanelChatModels.ChanelChat){
		for(i in 0 until itemCount){
			if(itemList[i].id==chanelChat.id){
				itemList.updateItemAt(i,chanelChat)
				return;
			}
		}
		itemList.add(chanelChat)
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
	
	override fun getItemCount() = itemList.size()
}