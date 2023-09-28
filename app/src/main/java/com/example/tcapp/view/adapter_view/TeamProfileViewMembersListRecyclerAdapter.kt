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
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class TeamProfileViewMembersListRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<TeamProfileModels.Member>
	) : RecyclerView.Adapter<TeamProfileViewMembersListRecyclerAdapter.ViewHolder>() {
	
	private var isActionAle:Boolean = false;
	
	private lateinit var callbackOfOpenOptions:(String,String?)->Unit
	public fun setCallbackOfOpenOptions(a:(String,String?)->Unit){
		callbackOfOpenOptions = a
	}
	
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	
	fun setInitList(list:ArrayList<TeamProfileModels.Member>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun setActionAble(isActionAle:Boolean=false){
		this.isActionAle = isActionAle;
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
		val leaderIcon: TextView = itemView.findViewById(R.id.componentTeamProfileMembersListItemLeaderFlag)
		val name: TextView = itemView.findViewById(R.id.componentTeamProfileMembersListItemName)
		val avatar: ImageView = itemView.findViewById(R.id.componentTeamProfileMembersListItemAvatar)
		val btnAction:ImageView = itemView.findViewById(R.id.componentTeamProfileMembersListItemBtnAction)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_team_profile_members_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		
		try{
			//set team avatar
//			if(member.avatar!=null&&member.avatar!="")
//				Glide.with(context)
//					.load(member.avatar)
//					.error(R.drawable.default_user_avatar)
//					.into(holder.avatar)
			Genaral.setUserAvatarImage(context , member.avatar , holder.avatar)
			
			
			holder.name.text = member.name;
			
			holder.leaderIcon.visibility = if(member.isLeader)View.VISIBLE else View.GONE;
			
			holder.btnAction.setOnClickListener {
				callbackOfOpenOptions(member.id!!,member.name)
			}
			if(isActionAle)
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