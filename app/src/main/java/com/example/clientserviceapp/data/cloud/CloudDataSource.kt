package com.example.clientserviceapp.data.cloud

import android.util.Log

import com.example.clientserviceapp.data.JokeError
import com.example.clientserviceapp.data.cache.DataSource
import com.example.clientserviceapp.data.cache.JokeCallback
import com.example.clientserviceapp.presentation.ManageResources
import retrofit2.Call
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownServiceException

interface CloudDataSource : DataSource {



    class Base(
        private val jokeService: JokeService, private val manageResources: ManageResources
    ) : CloudDataSource {


        private val noConnection by lazy { JokeError.NoConnection(manageResources) }
        private val serviceError by lazy { JokeError.ServiceUnavailable(manageResources) }


        override fun fetch(jokeCallback: JokeCallback) {
            Log.d("Retrofit", "fetch() started")

            jokeService.joke().enqueue(object : retrofit2.Callback<JokeCloud> {
                override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null){

                            jokeCallback.provideError(serviceError)
                        } else {

                            jokeCallback.provideJoke(body)
                        }
                    } else {
                        jokeCallback.provideError(serviceError)
                    }
                }

                override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                    Log.e("Retrofit", "Request failed: ${t.message}")
                    jokeCallback.provideError(
                        if (t is UnknownServiceException || t is ConnectException) noConnection
                        else {
                            Log.d("Miracles", "Mistake cloudDataSource 3rd else")
                            serviceError
                        }
                    )
                }
            })
        }
    }

}
