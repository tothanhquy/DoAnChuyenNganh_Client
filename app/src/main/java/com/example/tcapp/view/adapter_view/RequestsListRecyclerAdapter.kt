package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.request.RequestModels
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.model.user_profile.UserProfileModels


class RequestsListRecyclerAdapter(
	private var context: Context ,
	private var itemList: ArrayList<RequestModels.RequestsListItem>
	) : RecyclerView.Adapter<RequestsListRecyclerAdapter.ViewHolder>() {
	
	private lateinit var method:RequestModels.RequestMethod;
	private lateinit var viewer:RequestModels.RequestViewer;
	
	//default show all
	private var isImportantSelect:Boolean = true;
	private var notImportantSelect:Boolean = true;
	private var wasReadedSelect:Boolean = true;
	private var notReadSelect:Boolean = true;
	
	public var timePrevious:Long=0;
	public var wasLoaded:Boolean=false;
	
	
	private lateinit var callback:(String)->Unit
	public fun setCallback(a:(String)->Unit){
		callback = a
	}
	fun setMethod(method:RequestModels.RequestMethod){
		this.method = method;
	}
	fun setViewer(viewer:RequestModels.RequestViewer){
		this.viewer = viewer;
	}
	fun setFilter(isImportant:Boolean,notImportant:Boolean,wasReaded:Boolean,notRead:Boolean){
		val isRefresh =
			(this.isImportantSelect != isImportant)||
			(this.notImportantSelect != notImportant)||
			(this.wasReadedSelect != wasReaded)||
			(this.notReadSelect != notRead);
		this.isImportantSelect = isImportant;
		this.notImportantSelect = notImportant;
		this.wasReadedSelect = wasReaded;
		this.notReadSelect = notRead;
		if(isRefresh)this.notifyDataSetChanged()
	}
	fun setInitList(list:ArrayList<RequestModels.RequestsListItem>){
		itemList = list
		this.notifyDataSetChanged()
	}
	fun addList(list:ArrayList<RequestModels.RequestsListItem>){
		val previousSize = itemList.size
		itemList.addAll(previousSize,list);
		this.notifyDataSetChanged()
	}
	fun setReadedStatus(position : Int){
		itemList[position].wasReaded=true;
		this.notifyDataSetChanged()
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val fromAvatar: ImageView = itemView.findViewById(R.id.componentRequestsListItemFromAvatar)
		val fromName: TextView = itemView.findViewById(R.id.componentRequestsListItemFromName)
		val toAvatar: ImageView = itemView.findViewById(R.id.componentRequestsListItemToAvatar)
		val toName: TextView = itemView.findViewById(R.id.componentRequestsListItemToName)
		val dateTime: TextView = itemView.findViewById(R.id.componentRequestsListItemDate)
		val status: TextView = itemView.findViewById(R.id.componentRequestsListItemStatus)
		val isImportant: ImageView = itemView.findViewById(R.id.componentRequestsListItemIsImportant)
		val notRead: ImageView = itemView.findViewById(R.id.componentRequestsListItemNotRead)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_requests_list_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val request = itemList[position];
		try{
			//check filter
			if(
				((this.isImportantSelect&&request.isImportant)
				||(this.notImportantSelect&&!request.isImportant))&&
				((this.wasReadedSelect&&request.wasReaded)
				||(this.notReadSelect&&!request.wasReaded))
			){
				//show
				holder.item.layoutParams = RecyclerView.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT ,
					ViewGroup.LayoutParams.WRAP_CONTENT
				);
			}else{
				//hide
				holder.item.layoutParams = RecyclerView.LayoutParams(0,0);
			}
			holder.status.text = getStatusString(request.isWaiting,request.wasResponsed);
			
			holder.dateTime.text = Genaral.getDateTimeByUTC(request.requestTime);
			
			if(this.method==RequestModels.RequestMethod.SEND){
				holder.isImportant.visibility=View.GONE;
				holder.notRead.visibility=View.GONE;
			}else{
				holder.isImportant.visibility = if(request.isImportant)View.VISIBLE else View.GONE;
				holder.notRead.visibility = if(!request.wasReaded)View.VISIBLE else View.GONE;
			}
			
			if(request.wasLoadData)return;
			
			if(
				(
					this.viewer==RequestModels.RequestViewer.LEADER
						&& this.method==RequestModels.RequestMethod.SEND
					)
				||(
					this.viewer==RequestModels.RequestViewer.USER
						&& this.method==RequestModels.RequestMethod.RECEIVE
					)
			){
				//from team
				Genaral.setTeamAvatarImage(context , request.teamAvatar , holder.fromAvatar)
				holder.fromName.text = request.teamName;
				Genaral.setUserAvatarImage(context , request.userAvatar , holder.toAvatar)
				holder.toName.text = request.userName;
				
			}else{
				println(request.userName+request.userAvatar+request.teamName+request.teamAvatar)
				//from user
				Genaral.setUserAvatarImage(context , request.userAvatar , holder.fromAvatar)
				holder.fromName.text = request.userName;
				Genaral.setTeamAvatarImage(context , request.teamAvatar , holder.toAvatar)
				holder.toName.text = request.teamName;
			}
			
			holder.item.setOnClickListener {
				if(!request.wasReaded)setReadedStatus(position)
				callback(request.id!!)
			}
			request.wasLoadData = true;
		}catch(e:Exception){
			println(e.toString())
		}
	}
	private fun getStatusString(isWaiting:Boolean,wasResponsed:Boolean):String{
		if(isWaiting)return "Đang chờ phản hồi";
		if(wasResponsed)return "Đã trả lời";
		return "Đã hủy";
	}
	override fun getItemCount() = itemList.size
}