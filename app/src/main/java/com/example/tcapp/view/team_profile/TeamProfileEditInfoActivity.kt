package com.example.tcapp.view.team_profile

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.viewmodel.team_profile.TeamProfileEditInfoViewModel


class TeamProfileEditInfoActivity : CoreActivity() {
	private lateinit var viewModelObject: TeamProfileEditInfoViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var teamId:String?=null;
	
	private var teamName:EditText? =null;
	private var teamMaxim:EditText? =null;
	private var teamDescription:EditText? =null;
	private var teamInternalInfo:EditText? =null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = TeamProfileEditInfoViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.info)
		setContentView(R.layout.activity_team_profile_edit_info)
		
		teamName = findViewById<EditText>(R.id.teamProfileEditInfoActivityName)
		teamInternalInfo=findViewById<EditText>(R.id.teamProfileEditInfoActivityInternalInfo)
		teamMaxim = findViewById<EditText>(R.id.teamProfileEditInfoActivityMaxim)
		teamDescription=findViewById<EditText>(R.id.teamProfileEditInfoActivityDescription)
		
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
		viewModelObject.changeInfo(
			teamId,
			teamName!!.text.toString(),
			teamMaxim!!.text.toString(),
			teamDescription!!.text.toString(),
			teamInternalInfo!!.text.toString(),
		)
	}
	private fun loadData() {
		teamId = intent.getStringExtra("teamId").toString();
		teamName!!.setText(
			(intent.getStringExtra("name")?:"").toString())
		teamInternalInfo!!.setText(
			(intent.getStringExtra("internalInfo")?:"").toString())
		teamMaxim!!.setText(
			(intent.getStringExtra("maxim")?:"").toString())
		teamDescription!!.setText(
			(intent.getStringExtra("description")?:"").toString())
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.teamProfileEditInfoActivity)
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