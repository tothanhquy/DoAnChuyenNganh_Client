package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.model.GeneralModel


class ProjectNegativeReportGeneralKeywordsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<GeneralNegativeKeyword>
	) : RecyclerView.Adapter<ProjectNegativeReportGeneralKeywordsRecyclerAdapter.ViewHolder>() {

	class GeneralNegativeKeyword{
		public var id:String?=null;
		public var name:String?=null;
		public var number:Int=0;
		constructor(){}
		constructor(id: String?, name: String?, number: Int) {
			this.id = id
			this.name = name
			this.number = number
		}
	}

	fun setInitList(list:ArrayList<GeneralNegativeKeyword>){
		itemList = list
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentProjectNegativeReportsItemName)
		val number: TextView = itemView.findViewById(R.id.componentProjectNegativeReportsItemNumber)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_project_negative_reports_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		holder.name.text = item.name
		holder.number.text = item.number.toString()
	}
	
	override fun getItemCount() = itemList.size
}