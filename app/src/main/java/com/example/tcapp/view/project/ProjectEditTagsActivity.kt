package com.example.tcapp.view.project

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.core.CoreActivity
import com.example.tcapp.core.Genaral
import com.example.tcapp.view.adapter_view.ProjectEditTagsRecyclerAdapter
import com.example.tcapp.viewmodel.project.ProjectEditTagsViewModel


class ProjectEditTagsActivity : CoreActivity() {
	private lateinit var viewModelObject: ProjectEditTagsViewModel;
	private var backgroundColor:Int =0
	private var  loadingLayout:View? = null;
	
	private var projectId:String?=null;
	private var focusTagPosition:Int=-1;

	private var tagsRecyclerView:RecyclerView?=null;
	private var editTagsRecyclerAdapter: ProjectEditTagsRecyclerAdapter?=null;
	private var input:EditText?=null;
	private var addButton: Button?=null;

	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		viewModelObject = ProjectEditTagsViewModel(applicationContext)
		backgroundColor = getColor(R.color.light_blue_900)
		super.setTitleBarAndNavigationBar(backgroundColor,R.string.edit_tags)
		setContentView(R.layout.activity_project_edit_tags)

		tagsRecyclerView=findViewById(R.id.projectEditTagsActivityRecyclerView)
		input=findViewById(R.id.projectEditTagsActivityInput)
		addButton=findViewById(R.id.projectEditTagsActivityAddButton)

		editTagsRecyclerAdapter = ProjectEditTagsRecyclerAdapter(this, arrayListOf())
		editTagsRecyclerAdapter!!.setCallbackOfOpenOptions(::openOptions)
		tagsRecyclerView!!.setHasFixedSize(true)
		tagsRecyclerView!!.layoutManager =
			Genaral.Static.HorizontalLayoutAutoWrapper(this)
		tagsRecyclerView!!.adapter = editTagsRecyclerAdapter

		setEvents()
		initViews()
		setRender()
		loadData()
	}
	private fun setEvents(){
		input?.addTextChangedListener(object : TextWatcher {
			override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
			override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
				if (charSequence.isEmpty()) {
					input?.error = "không được để trống"
				} else if (!isValidTag(charSequence.toString())) {
					input?.error = "không chứa khoảng trắng"
				} else {
					input?.error = null
				}
			}

			override fun afterTextChanged(editable: Editable) {}
		})
	}
	
	override fun onCreateOptionsMenu(menu : Menu?) : Boolean {
		menuInflater.inflate(R.menu.edit_menu , menu)
		return super.onCreateOptionsMenu(menu)
	}
	override fun onOptionsItemSelected(item : MenuItem) : Boolean {
		when (item.getItemId()) {
			R.id.save -> {
				changeTags()
			}
		}
		return super.onOptionsItemSelected(item)
	}
	private fun changeTags(){
		val editTags = editTagsRecyclerAdapter!!.getList()
		viewModelObject.changeTags(
			projectId,editTags,::changeTagsOkCallback
		)
	}
	private fun changeTagsOkCallback(newTags:ArrayList<String?>?){
		runOnUiThread {
			setTagsContainer(newTags)
		}
	}
	private fun setTagsContainer(tags:ArrayList<String?>?){
		editTagsRecyclerAdapter?.setInit(tags)
		updateButton()
	}
	private fun loadData() {
		projectId = intent.getStringExtra("projectId").toString();
		viewModelObject.loadData(projectId)
	}
	
	private fun initViews(){
		var viewActivity = findViewById<ViewGroup>(R.id.projectEditTagsActivity)
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
					super.showNotificationDialog(it.title,it.contents,it.listener)
				}
			}
		})

		viewModelObject.tags.observe(this, Observer {
			runOnUiThread {
				if(it!=null){
					setTagsContainer(it);
				}
			}
		})
	}
	private fun openOptions(position:Int){
		focusTagPosition = position;
		findViewById<LinearLayout>(R.id.projectEditTagsActivityOptions).visibility = View.VISIBLE;
	}
	fun closeOptions(view:View){
		findViewById<LinearLayout>(R.id.projectEditTagsActivityOptions).visibility = View.GONE;
	}

	fun deleteTag(view:View){
		editTagsRecyclerAdapter?.deleteAt(focusTagPosition)
		closeOptions(View(applicationContext))
		updateButton()
	}
	private fun isValidTag(tag:String):Boolean{
		return tag.indexOf(' ')!=-1
	}
	fun addTag(view:View){
		val count = editTagsRecyclerAdapter!!.itemCount
		val maxCount = editTagsRecyclerAdapter!!.MAX_COUNT
		if(count>=maxCount){
			super.showNotificationDialog("Lưu ý","Chỉ tối đa $maxCount thẻ.",null)
		}else{
			val tagContent = input!!.text.toString()
			if(tagContent.isEmpty())return;
			input!!.setText("")
			editTagsRecyclerAdapter?.add(tagContent)
			updateButton()
		}
	}
	private fun updateButton(){
		val count = editTagsRecyclerAdapter!!.itemCount
		val maxCount = editTagsRecyclerAdapter!!.MAX_COUNT
		addButton?.text="Thêm Thẻ\n($count/$maxCount)"
	}

}