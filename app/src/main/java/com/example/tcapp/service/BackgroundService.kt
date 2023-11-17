package com.example.tcapp.service

import android.app.Service
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.IBinder
import com.example.tcapp.api.API
import com.example.tcapp.model.notification.NotificationModels
import com.google.gson.Gson
import java.net.URISyntaxException


class BackgroundService() : Service() {
    private final val SERVICE_NAME = "notification"
    private val mBinder: IBinder = NotificationServiceBinder()
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


    public inner class NotificationServiceBinder : Binder() {
        val service: BackgroundService
            get() = this@BackgroundService
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

    private fun notificationNewSocketServiceCallback(data:String?){
        getNotification(data);
    }
    private fun getNotification(id:String?){
        try{
            val  response : API.ResponseAPI = API.getResponse(applicationContext,
                khttp.get(
                    url = API.getBaseUrl() + "/Notification/GetNotification",
                    cookies = mapOf("auth" to API.getAuth(applicationContext)),
                    params= mapOf(
                        "id_notification" to (id?:"")
                    )
                )
            )

            if(response.code==1||response.code==404){
                //system error
            }else if(response.code==403){
                //not authen
            }else{
                if(response.status=="Success"){
                    val notificationCrude = Gson().fromJson(response.data.toString(), NotificationModels.NotificationCrude::class.java)
                    getNotificationOkCallback(NotificationModels().Notification(notificationCrude))
                }else{
                }
            }
        }catch(err:Exception){
            println(err.toString())
        }
    }
    private fun getNotificationOkCallback(notifi:NotificationModels.Notification){

    }

}