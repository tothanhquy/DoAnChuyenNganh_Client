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


class TeamProfileSelectNewLeaderRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<TeamProfileModels.Member>
	) : RecyclerView.Adapter<TeamProfileSelectNewLeaderRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String,String?)->Unit
	public fun setCallback(a:(String,String?)->Unit){
		callback = a
		println("callback")
	}
	
	
	fun setInitList(list:ArrayList<TeamProfileModels.Member>){
		itemList = list
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentTeamProfileSelectNewLeaderItemName)
		val avatar: ImageView = itemView.findViewById(R.id.componentTeamProfileSelectNewLeaderItemAvatar)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_team_profile_select_new_leader_item, parent, false)
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
			
			holder.item.setOnClickListener {
				callback(member.id!!,member.name)
			}
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}