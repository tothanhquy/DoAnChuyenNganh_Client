package com.example.tcapp.view.user_profile

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.api.API
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.user_profile.MyUserProfileCVViewModel


class MyUserProfileCVActivity : CoreActivity() {
	private lateinit var viewModelObject: MyUserProfileCVViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	
	private var  uriPDF:Uri? = null;
	private var  method:String? = null;
	private var idCV:String? = null;
	
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		//set method
		method = intent.getStringExtra("method").toString()
		
		viewModelObject = MyUserProfileCVViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.cv)
		setContentView(R.layout.activity_my_user_profile_cv)
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		if(method=="edit"){
			menuInflater.inflate(R.menu.edit_menu , menu)
		}else{
			menuInflater.inflate(R.menu.create_menu , menu)
		}
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.save -> {
				createOrEditCV()
			}
			R.id.create -> {
				createOrEditCV()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	
	override  fun onActivityResult(requestCode : Int , resultCode : Int , data : Intent?) {
		super.onActivityResult(requestCode , resultCode , data)
		if (requestCode == CHOOSE_AVATAR_REQUEST_CODE && resultCode == RESULT_OK) {
			uriPDF =	data!!.data //The uri with the location of the file
			findViewById<TextView>(R.id.myUserProfileCVActivityFileName).text = uriPDF!!.path.toString()
		}
	}
	
	private fun createOrEditCV(){
		val cv = UserProfileModels.MyCV(idCV,
			findViewById<EditText>(R.id.myUserProfileCVActivityName).text.toString(),
			findViewById<Switch>(R.id.myUserProfileCVActivityIsActive).isChecked
		)
//		if(uriPDF==null)return
		val selectedPath = if(uriPDF==null) null else Genaral.URIPathHelper().getPath(applicationContext, uriPDF!!)
		
		viewModelObject.createOrEditCV(method,cv,selectedPath)
	}
	
	fun deleteThisCV(view:View){
		viewModelObject.deleteCV(idCV)
	}
	private fun loadData() {
		if(method=="edit"){
			findViewById<EditText>(R.id.myUserProfileCVActivityName).setText(intent.getStringExtra("name").toString())
			findViewById<Switch>(R.id.myUserProfileCVActivityIsActive).isChecked = (intent.getBooleanExtra("isActive",false))
			findViewById<View>(R.id.myUserProfileCVActivityViewPDF).visibility= View.VISIBLE
			findViewById<View>(R.id.myUserProfileCVActivityDeleteCV).visibility= View.VISIBLE
			idCV = intent.getStringExtra("idCV").toString()
		}else{
			findViewById<EditText>(R.id.myUserProfileCVActivityName).setText("")
			findViewById<Switch>(R.id.myUserProfileCVActivityIsActive).isChecked = true
			findViewById<View>(R.id.myUserProfileCVActivityViewPDF).visibility= View.GONE
			findViewById<View>(R.id.myUserProfileCVActivityDeleteCV).visibility= View.GONE
		}
		viewModelObject.isActive = findViewById<Switch>(R.id.myUserProfileCVActivityIsActive).isChecked
	}
	fun getFile(view:View){
		if(checkPermissionsReadFile()){
			var intent: Intent =  Intent()
				.setType("application/pdf")
				.setAction(Intent.ACTION_GET_CONTENT);
			
			startActivityForResult(Intent.createChooser(intent, "Select PDF CV"), CHOOSE_AVATAR_REQUEST_CODE);
		}else{
			requestPermissionsReadFile()
		}
		
	}
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myUserProfileCVActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		viewModelObject.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		
		//set loading
		viewModelObject.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		viewModelObject.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,fun(dia: DialogInterface , i:Int){this@MyUserProfileCVActivity.finish()})
				}
			}
		})
		
		//set was delete
		viewModelObject.wasDelete.observe(this, Observer {
			runOnUiThread {
				if(it==true){
					this.finish()
				}
			}
		})
		
		//set was delete
		viewModelObject.viewPDFAction.observe(this, Observer {
			runOnUiThread {
				if(it==true){
					viewPDFCVAction()
				}
			}
		})
	}
	private fun viewPDFCVAction(){
//		val intent = Intent(applicationContext , UserProfileViewCVPDFActivity::class.java)
//		intent.putExtra("idCV", idCV)
//		intent.putExtra("token", viewModelObject.ownerToken)
//		startActivity(intent)
		
		val pdfUrl = API.getBaseUrl()+"/UserProfile/viewPDFCV?idcv="+idCV+"&token="+viewModelObject.ownerToken;
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pdfUrl))
		startActivity(intent)
	}
	fun viewPDFCV(view:View){
		if(! viewModelObject.isActive){
			viewModelObject.requestViewPDF(idCV)
		}else{
			viewPDFCVAction()
		}
	}
}