package com.example.clientserviceapp

import com.google.gson.annotations.SerializedName

class JokeCloud(
    @SerializedName("type")
    private val type: String,
    @SerializedName("setup")
    private val mainText: String,
    @SerializedName("punchline")
    private val punchline: String,
    @SerializedName("id")
    private val id: Int
) {
    fun toJoke(): Joke {
          return Joke(mainText,punchline)
    }
}