package com.example.tcapp.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.example.tcapp.api.API
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException


public class SocketService() : Service() {
    private val mBinder: IBinder = SocketBinder()
    var mStartMode = 0
    private var mAllowRebind = true

    private var mSocket:Socket?=null;

    private var socketServiceCallbackMethod = SocketServiceCallbackMethod()

    public inner class SocketBinder : Binder() {
        val service: SocketService
            get() = this@SocketService
    }

    public fun serviceDestroyAllMethods(serviceName:String){
        socketServiceCallbackMethod.destroyAllMethodsOfService(serviceName)
    }
    public fun serviceRegisterMethod(eventName:String, serviceName: String, method: (String?) -> Unit){
        socketServiceCallbackMethod.registerMethod(eventName,serviceName,method)
    }

    override fun onCreate() {
        // The service is being created
        // setup socket
        try {
            mSocket = IO.socket(API.getBaseUrl())

            socketServiceCallbackMethod.events.forEach {
                val onNewListener =
                    Emitter.Listener { args ->
                        for(i in 0 until it.methods.size){
                            it.methods[i].callbackMethod(args[0] as String)
                        }
                    }
                mSocket?.on(it.name,onNewListener)
            }

            mSocket?.connect();
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
    override fun onBind(intent: Intent?): IBinder? {
        emit("join_personal_room",null)
        return mBinder
    }
    override fun onRebind(intent: Intent?) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
    }
    override fun onDestroy() {
        mSocket?.disconnect();
        // The service is no longer used and is being destroyed
    }

    class SocketEmitObject(var jwt:String?,data:Any?){}
    public fun emit(event:String,data:Any?){
        val jwt = API.getAuth(applicationContext)
        val sendData = SocketEmitObject(jwt,data)
        mSocket?.emit(event, sendData);
    }

}