package com.example.tcapp.view.adapter_view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class CreatePostImagesListRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<UriItem>
	) : RecyclerView.Adapter<CreatePostImagesListRecyclerAdapter.ViewHolder>() {
	
	class UriItem{
		var uri:Uri?=null;
		var wasLoaded:Boolean = false;
		constructor(uri : Uri,wasLoaded:Boolean){
			this.uri = uri;
			this.wasLoaded = wasLoaded;
		}
	}
	private lateinit var callback:(String,String?,String?)->Unit
	public fun setCallback(a:(String,String?,String?)->Unit){
		callback = a
	}
	public fun getList():ArrayList<Uri>{
		return ArrayList<Uri>(itemList.map {a->a.uri!!}) ;
	}
	private fun removeItem(uri:Uri?){
		val index = itemList.indexOfFirst { it.uri==uri; }
		if(index!=-1)itemList.removeAt(index);
//		this.notifyDataSetChanged();
		this.notifyItemRemoved(index)
	}
	fun addList(list:ArrayList<Uri>){
		list.forEach {
			itemList.add(UriItem(it,false));
		}
		this.notifyDataSetChanged()
	}
	
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val image: ImageView = itemView.findViewById(R.id.componentCreatePostImage)
		val remove: ImageView = itemView.findViewById(R.id.componentCreatePostRemove)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_create_post_images_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val uriItem = itemList[position];
		
		try{
			if(uriItem.wasLoaded)return;
			Glide.with(context)
				.load(uriItem.uri)
				.apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
				.into(holder.image)
			holder.image.setOnClickListener {
				holder.remove.visibility =
					if(holder.remove.visibility==View.GONE)View.VISIBLE else View.GONE;
			}
			holder.remove.setOnClickListener {
				removeItem(uriItem.uri);
			}
			itemList[position].wasLoaded = true;
		}catch(e:Exception){}
	}
	override fun getItemCount() = itemList.size
	
	
}