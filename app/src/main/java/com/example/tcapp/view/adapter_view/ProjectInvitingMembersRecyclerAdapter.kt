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


class ProjectInvitingMembersRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<ProjectModels.InvitingMember>
	) : RecyclerView.Adapter<ProjectInvitingMembersRecyclerAdapter.ViewHolder>() {
	
	private lateinit var openCallback:(String)->Unit
	public fun setOpenCallback(a:(String)->Unit){
		openCallback = a
	}
	private lateinit var cancelCallback:(String)->Unit
	public fun setUpdateCallback(a:(String)->Unit){
		cancelCallback = a
	}

	public fun deleteId(id:String?){
		for(i in 0..itemCount){
			if(itemList[i].id==id){
				itemList.removeAt(i)
				break;
			}
		}
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar: ImageView = itemView.findViewById(R.id.componentProjectInvitingMembersAvatar)
		val name: TextView = itemView.findViewById(R.id.componentProjectInvitingMembersName)
		val role: TextView = itemView.findViewById(R.id.componentProjectInvitingMembersRole)
		val agree: Button = itemView.findViewById(R.id.componentProjectInvitingMembersAgreeButton)
		val disagree: Button = itemView.findViewById(R.id.componentProjectInvitingMembersDisagreeButton)
		val cancel: Button = itemView.findViewById(R.id.componentProjectInvitingMembersCancelButton)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_project_inviting_members_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		Genaral.setUserAvatarImage(context , item.avatar , holder.avatar)
		holder.name.text = item.name;
		holder.role.text = "Vai trò: "+item.role;
		holder.name.setOnClickListener {
			openCallback(item.id!!)
		}
		holder.avatar.setOnClickListener {
			openCallback(item.id!!)
		}
		holder.agree.visibility = View.GONE
		holder.disagree.visibility = View.GONE
		holder.cancel.setOnClickListener {
			cancelCallback(item.id!!)
		}
	}
	
	override fun getItemCount() = itemList.size
}