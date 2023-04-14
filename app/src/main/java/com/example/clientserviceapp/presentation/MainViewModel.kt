package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientserviceapp.data.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel(
    private val repository: Repository<JokeUi, JokeError>,
    private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toUi: Joke.Mapper<JokeUi> = ToUi(),
    private val dispatcherIO : CoroutineDispatcher = DispatcherList.Base().io(),
    private val dispatcherUI : CoroutineDispatcher = DispatcherList.Base().ui()
) : ViewModel() {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()


 fun getJoke() {
        viewModelScope.launch(dispatcherIO) {
            val result = repository.fetch()
            val ui = if (result.isSuccessful())
                result.map(if (result.toFavorite()) toFavoriteUi else toUi)
            else
                JokeUi.Failed(result.errorMessage())
            withContext(dispatcherUI) {
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
        viewModelScope.launch(dispatcherIO) {
            val jokeUi = repository.changeJokeStatus()
            withContext(dispatcherUI) {
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

interface DispatcherList{

    fun io() : CoroutineDispatcher
    fun ui() : CoroutineDispatcher

    class Base : DispatcherList{
        override fun io(): CoroutineDispatcher = Dispatchers.IO
        override fun ui(): CoroutineDispatcher = Dispatchers.Main
    }
}