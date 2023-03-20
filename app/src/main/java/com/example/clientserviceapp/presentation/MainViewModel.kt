package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import com.example.clientserviceapp.data.JokeError

import com.example.clientserviceapp.data.Repository
import com.example.clientserviceapp.data.ResultCallback

class MainViewModel(private val repository: Repository<JokeUi, JokeError>) {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()

    private var resultCallback = object : ResultCallback<JokeUi, JokeError> {
        override fun provideSuccess(data: JokeUi) = data.show(jokeUiCallback)

        override fun provideError(error: JokeError) =
            JokeUi.Failed(error.message()).show(jokeUiCallback)
    }

    fun getJoke() {
        repository.fetch()
    }

    fun init(jokeUiCallback: JokeUiCallback) {
        this.jokeUiCallback = jokeUiCallback
        repository.init(resultCallback)
    }

    fun clear() {
        jokeUiCallback = JokeUiCallback.Empty()
        repository.clear()
    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorites(favorites)
    }

    fun changeJokeStatus() {
        repository.changeJokeStatus(resultCallback)
    }
}

interface JokeUiCallback {

    fun provideText(text: String)

    fun provideIconResId(@DrawableRes iconResId: Int)

    class Empty : JokeUiCallback {
        override fun provideText(text: String) = Unit
        override fun provideIconResId(iconResId: Int) = Unit

    }
}