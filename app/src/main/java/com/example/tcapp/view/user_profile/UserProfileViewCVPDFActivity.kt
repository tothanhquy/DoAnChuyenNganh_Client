package com.example.tcapp.view.user_profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.tcapp.R
import com.example.tcapp.api.API
import com.example.tcapp.core.CoreActivity

//drop
class UserProfileViewCVPDFActivity : CoreActivity() {
	private var backgroundColor:Int =0;
	private var myWebView : WebView? = null;
//	lateinit var myViewer : PDFView;
	@SuppressLint("MissingInflatedId")
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_user_profile_view_cvpdfactivity)
		val idCV = intent.getStringExtra("idCV").toString()
		val token = intent.getStringExtra("token").toString()
		
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.cv)
//		setContentView(R.layout.activity_my_user_profile_cv)
		// Create a new WebView instance
		myWebView = WebView(this)
//		val myWebView:WebView = findViewById(R.id.activityUserProfileViewCVWebview);
//		myViewer = findViewById(R.id.activityUserProfileViewerPDF);
		
		
		// Configure other WebView settings as needed
		
		val webSettings = myWebView!!.settings
		webSettings.javaScriptEnabled = true
		webSettings.domStorageEnabled = true
		myWebView!!.webViewClient = WebViewClient()
		
		// Load the desired URL into the WebView
		val pdfUrl = API.getBaseUrl()+"/UserProfile/ViewPDFCV?idcv=$idCV&token=$token";
		val fullUrl = "http://drive.google.com/viewerng/viewer?embedded=true&url=$pdfUrl";
//		myWebView!!.loadUrl(pdfUrl)
		myWebView!!.loadUrl(fullUrl)
//		println(fullUrl)
		// Set the WebView as the main content view of the activity
		setContentView(myWebView)
//		myViewer.fromUri(Uri.parse(pdfUrl)).load()
//		RetrivePDFfromUrl().execute(pdfUrl);
	}
	override fun onDestroy() {
		// Clean up the WebView when the activity is destroyed
		if (myWebView != null) {
			myWebView !!.destroy()
			myWebView = null
		}
		super.onDestroy()
	}
	
}