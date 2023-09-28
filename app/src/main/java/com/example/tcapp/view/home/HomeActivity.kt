package com.example.tcapp.view.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.tcapp.core.Genaral
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.LocalData
import com.example.tcapp.view.account.LoginAccountActivity
import com.example.tcapp.view.account.RegisterAccountActivity
import com.example.tcapp.view.account.SettingAccountActivity
import com.example.tcapp.view.post.PostsListActivity
import com.example.tcapp.view.post.UserPostsActivity
import com.example.tcapp.view.request.RequestsListActivity
import com.example.tcapp.view.team_profile.MyTeamsActivity
import com.example.tcapp.view.user_profile.MyUserProfileActivity
import com.example.tcapp.view.welcome.WelcomeActivity
import com.example.tcapp.viewmodel.home.HomeViewModel



const val  WAS_SHOWED_WELCOME_SCREEN_LOCAL_DATA_NAME :String= "was_showed_welcome_screen"
class HomeActivity : CoreActivity() {
	private lateinit var homeViewModel:HomeViewModel;
	private var backgroundColor:Int =0;
	private var  loadingLayout:View? = null;
	
	private var userName:String?=null;
	private var userAvatar:String?=null;
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		homeViewModel = HomeViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.login)
		super.hideTitle()
		setContentView(R.layout.activity_home)
		checkShowWelcomeActivity()
		initViews()
		setRender()
		loadData()
	}
	
	override fun onResume() {
		super.onResume()
		loadData()
	}
	private fun loadData() {
		homeViewModel.loadData()
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.homeActivity)
		loadingLayout = Genaral.getLoadingScreen(this,viewActivity,backgroundColor)
		viewActivity.addView(loadingLayout)
	}
	private fun setRender(){
		//set alert error
		homeViewModel.error.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showError(it.title,it.contents,it.listener)
				}
			}
		})
		//set user data
		homeViewModel.user.observe(this, Observer {
			runOnUiThread {
				findViewById<TextView>(R.id.homeScreAuthenUserName).text = "Xin chào, "+ it.name
				findViewById<Button>(R.id.homeScreAuthenVerifyEmail).visibility= if(!it.isVerifyEmail)View.VISIBLE else View.GONE
				userName = it.name;
				userAvatar = it.avatar;
			}
		})
		//set loading
		homeViewModel.isLoading.observe(this, Observer {
			runOnUiThread {
				if(it){
					loadingLayout?.visibility = View.VISIBLE
				}else{
					loadingLayout?.visibility = View.GONE
				}
			}
		})
		//set auth view
		homeViewModel.isAuthen.observe(this, Observer {
			runOnUiThread {
				if(it){
					findViewById<ViewGroup>(R.id.homeScreenAuthenticate)?.visibility = View.VISIBLE
					findViewById<ViewGroup>(R.id.homeScreenNotAuthenticate)?.visibility = View.GONE
				}else{
					findViewById<ViewGroup>(R.id.homeScreenAuthenticate)?.visibility = View.GONE
					findViewById<ViewGroup>(R.id.homeScreenNotAuthenticate)?.visibility = View.VISIBLE
				}
			}
		})
		//set alert notification
		homeViewModel.notification.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})
	}
	
	
	fun loginNavigation(view: View){
		val intent = Intent(applicationContext , LoginAccountActivity::class.java)
		startActivity(intent)
	}
	fun registerNavigation(view: View){
		val intent = Intent(applicationContext , RegisterAccountActivity::class.java)
		startActivity(intent)
	}
	
	
	private fun checkShowWelcomeActivity(){
		val localData = LocalData(applicationContext);
		var wasShowedWelcomeScreen = localData.getString(
			WAS_SHOWED_WELCOME_SCREEN_LOCAL_DATA_NAME
		);
//		wasShowedWelcomeScreen = null;
		if(wasShowedWelcomeScreen===null||wasShowedWelcomeScreen==="false") {
			wasShowedWelcomeScreen= "true"
			localData.setString(WAS_SHOWED_WELCOME_SCREEN_LOCAL_DATA_NAME ,wasShowedWelcomeScreen);
			goToWelcomeActivity()
		}
	}
	private fun goToWelcomeActivity(){
		val intent = Intent(applicationContext ,WelcomeActivity::class.java)
		startActivity(intent)
	}
	
	fun myUserProfileNavigation(view: View){
		val intent = Intent(applicationContext , MyUserProfileActivity::class.java)
		startActivity(intent)
	}
	fun myTeamsNavigation(view: View){
		val intent = Intent(applicationContext , MyTeamsActivity::class.java)
		startActivity(intent)
	}
	fun viewRequestsNavigation(view: View){
		val intent = Intent(applicationContext , RequestsListActivity::class.java)
		intent.putExtra("viewer", "user");
		startActivity(intent)
	}
	fun viewPostsNavigation(view: View){
		val intent = Intent(applicationContext , PostsListActivity::class.java)
		intent.putExtra("filter", "guest");
		startActivity(intent)
	}
	fun viewMyPostsNavigation(view: View){
		val intent = Intent(applicationContext , UserPostsActivity::class.java)
		intent.putExtra("user_name", userName?:"");
		intent.putExtra("user_avatar", userAvatar?:"");
		startActivity(intent)
	}
	fun viewSettingAccountNavigation(view: View){
		val intent = Intent(applicationContext , SettingAccountActivity::class.java)
		startActivity(intent)
	}
	fun requestVerifyEmail(view: View){
		homeViewModel.requestVerifyEmail(::callbackOkRequestVerifyEmail)
	}
	private fun callbackOkRequestVerifyEmail(){
		findViewById<Button>(R.id.homeScreAuthenVerifyEmail).text = "Resend to verify email"
	}
}