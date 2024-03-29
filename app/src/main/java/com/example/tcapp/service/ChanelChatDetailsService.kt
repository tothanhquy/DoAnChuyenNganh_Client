package com.example.tcapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.chanel_chat.MessageModels
import com.google.gson.Gson
import java.net.URISyntaxException


class ChanelChatDetailsService() : Service() {
    private final val SERVICE_NAME = "chanel-chat-details"
    private val mBinder: IBinder = ChanelChatDetailsServiceBinder()
    var mStartMode = 0
    private var mAllowRebind = true

    private var socketService:SocketService?=null
    private var isBoundSocketService:Boolean=false

    private var joinRoomFlag:Int=0
    private fun setSocketService(service:SocketService){
        this.socketService = service
    }
    private fun setIsBoundSocketService(isBound:Boolean){
        this.isBoundSocketService=isBound
    }
    private fun createdServiceCallback(){
        if(isBoundSocketService){
            socketService?.serviceRegisterMethod(SocketServiceCallbackMethod.EventName.ChanelChatNewMessages,SERVICE_NAME,::chanelChatNewMessagesSocketServiceCallback)
            socketService?.serviceRegisterMethod(SocketServiceCallbackMethod.EventName.ChanelChatUserSeen,SERVICE_NAME,::chanelChatUserSeenSocketServiceCallback)
        }
        joinRoomFlag++;
        if(joinRoomFlag==2){
            if(isBoundSocketService){
                socketService?.emit("join_real_chat_chanel_chat_room",ChanelChatJoinRoom(chanelChatId) as Object)
            }
        }
    }
    private var mConnectionService: ServiceConnection = ConnectionService.getSocketConnection(::setIsBoundSocketService,::setSocketService,::createdServiceCallback)

    private var chanelChatId:String?=null

    public inner class ChanelChatDetailsServiceBinder : Binder() {
        val service: ChanelChatDetailsService
            get() = this@ChanelChatDetailsService
    }

    override fun onCreate() {
        // The service is being created
        // setup socket
        try {
            val intent = Intent(this, SocketService::class.java)
            bindService(intent,mConnectionService , BIND_AUTO_CREATE)

        } catch (e: URISyntaxException) {
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Service is starting, due to a call to startService()
        return mStartMode
    }
    override fun onUnbind(intent: Intent?): Boolean {
        // All clients have unbound with unbindService()
        return mAllowRebind
    }
    data class ChanelChatJoinRoom(var id_chanel_chat:String?){}
    override fun onBind(intent: Intent?): IBinder? {
        chanelChatId = intent?.getStringExtra("chanelChatId")
        joinRoomFlag++;
        if(joinRoomFlag==2){
            if(isBoundSocketService){
                socketService?.emit("join_real_chat_chanel_chat_room",ChanelChatJoinRoom(chanelChatId) as Object)
            }
        }
        return mBinder
    }
    override fun onRebind(intent: Intent?) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    override fun onDestroy() {
        if(isBoundSocketService){
            socketService?.emit("out_real_chat_chanel_chat_room",ChanelChatJoinRoom(chanelChatId) as Object)
            socketService?.serviceDestroyAllMethods(SERVICE_NAME)
        }
        unbindService(mConnectionService);
        setIsBoundSocketService(false)
        // The service is no longer used and is being destroyed
    }

    private lateinit var chanelChatNewMessagesCallback:(messages:ArrayList<MessageModels.NewMessageSocket>?)->Unit;
    public fun setChanelChatNewMessagesCallback(callback:(messages:ArrayList<MessageModels.NewMessageSocket>?)->Unit){
        chanelChatNewMessagesCallback = callback
    }
    private fun chanelChatNewMessagesSocketServiceCallback(data:String?){
        var res = MessageModels.NewMessagesSocket();
        try{
            res = Gson().fromJson(data, MessageModels.NewMessagesSocket::class.java)
        }catch (e:Exception){
            println(e.toString())
        }
        chanelChatNewMessagesCallback(res.messages)
    }
    private lateinit var chanelChatUserSeenCallback:(ChanelChatModels.UserSeenSocket?)->Unit;
    public fun setChanelChatUserSeenCallback(callback:(ChanelChatModels.UserSeenSocket?)->Unit){
        chanelChatUserSeenCallback = callback
    }
    private fun chanelChatUserSeenSocketServiceCallback(data:String?){
        var res = ChanelChatModels.UserSeenSocket();
        println("socket service user seen: $data")
        try{
            res = Gson().fromJson(data, ChanelChatModels.UserSeenSocket::class.java)
        }catch (e:Exception){
            println(e.toString())
        }
        chanelChatUserSeenCallback(res)
    }


}