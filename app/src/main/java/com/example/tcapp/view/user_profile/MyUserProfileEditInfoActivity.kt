package com.example.tcapp.view.user_profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.user_profile.UserProfileModels
import com.example.tcapp.viewmodel.user_profile.MyUserProfileEditInfoViewModel


class MyUserProfileEditInfoActivity : CoreActivity() {
	private lateinit var viewModelObject: MyUserProfileEditInfoViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = MyUserProfileEditInfoViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.info)
		setContentView(R.layout.activity_my_user_profile_edit_info)
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.edit_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}
	
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.save -> {
				changeInfo()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	private fun changeInfo(){
		val info = UserProfileModels.Info("",
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityName).text.toString(),
			Integer.parseInt("0"+findViewById<EditText>(R.id.myUserProfileEditInfoActivityBirthYear).text.toString()),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityMaxim).text.toString(),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityContact).text.toString(),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityCareerTarget).text.toString(),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityEducation).text.toString(),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityWorkExperience).text.toString(),
			findViewById<EditText>(R.id.myUserProfileEditInfoActivityDescription).text.toString(),
		)
//		println(info.name)
		viewModelObject.changeInfo(info)
	}
	private fun loadData() {
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityName).setText(intent.getStringExtra("name").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityBirthYear).setText(intent.getIntExtra("birth_year",0).toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityMaxim).setText(intent.getStringExtra("maxim").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityContact).setText(intent.getStringExtra("contact").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityCareerTarget).setText(intent.getStringExtra("career_target").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityEducation).setText(intent.getStringExtra("education").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityWorkExperience).setText(intent.getStringExtra("work_experience").toString())
		findViewById<EditText>(R.id.myUserProfileEditInfoActivityDescription).setText(intent.getStringExtra("description").toString())
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myUserProfileEditInfoActivity)
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
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		
	}
	
}