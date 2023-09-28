package com.example.tcapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tcapp.R
import com.example.tcapp.view.adapter_view.testAdapter

class TestActivity : AppCompatActivity() {
	
	override fun onCreate(savedInstanceState : Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_test)
		val container = findViewById<RecyclerView>(R.id.testActivityRecyclerView)
		container !!.setHasFixedSize(true)
		container !!.layoutManager = LinearLayoutManager(this)
		
		val customAdapter = testAdapter(listOf("1","2","3"))
		container!!.adapter = customAdapter
	}
}