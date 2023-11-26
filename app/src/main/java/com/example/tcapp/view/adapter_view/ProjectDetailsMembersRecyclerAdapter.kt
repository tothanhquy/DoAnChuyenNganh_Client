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
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class ProjectDetailsMembersRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<ProjectModels.MemberNow>
	) : RecyclerView.Adapter<ProjectDetailsMembersRecyclerAdapter.ViewHolder>() {

	companion object Static{
		val MAXIMUM_SHOW_MEMBER = 7;
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val memberAvatar: ImageView = itemView.findViewById(R.id.componentTeamProfileMemberAvatar)
		val otherMembersNumber: TextView = itemView.findViewById(R.id.componentTeamProfileNumberOtherMembers)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_team_profile_member, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		try{
			if(MAXIMUM_SHOW_MEMBER==position){
				holder.otherMembersNumber.text = "+"+member.name;
				holder.memberAvatar.visibility=View.GONE
			}else{
				Genaral.setUserAvatarImage(context , member.avatar , holder.memberAvatar)
				holder.otherMembersNumber.visibility=View.GONE
			}
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}