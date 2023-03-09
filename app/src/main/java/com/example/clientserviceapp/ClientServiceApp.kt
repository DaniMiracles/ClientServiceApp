package com.example.clientserviceapp

import android.app.Application
import com.google.gson.Gson

class ClientServiceApp : Application() {

    lateinit var viewModel: MainViewModel
    override fun onCreate() {
        super.onCreate()

        viewModel = MainViewModel(BaseModel(JokeService.Base(Gson()), ManageResources.Base(this)))
    }
}