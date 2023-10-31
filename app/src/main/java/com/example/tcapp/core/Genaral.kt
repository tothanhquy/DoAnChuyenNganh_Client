package com.example.tcapp.core

import android.app.Dialog
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.bumptech.glide.Glide
//import pl.droidsonroids.gif.GifImageView;
import com.example.tcapp.R
import com.example.tcapp.api.API
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import org.json.JSONArray
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KProperty1


class Genaral {
	
	companion object Static{
		
		fun createBackgroundShapeRectangle(bgColor:Int, borderRadius:Float, borderWidth:Int, borderColor:Int): GradientDrawable {
			val shape = GradientDrawable()
			shape.shape = GradientDrawable.RECTANGLE
			shape.cornerRadii = FloatArray(8){borderRadius}
			shape.setColor(bgColor)
			shape.setStroke(borderWidth , borderColor)
			return shape
		}
		fun convertDpToPx(context: Context , dp:Float):Float{
			return context.resources.displayMetrics.density*dp
		}
		fun convertObjectJsonArrayToArrayOfString(arr:JSONArray):ArrayList<String?>{
			val resArr:ArrayList<String?> = arrayListOf();
			for (i in 0 until arr.length()) {
				resArr.add(arr.getString(i));
			}
			return resArr;
		}
		fun HorizontalLayoutAutoWrapper(context : Context):FlexboxLayoutManager{
			return FlexboxLayoutManager(context).apply {
				flexWrap = FlexWrap.WRAP
				flexDirection = FlexDirection.ROW
			}
		}
		fun setUserAvatarImage(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/users_avatar/"+path)
					.error(R.drawable.default_user_avatar)
					.into(imageView)
		}
		fun setPostImage(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/posts/"+path)
					.error(R.drawable.placeholder_image_icon)
					.into(imageView)
		}
		fun setPostImageWithPlaceholder(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/posts/"+path)
					.fitCenter()
					.placeholder(R.drawable.placeholder_image_icon)
					.into(imageView)
		}
		fun setTeamAvatarImage(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/teams_avatar/"+path)
					.error(R.drawable.default_team_avatar)
					.into(imageView)
		}
		fun setGroupChatAvatarImage(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/group_chat_avatar/"+path)
					.error(R.drawable.default_team_avatar)
					.into(imageView)
		}
		fun setSkillImage(context : Context,path:String?,imageView : ImageView){
			if(path!=null&&path!="")
				Glide.with(context)
					.load(API.getBaseUrl()+"/static/images/skills/"+path)
					.error(R.drawable.default_skill_image)
					.into(imageView)
		}
		fun getDateTimeByUTC(utcTimeMiliSec:Long):String{
			val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			dateFormat.timeZone = TimeZone.getDefault()
			return dateFormat.format(Date(utcTimeMiliSec))
		}
		fun getMinimizeDateTimeByUTC(utcTimeMiliSec:Long):String{
			val now = getMillisecondNow()
			val yearFormat = SimpleDateFormat("yyyy")
			yearFormat.timeZone = TimeZone.getDefault()
			val dateFormat = SimpleDateFormat("yyyy-MM-dd")
			dateFormat.timeZone = TimeZone.getDefault()
			val monthDayFormat = SimpleDateFormat("MM-dd")
			monthDayFormat.timeZone = TimeZone.getDefault()
			val timeFormat = SimpleDateFormat("HH:mm a")
			timeFormat.timeZone = TimeZone.getDefault()

			if(yearFormat.format(Date(utcTimeMiliSec)).equals(yearFormat.format(Date(now)))){
				//out year
				dateFormat.format(Date(utcTimeMiliSec))
			}
			if(dateFormat.format(Date(utcTimeMiliSec)).equals(dateFormat.format(Date(now)))){
				//out date
				monthDayFormat.format(Date(utcTimeMiliSec))
			}
			//in date
			return timeFormat.format(Date(utcTimeMiliSec))
		}
		fun setupActivityTitleBar(activity: AppCompatActivity){
		
		}
		
		
		
		fun createBackgroundOpacity(color:Int, alpha:Float):Int{
			return Color.argb((Color.alpha(color)*alpha).toInt(),
				Color.red(color),
				Color.green(color),
				Color.blue(color))
		}
		
		fun getTimeStringNow():String{
			val sdf =	SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			return sdf.format(Date()).replace(' ','_')
		}
		
		
		fun getMillisecondNow():Long{
			return System.currentTimeMillis()
		}
		fun convertMillisecondToTimeString(millisecond:Long):String{
//			println(millisecond)
			var res = ""
			val zero:Long = 0
			var milli:Long = millisecond
			
			if(milli/(1000*60*60)!==zero){
				//hour
				res+=""+(milli/(1000*60*60))+":"
				milli%=(1000*60*60)
			}
			//minus
			val minus = (milli/(1000*60))
			res+=""+(if (minus<10) "0"+minus else minus)
			milli%=(1000*60)
			//sec
			val sec = (milli/(1000))
			res+=":"+(if (sec<10) "0"+sec else sec)
			milli%=1000
			//millisec
//			milli/=100
//			res+="."+milli
			return res
		}
		
		fun createGetStringDialog(
			context: Context , title:String ,
			defaultInput:String ,
			funcHandel:(input:String?)->Boolean ,//return true then dismiss
		): Dialog {
			var dialog = Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen)
			
			dialog.setContentView(R.layout.get_string_dialog)
			dialog.findViewById<TextView>(R.id.get_string_dialog_title).text = title
			dialog.findViewById<EditText>(R.id.get_string_dialog_input).setText(defaultInput)
			dialog.findViewById<Button>(R.id.get_string_dialog_bt_default).setOnClickListener {
				if(funcHandel(null))
					dialog.dismiss()
			}
			dialog.findViewById<Button>(R.id.get_string_dialog_bt_ok).setOnClickListener {
				if(funcHandel(dialog.findViewById<EditText>(R.id.get_string_dialog_input).text.toString()))
					dialog.dismiss()
			}
			return dialog
		}
		
