package com.example.tcapp.core

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tcapp.R
import com.example.tcapp.model.SettingTheme
import com.google.gson.Gson


const val  SETTING_THEME_NAME = "setting_theme";
const val REQUEST_READ_FILE_PERMISSION_CODE = 1
 open class CoreActivity : AppCompatActivity(){
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		
//		hideTitle()
		
		val localData = LocalData(applicationContext);
		var themeJson = localData.getString(SETTING_THEME_NAME);
		var theme: SettingTheme? = Gson().fromJson(themeJson,SettingTheme::class.java);
		
		if(theme===null) {
			theme=SettingTheme();
			//save if not exist
			themeJson = Gson().toJson(theme)
			localData.setString(SETTING_THEME_NAME,themeJson);
		}
		
		if(theme.theme=="light"){
			setTheme(R.style.AppThemeFullscreenDay)
		}else {
			setTheme(R.style.AppThemeFullscreenNight)
		}
		setFullScreen()
//		window.setSoftInputMode(
//			WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//		);
//		this.currentFocus?.let { view ->
//			val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
//			imm?.hideSoftInputFromWindow(view.windowToken, 0)
//		}
		setEvent()
	}
	 override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		 return super.onCreateOptionsMenu(menu)
	 }
	 override fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		 super.onActivityResult(requestCode , resultCode , data)
	 }
	private fun setFullScreen(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(
			WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	 private fun setEvent(){
		
	 }
	
	protected fun hideTitle(){
		supportActionBar?.hide()
	}
	
	protected fun setTitleBarAndNavigationBar( backgroundScreenColor:Int , titleString:Int){
//			set title bar
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setTitle(titleString)
		val backgroundScreenColor = getColor(R.color.background_bar_screen);
//		          set background color title
		val backgroundTitleColor:Int = Color.argb((Color.alpha(backgroundScreenColor)*0.7).toInt(),
			Color.red(backgroundScreenColor),
			Color.green(backgroundScreenColor),
			Color.blue(backgroundScreenColor))
		supportActionBar?.setBackgroundDrawable(ColorDrawable(backgroundScreenColor))
//		          set window navigation bar color
		window.navigationBarColor = backgroundScreenColor
	}
	 protected fun setDynamicTitleBar( titleString:String?){
//		set title bar
	 		supportActionBar?.title=titleString
	 }
	 override fun  onOptionsItemSelected(item: MenuItem):Boolean {
		val id:Int = item.getItemId();

		if ( id == android.R.id.home ) {
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	fun showError(title:String , content:String , listener : DialogInterface.OnClickListener?=null){
		var builder: AlertDialog.Builder = AlertDialog.Builder(this)
		builder
			.setTitle(title)
			.setMessage(content)
			.setIcon(R.drawable.warning_icon)
			.setPositiveButton(android.R.string.ok,listener)
		var dialog: AlertDialog = builder.create()
		dialog.setCanceledOnTouchOutside(false)
		dialog.show()
		
	}
	fun showNotificationDialog(title:String , content:String , listener : DialogInterface.OnClickListener?=null){
		var builder: AlertDialog.Builder = AlertDialog.Builder(this)
		builder
			.setTitle(title)
			.setMessage(content)
			.setIcon(R.drawable.notification_icon)
			.setPositiveButton(android.R.string.ok,listener)
		var dialog: AlertDialog = builder.create()
		dialog.setCanceledOnTouchOutside(false)
		dialog.show()
	}
	fun showAskDialog( title:String , content:String , listener: DialogInterface.OnClickListener){
		var builder: AlertDialog.Builder = AlertDialog.Builder(this)
		builder
			.setTitle(title)
			.setMessage(content)
			.setIcon(R.drawable.question_icon)
			.setPositiveButton(android.R.string.yes,listener)
			.setOnCancelListener(null)
			.setNegativeButton(android.R.string.no, null)
		var dialog: AlertDialog = builder.create()
		dialog.setCanceledOnTouchOutside(false)
		dialog.show()
	}
	
	 private fun showMessageOKCancel(
		message : String ,
		okListener : DialogInterface.OnClickListener
	) {
		AlertDialog.Builder(this)
			.setMessage(message)
			.setPositiveButton("OK" , okListener)
			.setNegativeButton("Cancel" , null)
			.create()
			.show()
	}
	
	 fun createGetStringDialog(
		 context:Context,title:String,
		 defaultInput:String,
		 funcHandel:(input:String?)->Boolean,//return true then dismiss
	 ): Dialog {
		 var dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
		
		 dialog.setContentView(R.layout.get_string_dialog)
		 dialog.findViewById<TextView>(R.id.get_string_dialog_title).text = title
		 dialog.findViewById<EditText>(R.id.get_string_dialog_input).setText(defaultInput)
			dialog.findViewById<Button>(R.id.get_string_dialog_bt_default).setOnClickListener {
				if(funcHandel(defaultInput))
					dialog.dismiss()
			}
		 dialog.findViewById<Button>(R.id.get_string_dialog_bt_ok).setOnClickListener {
			 if(funcHandel(dialog.findViewById<EditText>(R.id.get_string_dialog_input).text.toString()))
				 dialog.dismiss()
		 }
		 dialog.findViewById<Button>(R.id.get_string_dialog_bt_cancel).setOnClickListener {
			 dialog.dismiss()
		 }
		 return dialog
	 }
	 
	 protected fun checkPermissionsReadFile() : Boolean {
		val result = ContextCompat.checkSelfPermission(
			applicationContext ,
			READ_EXTERNAL_STORAGE
		)
		return result == PackageManager.PERMISSION_GRANTED
	}
	
	protected fun requestPermissionsReadFile() {
		showMessageOKCancel(
			"We need to allow access to Read Storage"
		) { dialog:DialogInterface  , which:Int->
			ActivityCompat.requestPermissions(
				this,
				arrayOf<String>(
					READ_EXTERNAL_STORAGE
				) ,
				REQUEST_READ_FILE_PERMISSION_CODE
			)
		}
	}
//	 protected fun checkPermissionsWriteFile() : Boolean {
//		 val result = ContextCompat.checkSelfPermission(
//			 applicationContext ,
//			 WRITE_EXTERNAL_STORAGE
//		 )
//		 return result == PackageManager.PERMISSION_GRANTED
//	 }
//
//	 protected fun requestPermissionsWriteFile() {
//		 showMessageOKCancel(
//			 "We need to allow access to Read Storage"
//		 ) { dialog:DialogInterface  , which:Int->
//			 ActivityCompat.requestPermissions(
//				 this,
//				 arrayOf<String>(
//					 WRITE_EXTERNAL_STORAGE,
//					 READ_EXTERNAL_STORAGE
//				 ) ,
//				 REQUEST_READ_FILE_PERMISSION_CODE
//			 )
//		 }
//	 }
	override fun dispatchTouchEvent(event : MotionEvent) : Boolean {
		if (event.action == MotionEvent.ACTION_DOWN) {
			val v : View? = currentFocus
			if (v is EditText) {
				val outRect = Rect()
				v.getGlobalVisibleRect(outRect)
				if (! outRect.contains(event.rawX.toInt() , event.rawY.toInt())) {
					v.clearFocus()
					val imm : InputMethodManager =
						getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
					imm.hideSoftInputFromWindow(v.getWindowToken() , 0)
				}
			}
		}
		return super.dispatchTouchEvent(event)
	}
 }