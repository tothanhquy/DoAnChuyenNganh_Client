package com.example.tcapp.view.adapter_view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.example.tcapp.R
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.chanel_chat.MessageModels


class ChanelChatIMessagesRecyclerAdapter(
	var context: Context,
	private var itemList: SortedList<MessageModels.Message>?
//	private var itemList: ArrayList<MessageModels.Message>?
	) : RecyclerView.Adapter<ChanelChatIMessagesRecyclerAdapter.ViewHolder>() {

	init {
//		itemList=ArrayList()
		//init list
		itemList = SortedList<MessageModels.Message>(MessageModels.Message::class.java, object : SortedList.Callback<MessageModels.Message>() {
			override fun compare(o1: MessageModels.Message, o2: MessageModels.Message): Int {
				return (o1.time-o2.time).toInt()
			}

			override fun onChanged(position: Int, count: Int) {
				notifyItemRangeChanged(position, count)
			}

			override fun areContentsTheSame(oldItem: MessageModels.Message, newItem: MessageModels.Message): Boolean {
				return oldItem.id.equals(newItem.id)
			}

			override fun areItemsTheSame(item1: MessageModels.Message, item2: MessageModels.Message): Boolean {
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

	private var isFinish:Boolean = false;
	private var startTime:Long = -1;
	private var lastTime:Long = -1;
	public var lastMessageId:String? = null;
	private var accountId:String?=null;

	private var members:ArrayList<ChanelChatModels.Member> = ArrayList()
	private var usersSeen:ArrayList<ChanelChatModels.LastTimeMemberSeen> = ArrayList()
	//private var astUsersSeenPosition:ArrayList<LastUserSeenPosition> = ArrayList()

	private var replyIdNeedToScrollTo:String?=null;
	fun setInitList(messages:MessageModels.Messages?){

		if(messages != null){
//			this.itemList!!.add(messages!!.messages?.get(0)!!)
//			this.itemList!!.beginBatchedUpdates()
			messages.messages!!.forEach {
				if(!isExistId(it.id)){
					this.itemList!!.add(it)
				}

//				println("count:$itemCount id:${it.id}")
			}
			isFinish = messages.isFinish
			updateStartLastTime()
//			this.itemList!!.endBatchedUpdates()
			this.notifyDataSetChanged()
		}
	}
	fun setAccountId(accountId:String?){
		this.accountId = accountId
	}
	fun setMembers(members:ArrayList<ChanelChatModels.Member>){
		this.members = members
	}
	fun setUsersSeen(usersSeen:ArrayList<ChanelChatModels.LastTimeMemberSeen>){
		this.usersSeen = usersSeen
	}

//	class LastUserSeenPosition{
//		public var idUser:String="";
//		public var position:Int=0;
//	}

	fun updateUserSeen(idUser:String?,idLastMessage:String?){
		var beforeId:String? = "";
		usersSeen.forEach {
			if(it.userId==idUser){
				beforeId = it.messageId
				it.messageId = idLastMessage;
			}
		}
		for(i in 0 until getItemCount()){
			if(itemList!![i].id==beforeId){
				this.notifyItemChanged(i)
			}else if(itemList!![i].id==idLastMessage){
				this.notifyItemChanged(i)
			}
		}
	}

	private fun updateStartLastTime(){
		for (i in 0 until itemCount){
			if(this.startTime==-1L||this.startTime>itemList!![i].time){
				this.startTime = itemList!![i].time
			}
			if(this.lastTime==-1L||this.lastTime<itemList!![i].time){
				this.lastTime = itemList!![i].time
				this.lastMessageId = itemList!![i].id
			}
		}
	}

	private lateinit var callbackOfViewUser:(String)->Unit
	public fun setCallbackOfViewUser(a:(String)->Unit){
		callbackOfViewUser = a
	}
	private lateinit var callbackLongTouchMessage:(String?,String?)->Unit
	public fun setCallbackLongTouchMessage(a:(String?,String?)->Unit){
		callbackLongTouchMessage = a
	}
	private lateinit var callbackLoadHistoryMessages:(lastTime:Long)->Unit
	public fun setCallbackLoadHistoryMessages(a:(lastTime:Long)->Unit){
		callbackLoadHistoryMessages = a
	}
	private lateinit var callbackLoadMessagesBetweenTime:(startTime:Long,lastTime:Long)->Unit
	public fun setCallbackLoadMessagesBetweenTime(a:(startTime:Long,lastTime:Long)->Unit){
		callbackLoadMessagesBetweenTime = a
	}
	private lateinit var callbackScrollRecyclerView:(position:Int)->Unit
	public fun setCallbackScrollRecyclerView(a:(position:Int)->Unit){
		callbackScrollRecyclerView = a
	}

	fun insertMessagesBefore(messages:MessageModels.Messages?){
		if(messages!=null){
			this.isFinish = messages.isFinish
			insertMessages(messages.messages)
		}
	}
	private fun isExistId(id:String?):Boolean{
		for(i in 0 until itemCount){
			if(this.itemList!![i].id==id)return true;
		}
		return false;
	}
	fun insertMessages(messages:ArrayList<MessageModels.Message>?){
		if(messages != null){
			messages!!.forEach {
				if(isExistId(it.id)){
					this.itemList!!.add(it)
					println("insert:$itemCount")
				}
			}
			updateStartLastTime()
		}

		updateStartLastTime()
		if(replyIdNeedToScrollTo!=null){
			gotoReplyMessage(replyIdNeedToScrollTo)
		}
		//change update

	}

	private fun gotoReplyMessage(replyId:String?,replyTime:Long=0){
		var isExist:Boolean=false;
		for (i in 0 until itemCount){
			if(itemList!![i].id==replyId){
				callbackScrollRecyclerView(i)
				replyIdNeedToScrollTo=null
				isExist=true;
				return;
			}
		}
		if(!isExist){
			//load more
			replyIdNeedToScrollTo=replyId
			callbackLoadMessagesBetweenTime(replyTime,this.startTime)
		}
	}

	class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val space: View = itemView.findViewById(R.id.componentChanelChatMessagesItemSpace)
		val loadMore: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemLoadMoreContainer)
		val timeContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemTimeContainer)
		val time: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemTime)
		val friendContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendContainer)
		val friendContainerContent: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendContainerContent)
		val friendAvatar: ImageView = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendAvatar)
		val friendName: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendName)
		val friendReplyContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendReplyContainer)
		val friendReplyContent: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendReplyContent)
		val friendContent: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendContent)
		val friendTime: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemFriendTime)
		val meContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemMeContainer)
		val meContainerContent: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemMeContainerContent)
		val meReplyContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemMeReplyContainer)
		val meReplyContent: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemMeReplyContent)
		val meContent: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemMeContent)
		val meTime: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemMeTime)
		val usersSeenContainer: LinearLayout = itemView.findViewById(R.id.componentChanelChatMessagesItemUsersSeenContainer)
		val usersSeenNotifi: TextView = itemView.findViewById(R.id.componentChanelChatMessagesItemUsersSeenNotification)
		val item:View = itemView
	}
	
	override fun onCreateViewHolder(parent: ViewGroup , viewType: Int): ViewHolder {
		val itemView = LayoutInflater.from(parent.context).inflate(R.layout.component_chanel_chat_messages_item, parent, false)
		return ViewHolder(itemView)
	}
	
	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val message = itemList!![position];
		val beforeMessage = if(position==0)null else itemList!![position-1];
		val afterMessage = if(position== getItemCount() -1)null else itemList!![position+1];
