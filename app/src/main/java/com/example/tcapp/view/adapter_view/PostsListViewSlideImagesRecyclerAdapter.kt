package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral


class PostsListViewSlideImagesRecyclerAdapter(
	private var context: Context,
	private var itemList: ArrayList<String?>
	) : RecyclerView.Adapter<PostsListViewSlideImagesRecyclerAdapter.ViewHolder>() {
	
	fun setInitList(list:ArrayList<String?>){
		itemList = list
		println(itemList.size)
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val image: ImageView = itemView.findViewById(R.id.componentListPostsViewSlideImagesItemImage)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_list_posts_view_slide_images_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val image = itemList[position];
		
		try{
			Genaral.setPostImageWithPlaceholder(context , image , holder.image)
		}catch(e:Exception){}
	}
	
	override fun getItemCount() = itemList.size
}