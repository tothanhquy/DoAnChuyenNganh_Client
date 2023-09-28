package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral.Static.setSkillImage
import com.example.tcapp.model.user_profile.UserProfileModels


class ProfileSkillsRecyclerAdapter(
	var context:Context,
	private var itemList: ArrayList<UserProfileModels.Skill>
	) : RecyclerView.Adapter<ProfileSkillsRecyclerAdapter.ViewHolder>() {
	
	fun setInitList(list:ArrayList<UserProfileModels.Skill>){
		itemList = list
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentProfileSkillName)
		val image: ImageView = itemView.findViewById(R.id.componentProfileSkillImage)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_profile_skill, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val skill = itemList[position];
		
		holder.item.visibility = View.VISIBLE
//		holder.item.layoutParams = RecyclerView.LayoutParams(
//			ViewGroup.LayoutParams.WRAP_CONTENT ,
//			ViewGroup.LayoutParams.WRAP_CONTENT
//		)
		
		setSkillImage(context,skill.image,holder.image)
		holder.name.text = skill.name
//		holder.image.visibility = View.GONE
	}
	
	override fun getItemCount() = itemList.size
}