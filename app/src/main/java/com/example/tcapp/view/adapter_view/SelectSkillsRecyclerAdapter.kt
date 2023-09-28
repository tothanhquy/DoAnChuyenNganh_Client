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


class SelectSkillsRecyclerAdapter(
	var context: Context ,
	private var itemList: ArrayList<SelectSkill> ,
	val outListener:(id:String,position:Int,isCheck:Boolean)->Unit
	) : RecyclerView.Adapter<SelectSkillsRecyclerAdapter.ViewHolder>() {
	fun setInitList(list:ArrayList<SelectSkill>){
		itemList = list
	}
	private fun toggleItemIsCheck(position : Int){
		itemList[position].isCheck = !itemList[position].isCheck
//		this.notifyDataSetChanged()
		this.notifyItemChanged(position)
	}
	public fun setIsCheckOfItems(ids:ArrayList<String>,value:Boolean){
		for (i in 0 until ids.size){
			val index = itemList.indexOfFirst{
				it.id == ids[i]
			}
			if(index!=-1){
				itemList[index].isCheck = value
				this.notifyItemChanged(index)
				println(ids[i])
			}
		}
//		this.notifyDataSetChanged()
	}
	public fun filterByName(name:String){
		for (i in 0 until itemList.size){
			itemList[i].isShow = name.lowercase()==""||(itemList[i].name!=null&&itemList[i].name?.lowercase()!!.contains(name.lowercase()))
		}
//		this.notifyDataSetChanged()
		this.notifyItemRangeChanged(0,itemList.size)
//		println(itemList.size)
	}
	public fun getIdIsCheck():ArrayList<String>{
		val ids = ArrayList<String>()
		for (i in 0 until itemList.size){
			if(itemList[i].isCheck)ids.add(itemList[i].id!!)
		}
		return ids
	}
	class SelectSkill{
		var id:String?=null;
		var name:String?=null;
		var image:String?=null;
		var isCheck:Boolean=false;
		var isShow:Boolean=true;
		constructor(
			id : String? ,
			name : String? ,
			image : String? ,
			isCheck : Boolean = false,
			isShow : Boolean = true,
		) {
			this.id = id
			this.name = name
			this.image = image
			this.isCheck = isCheck
			this.isShow = isShow
		}
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val name: TextView = itemView.findViewById(R.id.componentSelectSkillsName)
		val notCheck: ImageView = itemView.findViewById(R.id.componentSelectSkillsNotCheck)
		val checked: ImageView = itemView.findViewById(R.id.componentSelectSkillsChecked)
		val image: ImageView = itemView.findViewById(R.id.componentSelectSkillsImage)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_select_skills, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val skill = itemList[position];
		if(!skill.isShow){
			holder.item.visibility = View.GONE
			holder.item.layoutParams = RecyclerView.LayoutParams(0 , 0)
		}else{
			holder.item.visibility = View.VISIBLE
			holder.item.layoutParams = RecyclerView.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT ,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			
			holder.name.text = skill.name
			if(skill.isCheck){
				holder.notCheck.visibility = View.GONE
				holder.checked.visibility = View.VISIBLE
				holder.checked.setOnClickListener {
					outListener(skill.id!!,position,false)
					toggleItemIsCheck(position)
				}
			}else{
				holder.notCheck.visibility = View.VISIBLE
				holder.checked.visibility = View.GONE
				holder.notCheck.setOnClickListener {
					outListener(skill.id!!,position,true)
					toggleItemIsCheck(position)
				}
			}
//			holder.image.visibility = View.GONE
			Genaral.setSkillImage(context , skill.image , holder.image)
		}
	}
	
	override fun getItemCount() = itemList.size
	
	fun changeData(){
//		runOnUiThread(Runnable {  })
//		runOnUiThread{

//
	}
}