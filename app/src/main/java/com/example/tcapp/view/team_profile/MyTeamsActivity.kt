package com.example.tcapp.view.team_profile

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.model.team_profile.TeamProfileModels
import com.example.tcapp.view.adapter_view.MyTeamsRecyclerAdapter
import com.example.tcapp.viewmodel.team_profile.MyTeamsViewModel

class MyTeamsActivity : CoreActivity() {
	private lateinit var objectViewModel: MyTeamsViewModel;
	
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var myTeamsContainer:RecyclerView?= null;
	private var myTeamsContainerAdapter:MyTeamsRecyclerAdapter?= null;
	
	private var newTeamNameBefore:String? = null;
	
	private var reShowCreateNewTeam:Int =0;
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		objectViewModel = MyTeamsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.my_teams)
		setContentView(R.layout.activity_my_teams)
		
		myTeamsContainer =  findViewById<RecyclerView>(R.id.myTeamsActivityRecyclerView);
		
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	
	private fun loadData() {
		objectViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.myTeamsActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	@SuppressLint("SuspiciousIndentation")
	private fun setRender(){
		//set alert error
		objectViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,fun(dialogInterface: DialogInterface , i:Int){
						if(reShowCreateNewTeam!=objectViewModel.reShowCreateNewTeam){
							reShowCreateNewTeam=objectViewModel.reShowCreateNewTeam;
							showDialogCreateNewTeam(newTeamNameBefore?:"")
						}
					})
				}
			}
		})
		
		//set loading
		objectViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set alert notification
		objectViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,null)
				}
			}
		})
		//set avatar
		objectViewModel.newTeam.observe(this, Observer {
			if(it!=null)
			runOnUiThread {
				pushNewTeam(it)
			}
		})
		//set user data
		objectViewModel.myTeams.observe(this, Observer {
			runOnUiThread {
				setTeamsContainer(it)
			}
		})
		//set
//		objectViewModel.reShowCreateNewTeam.observe(this, Observer {
//			if(it!=0)
//				showDialogCreateNewTeam(newTeamNameBefore?:"")
//		})
	}
	private fun pushNewTeam(newTeam: TeamProfileModels.MyTeamItem){
		try{
			myTeamsContainerAdapter!!.add(newTeam)
		}catch(e:Exception){}
	}
	private fun setTeamsContainer(teams: ArrayList<TeamProfileModels.MyTeamItem>){
		myTeamsContainerAdapter = MyTeamsRecyclerAdapter(applicationContext,teams)
		myTeamsContainerAdapter!!.setCallback(::openTeamProfile);
		myTeamsContainer!!.setHasFixedSize(true)
		myTeamsContainer!!.layoutManager = LinearLayoutManager(this)
		myTeamsContainer!!.adapter = myTeamsContainerAdapter;
		
	}
	
	private fun openTeamProfile(idTeam:String){
		val intent = Intent(applicationContext ,TeamProfileActivity::class.java)
		intent.putExtra("teamId", idTeam)
		startActivity(intent)
	}
	fun createNewTeamClick(view:View){
		showDialogCreateNewTeam("new team")
	}
	private fun showDialogCreateNewTeam(inputDefault:String){
		val dialog = createGetStringDialog(this@MyTeamsActivity,"New team name",inputDefault,::createNewTeam)
		dialog.show();
	}
	private fun createNewTeam(name:String?):Boolean{
		newTeamNameBefore = name;
		objectViewModel.createNewTeam(name);
		return true;
	}
}