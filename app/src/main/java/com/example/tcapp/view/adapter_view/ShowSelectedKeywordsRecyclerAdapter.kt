package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.model.GeneralModel


class ShowSelectedKeywordsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<GeneralModel.Keyword>
	) : RecyclerView.Adapter<ShowSelectedKeywordsRecyclerAdapter.ViewHolder>() {

	private var isContainerShow:Boolean=true;

	public fun toggleItem(item:GeneralModel.Keyword,isShow:Boolean){
		val ind = itemList.indexOfFirst { it.id==item.id }
		if(ind!=-1){
			//remove
			if(!isShow){
				itemList.removeAt(ind)
				this.notifyItemRemoved(ind)
			}
		}else{
			//add
			itemList.add(item)
			this.notifyItemInserted(itemCount-1)
		}
	}
	fun setInitList(list:ArrayList<GeneralModel.Keyword>){
		itemList = list
	}
	fun toggleIsContainerShow(){
		isContainerShow = !isContainerShow;
		this.notifyItemRangeChanged(0,itemList.size)
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentKeywordsItemName)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_keywords_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		if(!isContainerShow){
			holder.item.visibility = View.GONE
			holder.item.layoutParams = RecyclerView.LayoutParams(0 , 0)
		}else{
			holder.item.visibility = View.VISIBLE
			holder.item.layoutParams = RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT ,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			holder.name.text = item.name
		}
	}
	
	override fun getItemCount() = itemList.size
}