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


class MyTeamsRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<TeamProfileModels.MyTeamItem>
	) : RecyclerView.Adapter<MyTeamsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<TeamProfileModels.MyTeamItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun add(team:TeamProfileModels.MyTeamItem){
		itemList.add(team)
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val teamAvatar: ImageView = itemView.findViewById(R.id.componentMyTeamsItemTeamAvatar)
		val teamName: TextView = itemView.findViewById(R.id.componentMyTeamsItemTeamName)
		val leaderAvatar: ImageView = itemView.findViewById(R.id.componentMyTeamsItemLeaderAvatar)
		val leaderName: TextView = itemView.findViewById(R.id.componentMyTeamsItemLeaderName)
		val membersNumber: TextView = itemView.findViewById(R.id.componentMyTeamsItemMembersNumber)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_my_teams_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val team = itemList[position];
//		try{
			//set team avatar
//			if(team.teamAvatar!=null&&team.teamAvatar!="")
//				Glide.with(context)
//					.load(team.teamAvatar)
//					.error(R.drawable.default_user_avatar)
//					.into(holder.teamAvatar)
			Genaral.setTeamAvatarImage(context , team.teamAvatar , holder.teamAvatar)
			
			holder.teamName.text = team.teamName;
			
			//set leader avatar
//			if(team.leaderAvatar!=null&&team.leaderAvatar!="")
//				Glide.with(context)
//					.load(team.leaderAvatar)
//					.error(R.drawable.default_user_avatar)
//					.into(holder.leaderAvatar)
			Genaral.setUserAvatarImage(context , team.leaderAvatar , holder.leaderAvatar)
			
			holder.leaderName.text = team.leaderName;
			holder.membersNumber.text = team.memberNumber.toString();
			
			holder.item.setOnClickListener {
				callback(team.teamId!!)
			}
//		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}