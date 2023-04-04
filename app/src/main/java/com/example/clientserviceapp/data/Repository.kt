package com.example.clientserviceapp.data

import com.example.clientserviceapp.data.cache.JokeResult
import com.example.clientserviceapp.presentation.JokeUi


interface Repository<S, E> {

    suspend fun fetch(): JokeResult
    suspend fun changeJokeStatus(): JokeUi
    fun chooseFavorites(favorites: Boolean)

}
