package com.example.tcapp.view.adapter_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.model.user_profile.UserProfileModels


class GuestUserProfileCVsRecyclerAdapter(
	private var itemList: ArrayList<UserProfileModels.GuestCV>
	) : RecyclerView.Adapter<GuestUserProfileCVsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<UserProfileModels.GuestCV>){
		itemList = list
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentGuestProfileCVName)
		val btnOpen: Button = itemView.findViewById(R.id.componentGuestProfileCVOpenPDF)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_guest_user_profile_cv, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val cv = itemList[position];
		
		holder.name.text = cv.name
		holder.btnOpen.setOnClickListener {
			callback(cv.id!!)
		}
		
	}
	
	override fun getItemCount() = itemList.size
}