package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.GeneralModel


class SelectKeywordsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<SelectKeyword>,
	) : RecyclerView.Adapter<SelectKeywordsRecyclerAdapter.ViewHolder>() {

	fun setInitList(list:ArrayList<GeneralModel.Keyword>){
		itemList = ArrayList(list.map{SelectKeyword(it.id,it.name)})
		this.notifyDataSetChanged()
	}
	private lateinit var clickCallback:(GeneralModel.Keyword,Boolean)->Unit
	public fun setClickCallback(a:(GeneralModel.Keyword,Boolean)->Unit){
		clickCallback = a
	}
	private fun toggleItemIsCheck(position : Int){
		itemList[position].isCheck = !itemList[position].isCheck
		this.notifyItemChanged(position)
	}
	public fun setIsCheckOfItems(list:ArrayList<GeneralModel.Keyword>,value:Boolean){
		for (i in 0 until list.size){
			val index = itemList.indexOfFirst{
				it.id == list[i].id
			}
			if(index!=-1){
				itemList[index].isCheck = value
				this.notifyItemChanged(index)
			}
		}
	}
	public fun setIsCheckOfAll(value:Boolean){
		for (i in 0 until itemList.size){
			itemList[i].isCheck=value
		}
		this.notifyDataSetChanged()
	}
	public fun filterByName(searchString:String){
		for (i in 0 until itemList.size){
			val beforeIsShow = itemList[i].isShow;
			itemList[i].isShow = searchString==""||(itemList[i].name!=null&&Genaral.searchByWords(itemList[i].name!!,searchString))
			if(beforeIsShow!=itemList[i].isShow){
				this.notifyItemChanged(i)
			}
		}
	}
	public fun getIdIsCheck():ArrayList<String>{
		val ids = ArrayList<String>()
		for (i in 0 until itemList.size){
			if(itemList[i].isCheck)ids.add(itemList[i].id!!)
		}
		return ids
	}
	public fun getKeywordsIsCheck():ArrayList<GeneralModel.Keyword>{
		val items = ArrayList<GeneralModel.Keyword>();
		for (i in 0 until itemList.size){
			if(itemList[i].isCheck)items.add(GeneralModel.Keyword(itemList[i].id,itemList[i].name));
		}
		return items
	}
	class SelectKeyword{
		var id:String?=null;
		var name:String?=null;
		var isCheck:Boolean=false;
		var isShow:Boolean=true;
		constructor(
			id : String? ,
			name : String? ,
			isCheck : Boolean = false,
			isShow : Boolean = true,
		) {
			this.id = id
			this.name = name
			this.isCheck = isCheck
			this.isShow = isShow
		}
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentSelectKeywordsItemName)
		val notCheck: ImageView = itemView.findViewById(R.id.componentSelectKeywordsItemNotCheck)
		val checked: ImageView = itemView.findViewById(R.id.componentSelectKeywordsItemChecked)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_select_keywords_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		if(!item.isShow){
			holder.item.visibility = View.GONE
			holder.item.layoutParams = RecyclerView.LayoutParams(0 , 0)
		}else{
			holder.item.visibility = View.VISIBLE
			holder.item.layoutParams = RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT ,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			
			holder.name.text = item.name
			if(item.isCheck){
				holder.notCheck.visibility = View.GONE
				holder.checked.visibility = View.VISIBLE
				holder.checked.setOnClickListener {
					clickCallback(GeneralModel.Keyword(item.id,item.name),false)
					toggleItemIsCheck(position)
				}
			}else{
				holder.notCheck.visibility = View.VISIBLE
				holder.checked.visibility = View.GONE
				holder.notCheck.setOnClickListener {
					clickCallback(GeneralModel.Keyword(item.id,item.name),true)
					toggleItemIsCheck(position)
				}
			}
		}
	}
	
	override fun getItemCount() = itemList.size

}