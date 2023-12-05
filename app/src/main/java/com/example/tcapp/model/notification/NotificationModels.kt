package com.example.tcapp.model.notification

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan


class NotificationModels {
    companion object{
        fun getContentWithStyle(content:String?,styles:ArrayList<StyleRange>?):SpannableStringBuilder{
            val notNullContent = content ?: "";
            val resContent = SpannableStringBuilder(notNullContent);
            if(styles==null||styles.size==0)return resContent;

            styles.forEach {
                val style = when(it.style) {
                    StyleOfRange.Bold->
                        StyleSpan(Typeface.BOLD)
                    StyleOfRange.Italic->
                        StyleSpan(Typeface.ITALIC)
                    else->
                        StyleSpan(Typeface.BOLD_ITALIC)
                }
                resContent.setSpan(
                    style,
                    it.position,
                    it.position+it.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            return resContent;
        }
    }
    enum class StyleOfRange {
        Bold,Italic,Underline
    }
    public fun convertStringToStyleOfRange(str:String?):StyleOfRange{
        return when(str){
            "italic"-> StyleOfRange.Italic
            "underline"-> StyleOfRange.Underline
            else-> StyleOfRange.Bold
        }
    }
    inner class StyleRange{
        public lateinit var style:StyleOfRange;
        public var position:Int=0;
        public var length:Int=0;
        constructor(style:StyleOfRange,position:Int,length:Int){
            this.style=style;
            this.position=position;
            this.length=length;
        }
        constructor(crude:StyleRangeCrude){
            this.style=convertStringToStyleOfRange(crude.style);
            this.position=crude.position;
            this.length=crude.length;
        }
    }
    class StyleRangeCrude{
        public var style:String?=null;
        public var position:Int=0;
        public var length:Int=0;
        constructor(style:String?,position:Int,length:Int){
            this.style=style;
            this.position=position;
            this.length=length;
        }
    }
    inner class Notification{
        public var id:String?=null;
        public var content:String?=null;
        public var time:Long=0L;
        public var wasRead:Boolean=false;
        public var directLink:String?=null;
        public var styleRanges: ArrayList<StyleRange>?=null;
        constructor(
            id:String?=null,
            content:String?=null,
            time:Long=0L,
            wasRead:Boolean=false,
            directLink:String?=null,
            styleRanges: ArrayList<StyleRange>?=null
        ){
            this.id=id;
            this.content=content;
            this.time=time;
            this.wasRead=wasRead;
            this.directLink=directLink;
            this.styleRanges=styleRanges;
        }
        constructor(
            crude:NotificationCrude
        ){
            this.id=crude.id;
            this.content=crude.content;
            this.time=crude.time;
            this.wasRead=crude.wasRead;
            this.directLink=crude.directLink;
            this.styleRanges= if(crude.styleRanges==null)null else ArrayList<StyleRange>(crude.styleRanges?.map { e-> StyleRange(e) });
        }
    }
    class NotificationCrude{
        public var id:String?=null;
        public var content:String?=null;
        public var time:Long=0L;
        public var wasRead:Boolean=false;
        public var directLink:String?=null;
        public var styleRanges: ArrayList<StyleRangeCrude>?=null;
        constructor(
            id:String?=null,
            content:String?=null,
            time:Long=0L,
            wasRead:Boolean=false,
            directLink:String?=null,
            styleRanges: ArrayList<StyleRangeCrude>?=null
        ){
            this.id=id;
            this.content=content;
            this.time=time;
            this.wasRead=wasRead;
            this.directLink=directLink;
            this.styleRanges=styleRanges;
        }
    }
    inner class Notifications{
        public var notifications:ArrayList<Notification>?=null;
        public var isFinish:Boolean=false;
        constructor(){}
        constructor(notifications: ArrayList<Notification>?, isFinish: Boolean) {
            this.notifications = notifications
            this.isFinish = isFinish
        }
        constructor(crude:NotificationsCrude){
            this.isFinish = crude.isFinish;
            this.notifications = if(crude.notifications==null) null else ArrayList(crude.notifications!!.map{ e-> Notification(e)});
        }
    }
    class NotificationsCrude{
        public var notifications:ArrayList<NotificationCrude>?=null;
        public var isFinish:Boolean=false;
        constructor(){}
        constructor(notifications: ArrayList<NotificationCrude>?, isFinish: Boolean) {
            this.notifications = notifications
            this.isFinish = isFinish
        }
    }
    constructor(){}
}