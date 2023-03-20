package com.example.clientserviceapp.data.cloud

import com.example.clientserviceapp.data.cache.CacheDataSource
import com.example.clientserviceapp.presentation.JokeUi
import com.google.gson.annotations.SerializedName

 data class JokeCloud(

    @SerializedName("setup")
    private val mainText: String,
    @SerializedName("punchline")
    private val punchline: String,
    @SerializedName("id")
    private val id: Int
) {
    fun toUi(): JokeUi = JokeUi.Base(mainText, punchline)
    fun toFavoriteUi(): JokeUi = JokeUi.Favorite(mainText, punchline)

    fun change(cacheDataSource: CacheDataSource): JokeUi =
        cacheDataSource.addOrRemove(id, this)

}