//		println("position:$position")
//		if(!message.isLoaded){
			//load static data
			if(position==0&&!this.isFinish){
				holder.loadMore.visibility = View.VISIBLE
				holder.loadMore.setOnClickListener {
					callbackLoadHistoryMessages(startTime)
				}
			}else{
				holder.loadMore.visibility = View.GONE
			}
			val isOverTimeBefore = beforeMessage==null||isDifferentTime(beforeMessage.time,message.time)
			val isOverTimeAfter = afterMessage==null||isDifferentTime(afterMessage.time,message.time)
			val isFarMessageBefore = isOverTimeBefore||beforeMessage!!.userId!=message.userId
			val isFarMessageAfter = isOverTimeAfter||afterMessage!!.userId!=message.userId

		println("position:$position time:${message.time} isFarMessageBefore:$isFarMessageBefore isFarMessageAfter:$isFarMessageAfter")

			if(isFarMessageBefore){
				holder.space.visibility=View.VISIBLE
			}else{
				holder.space.visibility=View.GONE
			}
			if(isOverTimeBefore){
				holder.timeContainer.visibility = View.VISIBLE;
				holder.time.text = Genaral.getDateTimeByUTC(message.time)
			}else{
				holder.timeContainer.visibility = View.GONE;
			}

			if(message.userId==accountId){
				//me
				holder.meContainer.visibility = View.VISIBLE;
				holder.friendContainer.visibility = View.GONE;

				if(message.replyId!=null&&message.replyId!=""){
					holder.meReplyContainer.visibility=View.VISIBLE
					holder.meReplyContent.text = message.replyContent
					holder.meReplyContainer.setOnClickListener {
						gotoReplyMessage(message.replyId,message.replyTime)
					}
				}
				holder.meContent.text=message.content
				if(isFarMessageAfter){
					holder.meTime.visibility=View.VISIBLE
					holder.meTime.text = Genaral.getMinimizeDateTimeByUTC(message.time)
				}else{
					holder.meTime.visibility=View.GONE
				}
				holder.meContainerContent.setOnLongClickListener(OnLongClickListener {
					callbackLongTouchMessage(message.id,message.content)
					true
				})
			}else{
				//friend
				holder.friendContainer.visibility = View.VISIBLE;
				holder.meContainer.visibility = View.GONE;

				if(isFarMessageBefore){
					Genaral.setUserAvatarImage(context,getAvatarBaseId(message.userId),holder.friendAvatar)
					holder.friendName.visibility=View.VISIBLE
					holder.friendName.text = getNameBaseId(message.userId)
					holder.friendName.setOnClickListener {
						callbackOfViewUser(message.userId!!)
					}
					holder.friendAvatar.setOnClickListener {
						callbackOfViewUser(message.userId!!)
					}
				}
				if(message.replyId!=null&&message.replyId!=""){
					holder.friendReplyContainer.visibility=View.VISIBLE
					holder.friendReplyContent.text = message.replyContent
					holder.friendReplyContainer.setOnClickListener {
						gotoReplyMessage(message.replyId,message.replyTime)
					}
				}
				holder.friendContent.text=message.content
				if(isFarMessageAfter){
					holder.friendTime.visibility=View.VISIBLE
					holder.friendTime.text = Genaral.getMinimizeDateTimeByUTC(message.time)
				}else{
					holder.friendTime.visibility=View.GONE
				}
				holder.friendContainerContent.setOnLongClickListener(OnLongClickListener {
					callbackLongTouchMessage(message.id,message.content)
					true
				})
			}
			itemList!![position].isLoaded=true
