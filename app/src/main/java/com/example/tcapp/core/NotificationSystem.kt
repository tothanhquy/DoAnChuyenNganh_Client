package com.example.tcapp.core

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.StatusBarNotification
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.tcapp.R
import com.example.tcapp.model.friend.FriendModels
import com.example.tcapp.model.notification.NotificationModels
import com.example.tcapp.view.home.HomeActivity
import com.example.tcapp.view.chanel_chat.ChanelChatDetailsActivity
import com.example.tcapp.view.chanel_chat.MyChanelChatsActivity
import com.example.tcapp.view.friend.ViewFriendRequestActivity
import com.example.tcapp.view.post.PostDetailsActivity
import com.example.tcapp.view.project.ProjectDetailsActivity
import com.example.tcapp.view.project.ProjectInvitingMembersActivity
import com.example.tcapp.view.project.ProjectViewMembersNowActivity
import com.example.tcapp.view.team_profile.TeamProfileActivity
import com.example.tcapp.view.team_profile.TeamProfileViewMembersListActivity
import com.example.tcapp.view.user_profile.GuestUserProfileActivity


class NotificationSystem {
    public final val CHANEL_NOTIFICATION_ID = "mfksmdfnmasindasindiuansnfg";
    public fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "TCaPP channel notification"
            val description: String = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANEL_NOTIFICATION_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager: NotificationManager? = getSystemService(
                context,
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
    public fun addNotification(context: Context,notification: NotificationShowModel?){
        if(notification==null||notification.id==null)return;
        val notificationId = convertKeyToNotificationId(notification.id);
        if(isExistNotificationActive(context,notificationId)){
            //update
            showNotification(context,notificationId,getNotificationObject(context,notification))
        }else{
            //create
            showNotification(context,notificationId,getNotificationObject(context,notification))
        }
    }
    private fun showNotification(context: Context, notificationId:Int, mBuilder:NotificationCompat.Builder){
        val notificationManager:NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, mBuilder.build())
    }
    private fun convertKeyToNotificationId(key:String?):Int{
        return key.hashCode();
    }
    private fun isExistNotificationActive(context: Context, notificationId: Int): Boolean {
        val notificationManager:NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifications: Array<StatusBarNotification> =
            notificationManager.activeNotifications
        for (notification in notifications) {
            if (notification.id == notificationId) {
                return true;
            }
        }
        return false
    }
    private fun getNotificationObject(context: Context, data:NotificationShowModel):NotificationCompat.Builder{
        val spanContent = NotificationModels.getContentWithStyle(data.content,data.styleRanges)
        val mBuilder: NotificationCompat.Builder=NotificationCompat.Builder(context, CHANEL_NOTIFICATION_ID)
            .setSmallIcon(R.drawable.logo_icon)
            .setContentText(spanContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(spanContent))
            .setAutoCancel(true);
        if(data.directLink!=null){
            val pendingIntent = getPendingIntentOpenActivity(context,getIntentOpenActivity(context,data.directLink))
            if(pendingIntent!=null)mBuilder.setContentIntent(pendingIntent);
        }
        return mBuilder;
    }

    class NotificationShowModel{
        public var id:String?=null;
        public var content:String?=null;
        public var directLink:String?=null;
        public var styleRanges: ArrayList<NotificationModels.StyleRange>?=null;
        constructor()
        constructor(notification:NotificationModels.Notification){
            this.id = notification.id;
            this.content = notification.content;
            this.directLink = notification.directLink;
            this.styleRanges = notification.styleRanges;
        }

        constructor(
            id: String?,
            content: String?,
            directLink: String?,
            styleRanges: ArrayList<NotificationModels.StyleRange>?
        ) {
            this.id = id
            this.content = content
            this.directLink = directLink
            this.styleRanges = styleRanges
        }

    }
    private fun getPendingIntentOpenActivity(context: Context, intent: Intent?): PendingIntent? {
        if (intent == null) return null;
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );
    }
    public fun getIntentOpenActivity(context: Context, directLink:String?):Intent?{
        if(directLink==null)return null;
        try{
            var intent:Intent=Intent(context, HomeActivity::class.java);//default
            val splitPath = directLink.split("/");
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            when(splitPath[0]){
                "chanelchat"->{
                    return when(splitPath[1]){
                        "details"->{
                            intent= Intent(context, ChanelChatDetailsActivity::class.java)
                            intent.putExtra("chanelChatId",splitPath[2])
                            intent;
                        }

                        else->{
                            intent= Intent(context, MyChanelChatsActivity::class.java)
                            intent;
                        }
                    }
                }
                "friend"->{
                    if(splitPath[1]=="request"){
                        if(splitPath[2]=="details"){
                            intent= Intent(context, ViewFriendRequestActivity::class.java)
                            intent.putExtra("requestId",splitPath[3])
                            intent.putExtra("method", "receive")
                            return intent;
                        }
                    }
                }
                "account"->{
                    if(splitPath[1]=="details"){
                        intent= Intent(context, GuestUserProfileActivity::class.java)
                        intent.putExtra("idUser",splitPath[2])
                        return intent;
                    }
                }
                "team"->{
                    when(splitPath[1]){
                        "details"->{
                            intent= Intent(context, TeamProfileActivity::class.java)
                            intent.putExtra("teamId",splitPath[2])
                            return intent;
                        }
                        "members" ->{
                            intent= Intent(context, TeamProfileViewMembersListActivity::class.java)
                            intent.putExtra("teamId",splitPath[2])
                            return intent;
                        }
                    }
                }
                "project"->{
                    when(splitPath[1]){
                        "details"->{
                            intent= Intent(context, ProjectDetailsActivity::class.java)
                            intent.putExtra("projectId",splitPath[2])
                            return intent;
                        }
                        "members" ->{
                            intent= Intent(context, ProjectViewMembersNowActivity::class.java)
                            intent.putExtra("projectId",splitPath[2])
                            return intent;
                        }
                        "request_of_user"->{
                            intent = Intent(context , ProjectInvitingMembersActivity::class.java)
                            intent.putExtra("viewer", "user");
                            return intent;
                        }
                    }
                }
                "post"->{
                    if(splitPath[1]=="details"){
                        intent= Intent(context, PostDetailsActivity::class.java)
                        intent.putExtra("postId",splitPath[2])
                        return intent;
                    }
                }
            }

            return null;
        }catch (ex:Exception){
            return null;
        }
    }


}