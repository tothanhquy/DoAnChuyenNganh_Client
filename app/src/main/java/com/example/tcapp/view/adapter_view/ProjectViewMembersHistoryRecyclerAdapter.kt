package com.example.tcapp.view.adapter_view

import android.annotation.SuppressLint
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


class ProjectViewMembersHistoryRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<ProjectModels.MemberHistory>
	) : RecyclerView.Adapter<ProjectViewMembersHistoryRecyclerAdapter.ViewHolder>() {

	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	fun setInitList(list:ArrayList<ProjectModels.MemberHistory>){
		itemList = list
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val time: TextView = itemView.findViewById(R.id.componentProjectMembersHistoryItemTime)
		val name: TextView = itemView.findViewById(R.id.componentProjectMembersHistoryItemName)
		val role: TextView = itemView.findViewById(R.id.componentProjectMembersHistoryItemRole)
		val avatar: ImageView = itemView.findViewById(R.id.componentProjectMembersHistoryItemAvatar)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_project_members_history_item, parent, false)
		return ViewHolder(itemView)
	}
	
	@SuppressLint("ResourceAsColor")
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		
		try{
			Genaral.setUserAvatarImage(context , member.avatar , holder.avatar)
			holder.name.text = member.name;
			holder.time.text = Genaral.getDateByUTC(member.time)
			if(!member.isOut){
				holder.role.text="Đảm nhiệm vai trò: ${member.role}";
				holder.role.setTextColor(R.color.theme_color_2_dark)
			}else{
				holder.role.text="Rời Dự Án";
				holder.role.setTextColor(R.color.theme_color_5_dark)
			}

		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}