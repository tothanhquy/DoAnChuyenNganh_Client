package com.example.tcapp.model

import android.content.DialogInterface

class AlertDialog {
	class Error(var title:String, var contents:String, var listener: DialogInterface.OnClickListener?=null)
	class Notification(var title:String, var contents:String, var listener: DialogInterface.OnClickListener?=null)
	class Ask(var title:String, var contents:String, var listener: DialogInterface.OnClickListener)
}