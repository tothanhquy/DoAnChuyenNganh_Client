package com.example.tcapp.view.adapter_view

import android.content.Context
import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.project.ProjectModels


class ProjectResourceRecyclerAdapter(
	private var context: Context,
	private var thisContext: Context,
	private var itemList: ArrayList<ProjectModels.Resource>
	) : RecyclerView.Adapter<ProjectResourceRecyclerAdapter.ViewHolder>() {
	
	private var isActionAle:Boolean = false;
	private var resourceType:String = "";

	private lateinit var callbackOfOpenOptions:(String?,String?)->Unit
	public fun setCallbackOfOpenOptions(a:(String?,String?)->Unit){
		callbackOfOpenOptions = a
	}

	fun setInitList(list:ArrayList<ProjectModels.Resource>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun setActionAble(isActionAle:Boolean=false){
		this.isActionAle = isActionAle;
	}
	fun setType(type:String){
		this.resourceType = type;
	}
	fun deleteItem(path:String?){
		if(path!=null){
			val indexMember =  itemList.indexOfFirst{it.path==path}
			if(indexMember!=-1){
				itemList.removeAt(indexMember);
				this.notifyItemRemoved(indexMember)
			}
		}
	}
	fun add(item:ProjectModels.Resource){
		itemList.add(item)
//		this.notifyItemInserted(itemCount-1)
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val image: ImageView = itemView.findViewById(R.id.componentProjectResourceItemImage)
		val video: VideoView = itemView.findViewById(R.id.componentProjectResourceItemVideo)
		val alt: TextView = itemView.findViewById(R.id.componentProjectResourceItemAlt)
		val btnAction:ImageView = itemView.findViewById(R.id.componentProjectResourceItemOptionButton)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_project_resource_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList[position];
		println("position:$position")
		try{
			if(resourceType=="video"){
				holder.image.visibility=View.GONE
				holder.video.visibility=View.VISIBLE
				holder.video.stopPlayback()
				val urlPath = Genaral.getProjectResourcePath()
				val uri = Uri.parse(urlPath+item.path)
				// sets the resource from the
				// videoUrl to the videoView

//				holder.video.requestFocus()
				// creating object of
				// media controller class
				val mediaController = MediaController(thisContext)
				// sets the anchor view
				// anchor view for the videoView
				mediaController.setAnchorView(holder.video)
				// sets the media player to the videoView
				mediaController.setMediaPlayer(holder.video)
				// sets the media controller to the videoView
				holder.video.setMediaController(mediaController)
				// starts the video
//				println(holder.video.height)
				holder.video.setVideoURI(uri)
//				holder.video.setVideoPath(urlPath+item.path)
//				holder.video.start()
				holder.video.setOnPreparedListener(OnPreparedListener { mp ->
					mp.isLooping = false
//					holder.video.start()
				})
			}else{
				val urlPath = Genaral.getProjectResourcePath()
				println(urlPath+item.path)
				Genaral.setProjectImageResourceWithPlaceholder(context , item.path , holder.image)
				holder.image.visibility=View.VISIBLE
				holder.video.visibility=View.GONE
			}
			holder.alt.text = item.alt;
			holder.btnAction.setOnClickListener {
				callbackOfOpenOptions(item.path,item.alt)
			}
			if(isActionAle)
				holder.btnAction.visibility = View.VISIBLE;
			else
				holder.btnAction.visibility = View.GONE;
		}catch(e:Exception){
			println(e.toString())
		}
	}
	
	override fun getItemCount() = itemList.size
}