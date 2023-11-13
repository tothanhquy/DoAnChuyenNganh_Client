package com.example.tcapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.google.gson.Gson
import java.net.URISyntaxException


class MyChanelChatsService() : Service() {
    private final val SERVICE_NAME = "my-chanel-chats"
    private val mBinder: IBinder = MyChanelChatsServiceBinder()
    var mStartMode = 0
    private var mAllowRebind = true

    private var socketService:SocketService?=null
    private var isBoundSocketService:Boolean=false
    private fun setSocketService(service:SocketService){
        this.socketService = service
    }
    private fun setIsBoundSocketService(isBound:Boolean){
        this.isBoundSocketService=isBound
    }
    private var mConnectionService: ServiceConnection = ConnectionService.getSocketConnection(::setIsBoundSocketService,::setSocketService)


    public inner class MyChanelChatsServiceBinder : Binder() {
        val service: MyChanelChatsService
            get() = this@MyChanelChatsService
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
        if(isBoundSocketService){
            socketService?.serviceRegisterMethod("chanel-chat-notifi-last-message",SERVICE_NAME,::chanelChatNotifiLastMessageSocketServiceCallback)
            socketService?.serviceRegisterMethod("chanel-chat-you-has-new-chanel",SERVICE_NAME,::chanelChatYouHasNewChanelSocketServiceCallback)

        }

        return mStartMode
    }
    override fun onUnbind(intent: Intent?): Boolean {
        // All clients have unbound with unbindService()
        return mAllowRebind
    }
    override fun onBind(intent: Intent?): IBinder? {
        if(isBoundSocketService){
            socketService?.emit("join_chanel_chat_room",null)
        }
        return mBinder
    }
    override fun onRebind(intent: Intent?) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    override fun onDestroy() {
        socketService?.serviceDestroyAllMethods(SERVICE_NAME)
        unbindService(mConnectionService);
        setIsBoundSocketService(false)
        // The service is no longer used and is being destroyed
    }

    private lateinit var chanelChatYouHasNewChanelCallback:(String?)->Unit;
    public fun setChanelChatYouHasNewChanelCallback(callback:(String?)->Unit){
        chanelChatYouHasNewChanelCallback = callback
    }
    private fun chanelChatYouHasNewChanelSocketServiceCallback(data:String?){
        chanelChatYouHasNewChanelCallback(data)//idUser
    }
    private lateinit var chanelChatNotifiLastMessageCallback:(ChanelChatModels.LastNewMessageSocket?)->Unit;
    public fun setChanelChatNotifiLastMessageCallback(callback:(ChanelChatModels.LastNewMessageSocket?)->Unit){
        chanelChatNotifiLastMessageCallback = callback
    }
    private fun chanelChatNotifiLastMessageSocketServiceCallback(data:String?){
        var res = ChanelChatModels.LastNewMessageSocket();
        try{
            res = Gson().fromJson(data, ChanelChatModels.LastNewMessageSocket::class.java)
        }catch (e:Exception){}
        chanelChatNotifiLastMessageCallback(res)
    }


}