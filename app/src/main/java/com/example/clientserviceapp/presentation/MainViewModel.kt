package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import com.example.clientserviceapp.data.*


class MainViewModel(
    private val repository: Repository<JokeUi, JokeError>,
    private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toUi: Joke.Mapper<JokeUi> = ToUi(),
) {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()


    fun getJoke() {
        Thread{
            val result = repository.fetch()
            if (result.isSuccessful())
                result.map(if(result.toFavorite()) toFavoriteUi else toUi).show(jokeUiCallback)
            else
                JokeUi.Failed(result.errorMessage()).show(jokeUiCallback)
        }.start()

    }

    fun init(jokeUiCallback: JokeUiCallback) {
        this.jokeUiCallback = jokeUiCallback

    }

    fun clear() {
        jokeUiCallback = JokeUiCallback.Empty()
    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorites(favorites)
    }

    fun changeJokeStatus() {
        Thread {
            val jokeUi = repository.changeJokeStatus()
            jokeUi.show(jokeUiCallback)
        }.start()
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