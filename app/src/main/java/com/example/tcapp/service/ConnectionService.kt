package com.example.tcapp.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder


class ConnectionService {
    companion object{
        public fun getSocketConnection(setBound:(Boolean)->Unit,setService:(SocketService)->Unit,createdCallback:()->Unit):ServiceConnection{
            val mConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(
                    className: ComponentName,
                    service: IBinder
                ) {
                    val binder = service as SocketService.SocketBinder
                    setService(binder.service)
                    setBound(true)
                    createdCallback()
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    setBound(false)
                }
            }
            return mConnection
        }
        public fun getMyChanelChatsServiceConnection(setBound:(Boolean)->Unit,setService:(MyChanelChatsService)->Unit,createdCallback:()->Unit):ServiceConnection{
            val mConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(
                    className: ComponentName,
                    service: IBinder
                ) {
                    val binder = service as MyChanelChatsService.MyChanelChatsServiceBinder
                    setService(binder.service)
                    setBound(true)
                    createdCallback()
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    setBound(false)
                }
            }
            return mConnection
        }
        public fun getChanelChatDetailsServiceConnection(setBound:(Boolean)->Unit,setService:(ChanelChatDetailsService)->Unit,createdCallback:()->Unit):ServiceConnection{
            val mConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(
                    className: ComponentName,
                    service: IBinder
                ) {
                    val binder = service as ChanelChatDetailsService.ChanelChatDetailsServiceBinder
                    setService(binder.service)
                    setBound(true)
                    createdCallback()
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    setBound(false)
                }
            }
            return mConnection
        }
        public fun getNotificationServiceConnection(setBound:(Boolean)->Unit, setService:(BackgroundService)->Unit, createdCallback:()->Unit):ServiceConnection{
            val mConnection: ServiceConnection = object : ServiceConnection {
                override fun onServiceConnected(
                    className: ComponentName,
                    service: IBinder
                ) {
                    val binder = service as BackgroundService.NotificationServiceBinder
                    setService(binder.service)
                    setBound(true)
                    createdCallback()
                }

                override fun onServiceDisconnected(arg0: ComponentName) {
                    setBound(false)
                }
            }
            return mConnection
        }
    }

}