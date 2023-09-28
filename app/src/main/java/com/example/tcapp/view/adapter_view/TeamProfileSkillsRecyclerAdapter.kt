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


class TeamProfileSkillsRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<TeamProfileModels.TeamProfileSkills>
	) : RecyclerView.Adapter<TeamProfileSkillsRecyclerAdapter.ViewHolder>() {
	
	fun setInitList(list:ArrayList<TeamProfileModels.TeamProfileSkills>){
		itemList = list
		this.notifyDataSetChanged()
	}
//	fun add(team:TeamProfileModels.TeamProfileSkills){
//		itemList.add(team)
//		this.notifyDataSetChanged()
//	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val image: ImageView = itemView.findViewById(R.id.componentTeamProfileSkillImage)
		val number: TextView = itemView.findViewById(R.id.componentTeamProfileSkillNumber)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_team_profile_skill, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val skill = itemList[position];
		
		try{
			holder.number.text = skill.number.toString();
			//set image
//			if(skill.image!=null&&skill.image!="")
//				Glide.with(context)
//					.load(skill.image)
//					.error(R.drawable.default_user_avatar)
//					.into(holder.image)
			Genaral.setSkillImage(context , skill.image , holder.image)
			
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
	
	
}