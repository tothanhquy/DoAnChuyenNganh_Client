package com.example.tcapp.view.adapter_view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.search.SearchModels


class SearchObjectsRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<SearchModels.SearchItem>
	) : RecyclerView.Adapter<SearchObjectsRecyclerAdapter.ViewHolder>() {
	
	public var wasLoaded:Boolean = false;
	public var isFinish:Boolean = false;
	public val ITEM_NUMBER_PER_PAGE:Int = 20;
	
	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	private lateinit var callbackOfViewTeam:(String)->Unit
	public fun setCallbackOfViewTeam(a:(String)->Unit){
		callbackOfViewTeam = a
	}
	private lateinit var callbackOfViewProject:(String)->Unit
	public fun setCallbackOfViewProject(a:(String)->Unit){
		callbackOfViewProject = a
	}
	
	fun setInitList(list:ArrayList<SearchModels.SearchItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addItems(list:ArrayList<SearchModels.SearchItem>){
		val beforeCount = itemCount
		itemList.addAll(list)
		this.notifyItemRangeInserted(beforeCount,list.size);
//		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val avatar:ImageView = itemView.findViewById(R.id.componentSearchObjectsItemAvatar)
		val name:TextView = itemView.findViewById(R.id.componentSearchObjectsItemName)
		val type:TextView = itemView.findViewById(R.id.componentSearchObjectsItemType)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_search_objects_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		try{
			holder.name.text = item.name;
			when (item.type) {
				SearchModels.ObjectType.Team -> {
					Genaral.setTeamAvatarImage(context , item.avatar , holder.avatar)
					holder.name.setOnClickListener {
						callbackOfViewTeam(item.id!!);
					}
					holder.avatar.setOnClickListener {
						callbackOfViewTeam(item.id!!);
					}
					holder.type.text="Team";
				}
				SearchModels.ObjectType.Project -> {
					Genaral.setProjectImageWithPlaceholder(context , item.avatar , holder.avatar)
					holder.name.setOnClickListener {
						callbackOfViewProject(item.id!!);
					}
					holder.avatar.setOnClickListener {
						callbackOfViewProject(item.id!!);
					}
					holder.type.text="Dự án";
				}
				else -> {
					Genaral.setUserAvatarImage(context , item.avatar , holder.avatar)
					holder.name.setOnClickListener {
						callbackOfViewUser(item.id!!);
					}
					holder.avatar.setOnClickListener {
						callbackOfViewUser(item.id!!);
					}
					holder.type.text="Người dùng";
				}
			}
		}catch(e:Exception){}
	}
	override fun getItemCount() = itemList.size
}