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


class ProjectViewMembersNowRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<ProjectModels.MemberNow>
	) : RecyclerView.Adapter<ProjectViewMembersNowRecyclerAdapter.ViewHolder>() {
	
	private var isActionAle:Boolean = false;
	
	private lateinit var callbackOfOpenOptions:(String,String?,String?)->Unit
	public fun setCallbackOfOpenOptions(a:(String,String?,String?)->Unit){
		callbackOfOpenOptions = a
	}
	
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	
	fun setInitList(list:ArrayList<ProjectModels.MemberNow>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun setActionAble(isActionAle:Boolean=false){
		this.isActionAle = isActionAle;
	}
	fun deleteMember(memberId:String?){
		if(memberId!=null){
			val indexMember =  itemList.indexOfFirst{it.id==memberId}
			if(indexMember!=-1){
				itemList.removeAt(indexMember);
				this.notifyItemRemoved(indexMember)
			}
		}
	}
	fun updateRoleById(memberId:String?,role:String?){
		if(memberId!=null){
			val indexMember =  itemList.indexOfFirst{it.id==memberId}
			if(indexMember!=-1){
				itemList[indexMember].role=role;
				this.notifyItemChanged(indexMember)
			}
		}
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val leaderIcon: TextView = itemView.findViewById(R.id.componentProjectMembersNowItemLeaderFlag)
		val name: TextView = itemView.findViewById(R.id.componentProjectMembersNowItemName)
		val role: TextView = itemView.findViewById(R.id.componentProjectMembersNowItemRole)
		val avatar: ImageView = itemView.findViewById(R.id.componentProjectMembersNowItemAvatar)
		val btnAction:ImageView = itemView.findViewById(R.id.componentProjectMembersNowItemBtnAction)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_project_members_now_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val member = itemList[position];
		
		try{
			Genaral.setUserAvatarImage(context , member.avatar , holder.avatar)
			holder.name.text = member.name;
			holder.role.text = "Vai trò: ${member.role}";
			holder.leaderIcon.visibility = if(member.isLeader)View.VISIBLE else View.GONE;
			holder.btnAction.setOnClickListener {
				callbackOfOpenOptions(member.id!!,member.name,member.role)
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