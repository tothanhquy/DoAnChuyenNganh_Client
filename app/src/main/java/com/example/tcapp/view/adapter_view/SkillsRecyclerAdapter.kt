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


class SkillsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<Skill>
	) : RecyclerView.Adapter<SkillsRecyclerAdapter.ViewHolder>() {
	private var isContainerShow:Boolean=true;
	public fun toggleItemIsShow(id : String){
		for (i in 0 until itemList.size){
			if(itemList[i].id==id){
				itemList[i].isShow = !itemList[i].isShow
//				this.notifyDataSetChanged()
				this.notifyItemChanged(i)
				break
			}
		}
	}
	fun setInitList(list:ArrayList<Skill>){
		itemList = list
	}
	fun toggleIsContainerShow(){
		isContainerShow = !isContainerShow;
		this.notifyItemRangeChanged(0,itemList.size)
	}
	public fun setIsShowOfItems(ids:ArrayList<String>,value:Boolean){
		for (i in 0 until ids.size){
			val index = itemList.indexOfFirst{
				it.id == ids[i]
			}
			if(index!=-1){
				itemList[index].isShow = value
				this.notifyItemChanged(index)
			}
		}
//		this.notifyDataSetChanged()
	}
	class Skill{
		var id:String?=null;
		var name:String?=null;
		var image:String?=null;
		var isShow:Boolean=true;
		constructor(
			id : String? ,
			name : String? ,
			image : String? ,
			isShow : Boolean = false,
		) {
			this.id = id
			this.name = name
			this.image = image
			this.isShow = isShow
		}
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentProfileSkillName)
		val image: ImageView = itemView.findViewById(R.id.componentProfileSkillImage)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_profile_skill, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val skill = itemList[position];
		if(!skill.isShow||!isContainerShow){
			holder.item.visibility = View.GONE
			holder.item.layoutParams = RecyclerView.LayoutParams(0 , 0)
			
		}else{
			holder.item.visibility = View.VISIBLE
			holder.item.layoutParams = RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT ,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			
			holder.name.text = skill.name
//			holder.image.visibility = View.GONE
			Genaral.setSkillImage(context , skill.image , holder.image)
		}
	}
	
	override fun getItemCount() = itemList.size
}