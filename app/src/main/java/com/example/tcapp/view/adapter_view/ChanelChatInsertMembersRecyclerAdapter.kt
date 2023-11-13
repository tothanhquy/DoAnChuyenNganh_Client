package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.ChanelChatModels


class ChanelChatInsertMembersRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<ChanelChatModels.InsertMemberAdapterItem> ,
	) : RecyclerView.Adapter<ChanelChatInsertMembersRecyclerAdapter.ViewHolder>() {

	fun setInitList(list:ArrayList<ChanelChatModels.InsertMemberAdapterItem>){
		itemList = list
	}

	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	private lateinit var callbackOfSelectUser:(String)->Unit
	public fun setCallbackOfSelectUser(a:(String)->Unit){
		callbackOfSelectUser = a
	}

	private fun toggleItemIsCheck(position : Int){
		itemList[position].isCheck = !itemList[position].isCheck
		this.notifyItemChanged(position)
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar: ImageView = itemView.findViewById(R.id.componentChanelChatInsertMembersItemAvatar)
		val name: TextView = itemView.findViewById(R.id.componentChanelChatInsertMembersItemName)
		val notCheck: ImageView = itemView.findViewById(R.id.componentChanelChatInsertMembersItemNotCheck)
		val checked: ImageView = itemView.findViewById(R.id.componentChanelChatInsertMembersItemChecked)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_chanel_chat_insert_members_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];

//		if(!member.isLoadImage){
			Genaral.setUserAvatarImage(context,member.avatar,holder.avatar)
			holder.name.text = member.name
			holder.name.setOnClickListener {
				callbackOfViewUser(member.id!!)
			}
			holder.avatar.setOnClickListener {
				callbackOfViewUser(member.id!!)
			}
			member.isLoadImage=true
//		}

		if(member.wasExist){
			holder.notCheck.visibility = View.GONE
			holder.checked.visibility = View.GONE
		}else{
			if(member.isCheck){
				holder.notCheck.visibility = View.GONE
				holder.checked.visibility = View.VISIBLE
				holder.checked.setOnClickListener {
					callbackOfSelectUser(member.id!!)
					toggleItemIsCheck(position)
				}
			}else{
				holder.notCheck.visibility = View.VISIBLE
				holder.checked.visibility = View.GONE
				holder.notCheck.setOnClickListener {
					callbackOfSelectUser(member.id!!)
					toggleItemIsCheck(position)
				}
			}
		}

	}

	public fun updateInsertedMembers(membersId:ArrayList<String>?){
		if(membersId!=null){
			for (i in 0 until getItemCount()){
				if(membersId.indexOf(itemList[i].id)!=-1){
					itemList[i].wasExist = true;
					this.notifyItemChanged(i)
				}
			}
		}
	}
	
	override fun getItemCount() = itemList.size

}