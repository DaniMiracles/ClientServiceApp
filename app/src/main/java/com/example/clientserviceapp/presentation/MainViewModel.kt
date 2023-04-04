package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientserviceapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val repository: Repository<JokeUi, JokeError>,
    private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toUi: Joke.Mapper<JokeUi> = ToUi(),
) : ViewModel() {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()


 fun getJoke() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.fetch()
            val ui = if (result.isSuccessful())
                result.map(if (result.toFavorite()) toFavoriteUi else toUi)
            else
                JokeUi.Failed(result.errorMessage())

            withContext(Dispatchers.Main) {
                ui.show(jokeUiCallback)
            }
        }
    }

    override fun onCleared() {
        jokeUiCallback = JokeUiCallback.Empty()
    }

    fun init(jokeUiCallback: JokeUiCallback) {
        this.jokeUiCallback = jokeUiCallback
    }

    fun chooseFavorite(favorites: Boolean) {
        repository.chooseFavorites(favorites)
    }

     fun changeJokeStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            val jokeUi = repository.changeJokeStatus()
            withContext(Dispatchers.Main) {
                jokeUi.show(jokeUiCallback)
            }
        }
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