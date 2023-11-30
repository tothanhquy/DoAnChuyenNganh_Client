package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.model.project.ProjectModels


class StringListRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<String?>
	) : RecyclerView.Adapter<StringListRecyclerAdapter.ViewHolder>() {

	fun setList(items:ArrayList<String?>){
		this.itemList=items;
		this.notifyDataSetChanged()
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
	}
	
	override fun getItemCount() = itemList.size
}