package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.model.project.ProjectModels


class ProjectEditTagsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<String?>
	) : RecyclerView.Adapter<ProjectEditTagsRecyclerAdapter.ViewHolder>() {
	public val MAX_COUNT = 7;

	private lateinit var callbackOfOpenOptions:(Int)->Unit
	public fun setCallbackOfOpenOptions(a:(Int)->Unit){
		callbackOfOpenOptions = a
	}

	public fun setInit(items:ArrayList<String?>?){
		if(items==null){
			itemList= arrayListOf()
		}else{
			itemList=items;
		}
		this.notifyDataSetChanged()
	}
	public fun getList():ArrayList<String?>{
		return itemList
	}
	public fun add(item:String){
		itemList.add(item);
		this.notifyItemInserted(itemCount-1)
	}
	public fun deleteAt(position:Int){
		if(position in 0..itemCount){
			itemList.removeAt(position)
			this.notifyItemRemoved(position)
		}
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val content: TextView = itemView.findViewById(R.id.componentStringListItemContent)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_string_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		holder.content.text = item;
		holder.item.setOnLongClickListener {
			callbackOfOpenOptions(position)
			true
		}
	}
	
	override fun getItemCount() = itemList.size
}