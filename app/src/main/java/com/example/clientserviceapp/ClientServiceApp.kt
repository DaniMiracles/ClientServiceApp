package com.example.clientserviceapp

import android.app.Application
import android.util.Log
import com.example.clientserviceapp.data.BaseRepository
import com.example.clientserviceapp.data.cache.CacheDataSource
import com.example.clientserviceapp.data.cloud.CloudDataSource
import com.example.clientserviceapp.data.cloud.JokeService
import com.example.clientserviceapp.presentation.MainViewModel
import com.example.clientserviceapp.presentation.ManageResources
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ClientServiceApp : Application() {

    lateinit var viewModel: MainViewModel
    override fun onCreate() {
        super.onCreate()


        val okHttpClient = OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor { message ->
            Log.d("Retrofit", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://official-joke-api.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val manageResources = ManageResources.Base(this)

        viewModel = MainViewModel(
            BaseRepository(
                CloudDataSource.Base(retrofit.create(JokeService::class.java), manageResources),
                CacheDataSource.Fake(manageResources)
            )

        )
    }
}


