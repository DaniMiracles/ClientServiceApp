package com.example.clientserviceapp.data

import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.JokeUiCallback


interface Repository<S, E> {

    fun fetch()
    fun clear()
    fun init(resultCallback: ResultCallback<S, E>)
    fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, JokeError>)
     fun chooseFavorites(favorites: Boolean)

}

interface ResultCallback<S, E> {

    fun provideSuccess(data: S)

    fun provideError(error: E)
}