		fun getLoadingScreen(context : Context , parent: ViewGroup , bg:Int): View {
			var screen: View = LayoutInflater.from(context).inflate(R.layout.loading_layout, parent, false);
//			screen.setBackgroundColor(createBackgroundOpacity(bg,0.5f))
			var img = screen.findViewById<ImageView>(R.id.loadingScreenImage)
//			val animation : Animation = AnimationUtils.loadAnimation(
//				context,
//				R.anim.rotate_animation_infinite
//			)
//			img.startAnimation(animation)
			Glide.with(context).asGif().load(R.drawable.loading_gif3).override(800, 800).into(img);
			return screen
		}
		fun getLoadingView(context : Context , parent: ViewGroup , bg:Int): View {
			var screen: View = LayoutInflater.from(context).inflate(R.layout.loading_view, parent, false);
//			screen.setBackgroundColor(createBackgroundOpacity(bg,0.5f))
			var img = screen.findViewById<ImageView>(R.id.loadingViewImage)
//			val animation : Animation = AnimationUtils.loadAnimation(
//				context,
//				R.anim.rotate_animation_infinite
//			)
//			img.startAnimation(animation)
			Glide.with(context).asGif().load(R.drawable.loading_gif3).override(400, 400).into(img);
			return screen
		}
		@Suppress("UNCHECKED_CAST")
		fun <R> readInstanceProperty(instance: Any, propertyName: String): R {
			val property = instance::class.members
				// don't cast here to <Any, R>, it would succeed silently
				.first { it.name == propertyName } as KProperty1<Any, *>
			// force a invalid cast exception if incorrect type here
			return property.get(instance) as R
		}
		
//		fun getFileFromUri(context : Context,uri: Uri?): File? {
//			if(uri==null)return null
//			val contentResolver = context.contentResolver
//			val inputStream = contentResolver.openInputStream(uri)
//			val fileName = getFileName(context,uri)
//			val file = File(context.cacheDir, fileName)
////			println(fileName)
//			inputStream?.use { input ->
//				file.outputStream().use { output ->
//					input.copyTo(output)
//				}
//			}
//
//			return file
//		}
//
//		fun getFileName(context : Context,uri: Uri): String {
//			var name = "unknown"
//			val cursor = context.contentResolver.query(uri, null, null, null, null)
//			cursor?.let {
//				if (it.moveToFirst()) {
//					val ind = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//					if(ind!=-1)name = cursor.getString(ind)
//				}
//				cursor.close()
//			}
//			return name
//		}
		
		
	}
	class URIPathHelper {
		//			https://handyopinion.com/get-path-from-uri-in-kotlin-android/
		fun getPath(context: Context, uri: Uri): String? {
			val isKitKatorAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
			
			// DocumentProvider
			if (isKitKatorAbove && DocumentsContract.isDocumentUri(context, uri)) {
				// ExternalStorageProvider
				if (isExternalStorageDocument(uri)) {
					val docId = DocumentsContract.getDocumentId(uri)
					println(docId.toString())
					val split = docId.split(":".toRegex()).toTypedArray()
					val type = split[0]
					if ("primary".equals(type, ignoreCase = true)) {
						return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
					}
					
				} else if (isDownloadsDocument(uri)) {
					val fileName = getFilePath(context , uri)
					if (fileName != null) {
						return Environment.getExternalStorageDirectory()
							.toString() + "/Download/" + fileName
					}
					var id = DocumentsContract.getDocumentId(uri)
					if (id.startsWith("raw:")) {
						id = id.replaceFirst("raw:" , "")
						val file = File(id)
						if (file.exists()) return id
					}
					println(uri.toString())
					val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
					return getDataColumn(context, contentUri, null, null)
////
//					val id = DocumentsContract.getDocumentId(uri)
//
//					if (id != null && id.startsWith("raw:")) {
//						return id.substring(4)
//					}
//
//					val contentUriPrefixesToTry = arrayOf(
//						"content://downloads/public_downloads" ,
//						"content://downloads/my_downloads" ,
//						"content://downloads/all_downloads"
//					)
//
//					for (contentUriPrefix in contentUriPrefixesToTry) {
//						val contentUri =
//							ContentUris.withAppendedId(
//								Uri.parse(contentUriPrefix) ,
//								java.lang.Long.valueOf(
//									id
//								)
//							)
//						try {
//							val path = getDataColumn(
//								context ,
//								contentUri ,
//								null ,
//								null
//							)
//							if (path != null) {
//								return path
//							}
//						} catch (e : Exception) {
//						}
//					}
//
//					// path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
//
//					// path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
//					val fileName : String = getFileName(context , uri)
//					val cacheDir : File = getDocumentCacheDir(context)
//					val file : File =
//						generateFileName(fileName , cacheDir)
//					var destinationPath : String? = null
//					if (file != null) {
//						destinationPath = file.absolutePath
//						saveFileFromUri(
//							context ,
//							uri ,
//							destinationPath
//						)
//					}
//
//					return destinationPath
//
				} else if (isMediaDocument(uri)) {
					val docId = DocumentsContract.getDocumentId(uri)
					val split = docId.split(":".toRegex()).toTypedArray()
					val type = split[0]
					var contentUri: Uri? = null
					if ("image" == type) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
					} else if ("video" == type) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
					} else if ("audio" == type) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
					}
					val selection = "_id=?"
					val selectionArgs = arrayOf(split[1])
					return getDataColumn(context, contentUri, selection, selectionArgs)
				}
			} else if ("content".equals(uri.scheme, ignoreCase = true)) {
				return getDataColumn(context, uri, null, null)
			} else if ("file".equals(uri.scheme, ignoreCase = true)) {
				return uri.path
			}
			return null
		}
		
		private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
			if(uri==null)return null
			var cursor: Cursor? = null
			val column = "_data"
			val projection = arrayOf(column)
			try {
				cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,null)
				if (cursor != null && cursor.moveToFirst()) {
					val column_index: Int = cursor.getColumnIndexOrThrow(column)
					return cursor.getString(column_index)
				}
			} finally {
				if (cursor != null) cursor.close()
			}
			return null
		}
		
		private fun isExternalStorageDocument(uri: Uri): Boolean {
			return "com.android.externalstorage.documents" == uri.authority
		}
		
		private fun isDownloadsDocument(uri: Uri): Boolean {
			return "com.android.providers.downloads.documents" == uri.authority
		}
		
		private fun isMediaDocument(uri: Uri): Boolean {
			return "com.android.providers.media.documents" == uri.authority
		}
		private fun getFilePath(context : Context , uri : Uri?) : String? {
			var cursor : Cursor? = null
			val projection = arrayOf(
				MediaStore.MediaColumns.DISPLAY_NAME
			)
			try {
				cursor = context.contentResolver.query(
					uri !! , projection , null , null ,
					null
				)
				if (cursor != null && cursor.moveToFirst()) {
					val index =
						cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
					return cursor.getString(index)
				}
			} finally {
				cursor?.close()
			}
			return null
		}
	}
	
	
	
}


