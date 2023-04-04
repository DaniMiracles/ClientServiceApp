package com.example.clientserviceapp.data.cloud


import retrofit2.Call
import retrofit2.http.GET


interface JokeService {

    @GET("random_joke")
  fun joke(): Call<JokeCloud>

}