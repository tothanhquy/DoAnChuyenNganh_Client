package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral


class PostEditOldImagesRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<String>
	) : RecyclerView.Adapter<PostEditOldImagesRecyclerAdapter.ViewHolder>() {
	fun getResList():ArrayList<String>{
		return itemList
	}

	fun setInitList(list:ArrayList<String>){
		itemList = list
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val image: ImageView = itemView.findViewById(R.id.componentPostEditOldImagesItemImage)
		val delete: ImageView = itemView.findViewById(R.id.componentPostEditOldImagesItemDelete)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_post_edit_old_images_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val image = itemList[position];
		try{
			Genaral.setPostImageWithPlaceholder(context , image , holder.image)
			holder.delete.setOnClickListener {
				itemList.removeAt(position);
				this.notifyItemRemoved(position)
			}
			holder.image.setOnClickListener {
				holder.delete.visibility =
					if(holder.delete.visibility==View.GONE)View.VISIBLE else View.GONE;
			}
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}