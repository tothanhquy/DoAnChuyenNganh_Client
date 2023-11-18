package com.example.tcapp.service

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.example.tcapp.api.API
import com.example.tcapp.core.NotificationSystem
import com.example.tcapp.model.chanel_chat.ChanelChatModels
import com.example.tcapp.model.notification.NotificationModels
import com.google.gson.Gson
import java.net.URISyntaxException


class MyNotificationsService() : Service() {
    private final val SERVICE_NAME = "my-notifications"
    private val mBinder: IBinder = MyNotificationsServiceBinder()
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
    private fun createdServiceCallback(){
        if(isBoundSocketService){
            socketService?.serviceRegisterMethod(SocketServiceCallbackMethod.EventName.NotificationNew,SERVICE_NAME,::notificationNewSocketServiceCallback)
        }
    }
    private var mConnectionService: ServiceConnection = ConnectionService.getSocketConnection(::setIsBoundSocketService,::setSocketService,::createdServiceCallback)


    public inner class MyNotificationsServiceBinder : Binder() {
        val service: MyNotificationsService
            get() = this@MyNotificationsService
    }

    override fun onCreate() {
        // The service is being created
        // setup socket
        try {
            val intent = Intent(this, SocketService::class.java)
            bindService(intent,mConnectionService , BIND_AUTO_CREATE)

        } catch (e: URISyntaxException) {
            println(e.toString())
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
    override fun onBind(intent: Intent?): IBinder? {
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

    private lateinit var notificationNewCallback:(String?)->Unit;
    public fun setNotificationNewCallback(callback:(String?)->Unit){
        notificationNewCallback = callback
    }
    private fun notificationNewSocketServiceCallback(data:String?){
        notificationNewCallback(data);
    }
}