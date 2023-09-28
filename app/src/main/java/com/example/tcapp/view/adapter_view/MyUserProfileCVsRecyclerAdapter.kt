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


class MyUserProfileCVsRecyclerAdapter(
	private var itemList: ArrayList<UserProfileModels.MyCV>
	) : RecyclerView.Adapter<MyUserProfileCVsRecyclerAdapter.ViewHolder>() {
	
	private lateinit var callback:(String,String,Boolean)->Unit
	public fun setCallback(a:(String,String,Boolean)->Unit){
		callback = a
	}
	
	fun setInitList(list:ArrayList<UserProfileModels.MyCV>){
		itemList = list
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentMyProfileCVName)
		val isActive: ImageView = itemView.findViewById(R.id.componentMyProfileCVIsActive)
		val btnEdit: Button = itemView.findViewById(R.id.componentMyProfileCVEdit)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_my_user_profile_cv, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val cv = itemList[position];
		
		holder.name.text = cv.name
		holder.isActive.visibility = if(!cv.isActive) View.GONE else View.VISIBLE
		holder.btnEdit.setOnClickListener {
			callback(cv.id!!,cv.name!!,cv.isActive)
		}
		
	}
	
	override fun getItemCount() = itemList.size
}