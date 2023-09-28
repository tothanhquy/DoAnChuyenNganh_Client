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


class TeamsOfLeaderSelectRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<TeamProfileModels.TeamsOfLeaderItem>
	) : RecyclerView.Adapter<TeamsOfLeaderSelectRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String,String?,String?)->Unit
	public fun setCallback(a:(String,String?,String?)->Unit){
		callback = a
	}
	
//	fun setInitList(list:ArrayList<TeamProfileModels.TeamsOfLeaderItem>){
//		itemList = list
//		this.notifyDataSetChanged()
//	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar: ImageView = itemView.findViewById(R.id.componentTeamsOfLeaderItemTeamAvatar)
		val name: TextView = itemView.findViewById(R.id.componentTeamsOfLeaderItemTeamName)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_teams_of_leader_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val team = itemList[position];
		
		try{
			holder.name.text = team.teamName.toString();
			Genaral.setTeamAvatarImage(context , team.teamAvatar , holder.avatar)
			holder.item.setOnClickListener {
				callback(team.teamId!!,team.teamName,team.teamAvatar)
			}
		}catch(e:Exception){}
	}
	fun getFirstItem():TeamProfileModels.TeamsOfLeaderItem{
		return itemList[0]
	}
	override fun getItemCount() = itemList.size
	
	
}