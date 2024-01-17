package com.example.uberclone

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uberclone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        replaceFragment()
//        Places.initialize(applicationContext, "AIzaSyDiUmJIvtlQ4vwT4MTySOW5e98-XxQCbUA")
//        binding.searchView.isIconifiedByDefault = false
//        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//
//            }
//        })
    }


    private fun replaceFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view_tag,MapsFragment()).commit()
    }
}