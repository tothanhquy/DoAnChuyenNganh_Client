package com.example.tcapp.view.adapter_view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.MessageModels
import com.example.tcapp.model.notification.NotificationModels


class MyNotificationsRecyclerAdapter(
	private var context: Context ,
	private var itemList: SortedList<NotificationModels.Notification>?
	) : RecyclerView.Adapter<MyNotificationsRecyclerAdapter.ViewHolder>() {

	public var isFinish:Boolean=false;
	public var firstTime:Long=0L;

	private fun updateFirstTime(){
		var min = 0L
		for(i in 0 until itemCount){
			if(min==0L||itemList!![i].time<min){
				min = itemList!![i].time
			}
		}
		firstTime = min;
	}

	private lateinit var userReadCallback:(String?)->Unit
	public fun setCallbackOfUserRead(a:(String?)->Unit){
		userReadCallback = a
	}
	private lateinit var openDirectActivityCallback:(String?)->Unit
	public fun setCallbackOfOpenDirectActivity(a:(String?)->Unit){
		openDirectActivityCallback = a
	}
	
	fun setInit(){
		//init list
		itemList = SortedList<NotificationModels.Notification>(NotificationModels.Notification::class.java, object : SortedList.Callback<NotificationModels.Notification>() {
			override fun compare(o1: NotificationModels.Notification, o2: NotificationModels.Notification): Int {
				return (o2.time-o1.time).toInt()
			}

			override fun onChanged(position: Int, count: Int) {
				notifyItemRangeChanged(position, count)
			}

			override fun areContentsTheSame(oldItem: NotificationModels.Notification, newItem: NotificationModels.Notification): Boolean {
				return oldItem.id.equals(newItem.id)
			}

			override fun areItemsTheSame(item1: NotificationModels.Notification, item2: NotificationModels.Notification): Boolean {
				return item1.id.equals(item2.id)
			}

			override fun onInserted(position: Int, count: Int) {
				notifyItemRangeInserted(position, count)
			}

			override fun onRemoved(position: Int, count: Int) {
				notifyItemRangeRemoved(position, count)
			}

			override fun onMoved(fromPosition: Int, toPosition: Int) {
				notifyItemMoved(fromPosition, toPosition)
			}
		})
	}
	fun changeOrAdd(item:NotificationModels.Notification){
		for(i in 0 until itemCount){
			if(itemList!![i].id==item.id){
				itemList!!.updateItemAt(i,item)
				this.notifyItemChanged(i)
				return;
			}
		}
		itemList!!.add(item)
		updateFirstTime()
	}
	fun insertMany(items:ArrayList<NotificationModels.Notification>?){
		if(items != null){
			items!!.forEach {
				changeOrAdd(it)
			}
		}
	}
	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val notReadIcon: ImageView = itemView.findViewById(R.id.componentMyNotificationItemNotReadIcon)
		val content: TextView = itemView.findViewById(R.id.componentMyNotificationItemContent)
		val time: TextView = itemView.findViewById(R.id.componentMyNotificationItemTime)
		val layout: TextView = itemView.findViewById(R.id.componentMyNotificationItemLayout)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_my_notifications_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item = itemList!![position];

		holder.content.text = item.content;
		holder.time.text = Genaral.getMinimizeDateTimeByUTC(item.time);

		if(item.wasRead==true){
			holder.layout.setTextAppearance(context,R.style.NotificationItemReadBgColor)
			holder.notReadIcon.visibility = View.GONE
		}else{
			holder.layout.setTextAppearance(context,R.style.NotificationItemNotReadBgColor)
			holder.notReadIcon.visibility = View.VISIBLE
		}

		holder.item.setOnClickListener {
			if(!item.wasRead){
				userReadCallback(item.id)
				item.wasRead=true;
				this.notifyItemChanged(position);
			}
			if(item.directLink!=null){
				openDirectActivityCallback(item.directLink)
			}
		}
	}
	
	override fun getItemCount() = if(itemList==null)0 else itemList!!.size()
}