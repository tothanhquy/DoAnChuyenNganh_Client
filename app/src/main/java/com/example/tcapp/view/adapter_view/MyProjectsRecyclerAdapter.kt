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
import com.example.tcapp.model.project.ProjectModels


class MyProjectsRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<ProjectModels.ProjectListItem>
	) : RecyclerView.Adapter<MyProjectsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<ProjectModels.ProjectListItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun add(team:ProjectModels.ProjectListItem){
		itemList.add(team)
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val projectAvatar: ImageView = itemView.findViewById(R.id.componentMyTeamsItemTeamAvatar)
		val projectName: TextView = itemView.findViewById(R.id.componentMyTeamsItemTeamName)
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
		val item = itemList[position];
		Genaral.setProjectImageWithPlaceholder(context , item.projectAvatar , holder.projectAvatar)
		holder.projectName.text = item.projectName;
		Genaral.setUserAvatarImage(context , item.leaderAvatar , holder.leaderAvatar)
		holder.leaderName.text = item.leaderName;
		holder.membersNumber.text = item.memberNumber.toString();
		holder.item.setOnClickListener {
			callback(item.projectId!!)
		}
	}
	
	override fun getItemCount() = itemList.size
}