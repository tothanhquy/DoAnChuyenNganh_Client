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


class ChanelChatViewMembersListRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<ChanelChatModels.Member>
	) : RecyclerView.Adapter<ChanelChatViewMembersListRecyclerAdapter.ViewHolder>() {
	
	private var isActionAle:Boolean = false;
	private var accountId:String? = null;

	private lateinit var callbackOfOpenOptions:(String,String?)->Unit
	public fun setCallbackOfOpenOptions(a:(String,String?)->Unit){
		callbackOfOpenOptions = a
	}
	
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	
	fun setInitList(list:ArrayList<ChanelChatModels.Member>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun setActionAble(isActionAle:Boolean=false){
		this.isActionAle = isActionAle;
	}
	fun setAccountId(accountId:String?=null){
		this.accountId = accountId;
	}
	fun deleteMember(memberId:String?){
		if(memberId!=null){
			val delMember =  itemList.firstOrNull{it.id==memberId}
			if(delMember!=null){
				itemList.remove(delMember);
				this.notifyDataSetChanged()
			}
			
		}
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentChanelChatMembersListItemName)
		val avatar: ImageView = itemView.findViewById(R.id.componentChanelChatMembersListItemAvatar)
		val btnAction:ImageView = itemView.findViewById(R.id.componentChanelChatMembersListItemBtnAction)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_chanel_chat_members_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		
		try{
			Genaral.setUserAvatarImage(context , member.avatar , holder.avatar)
			holder.name.text = member.name;

			holder.btnAction.setOnClickListener {
				callbackOfOpenOptions(member.id!!,member.name)
			}
			if(isActionAle && member.id!=accountId)
				holder.btnAction.visibility = View.VISIBLE;
			else
				holder.btnAction.visibility = View.GONE;
			
			holder.name.setOnClickListener {
				callbackOfViewUser(member.id!!)
			}
			holder.avatar.setOnClickListener {
				callbackOfViewUser(member.id!!)
			}
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}