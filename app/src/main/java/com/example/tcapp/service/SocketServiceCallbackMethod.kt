package com.example.tcapp.service

class SocketServiceCallbackMethod {
    class EventName {
        companion object {
            public final const val ChanelChatUserSeen: String = "chanel-chat-user-seen";
            public final const val ChanelChatNotifiLastMessage: String = "chanel-chat-notifi-last-message";
            public final const val ChanelChatNewMessages: String = "chanel-chat-new-messages";
            public final const val ChanelChatYouHasNewChanel: String = "chanel-chat-you-has-new-chanel";
            public final const val NotificationNew: String = "notification-new";
        }
        constructor(){}
    }
    public lateinit var events:ArrayList<Event>;
    constructor(){
        events = ArrayList()
        events.add(Event(EventName.ChanelChatUserSeen))
        events.add(Event(EventName.ChanelChatNewMessages))
        events.add(Event(EventName.ChanelChatNotifiLastMessage))
        events.add(Event(EventName.NotificationNew))
        events.add(Event(EventName.ChanelChatYouHasNewChanel))


    }
    public fun registerMethod(eventName:String, serviceName: String, method: (String?) -> Unit){
        events.forEach {
            if(it.name==eventName){
                if(!it.isExistService(serviceName)){
                    it.addServiceMethod(serviceName,method)
                }
            }
        }
    }
    public fun destroyAllMethodsOfService(serviceName: String){
        events.forEach {
            it.removeServiceMethod(serviceName)
        }
    }

    class Event{
        public var name:String="";
        public var methods:ArrayList<ServiceMethod> = ArrayList();
        constructor(name:String){
            this.name = name
        }
        fun removeServiceMethod(serviceName:String){
            val count = countList;
           for(i in count-1 downTo 0){
               if(this.methods[i].serviceName==serviceName){
                   this.methods.removeAt(i)
               }
           }
        }
        fun isExistService(serviceName:String):Boolean{
            this.methods.forEach {
                if(it.serviceName==serviceName)return true;
            }
            return false;
        }
        fun addServiceMethod(serviceName:String,method:(String?)->Unit){
            this.methods.add(ServiceMethod(name,method))
        }
        public val countList = this.methods.size

        class ServiceMethod{
            public var serviceName:String="";
            public lateinit var callbackMethod:(String?)->Unit;
            constructor(name:String,method:(String?)->Unit){
                this.serviceName = name;
                this.callbackMethod=method;
            }
        }
    }
}