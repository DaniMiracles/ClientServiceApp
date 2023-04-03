package com.example.clientserviceapp.data

import com.example.clientserviceapp.data.cache.JokeResult
import com.example.clientserviceapp.presentation.JokeUi



interface Repository<S, E> {

    fun fetch() : JokeResult
    fun changeJokeStatus() : JokeUi
     fun chooseFavorites(favorites: Boolean)

}