//		}
		//users seen
		val usersSeenNotifi = getNotificationUsersSeenBaseIdMessage(message.id)
		if(usersSeenNotifi.isNotEmpty()){
			holder.usersSeenContainer.visibility=View.VISIBLE
			holder.usersSeenNotifi.text=usersSeenNotifi
		}else{
			holder.usersSeenContainer.visibility=View.GONE
		}
	}

	//true if over 1 hour
	private fun isDifferentTime(time1:Long, time2:Long):Boolean{
		val overTime = 1000*60*60;//1 hour
		return time1-time2>overTime || time1-time2<-overTime;
	}
	private fun getAvatarBaseId(id:String?):String?{
		members.forEach { if(it.id==id)return it.avatar }
		return ""
	}
	private fun getNameBaseId(id:String?):String?{
		members.forEach { if(it.id==id)return it.name }
		return ""
	}
	private fun getNotificationUsersSeenBaseIdMessage(idMessage:String?):String{
		var name = "";
		var numberOfUsers=0;
		usersSeen.forEach {
			if(it.messageId==idMessage){
				numberOfUsers++;
				if(numberOfUsers<=3){
					name+=getNameBaseId(it.userId)+","
				}
			}
		}
		if(name.isEmpty())return "";
		if(numberOfUsers<=3){
			return name.removeRange(name.length-1,name.length-1) + " đã xem."
		}else{
			return name.removeRange(name.length-1,name.length-1) + " + "+ (numberOfUsers-3) + " người khác đã xem."
		}
	}

	override fun getItemCount() = if(itemList!=null)itemList!!.size() else 0

}