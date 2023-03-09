package com.example.clientserviceapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.clientserviceapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = (application as ClientServiceApp).viewModel

        binding.buttonMainAct.setOnClickListener {
            binding.buttonMainAct.isEnabled = false
            binding.progressBarMainAct.visibility = View.VISIBLE
            viewModel.getJoke()
        }


        viewModel.init(object : TextCallback{
          override fun provideText(text:String) = runOnUiThread(){
              binding.buttonMainAct.isEnabled = true
              binding.progressBarMainAct.visibility = View.INVISIBLE
              binding.tvMainAct.text = text
          }
        })


    }

    override fun onDestroy() {
        viewModel.clear()
        super.onDestroy()
    }
}