package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clientserviceapp.data.*
import kotlinx.coroutines.*


class MainViewModel(
    private val repository: Repository<JokeUi, JokeError>,
    private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val toUi: Joke.Mapper<JokeUi> = ToUi(),
    private val dispatcherList: DispatcherList = DispatcherList.Base(),
) : BaseViewModel(dispatcherList = dispatcherList) {

    private var jokeUiCallback: JokeUiCallback = JokeUiCallback.Empty()


    fun getJoke() = handleAny({
        val result = repository.fetch()
        if (result.isSuccessful())
            result.map(if (result.toFavorite()) toFavoriteUi else toUi)
        else
            JokeUi.Failed(result.errorMessage())
    }) {
        it.show(jokeUiCallback)
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

    fun changeJokeStatus() : Unit {
        handleAny({repository.changeJokeStatus()}){
            it.show(jokeUiCallback)
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

interface DispatcherList {

    fun io(): CoroutineDispatcher
    fun ui(): CoroutineDispatcher

    class Base : DispatcherList {
        override fun io(): CoroutineDispatcher = Dispatchers.IO
        override fun ui(): CoroutineDispatcher = Dispatchers.Main
    }
}



abstract class BaseViewModel(private val dispatcherList: DispatcherList) : ViewModel() {

    fun <T> handleAny(
        blockIo: suspend () -> T,
        blockUi: suspend (T) -> Unit
    ) = viewModelScope.launch(dispatcherList.io()) {
        val result = blockIo.invoke()
        withContext(dispatcherList.ui()) {
            blockUi.invoke(result)
        }
    }
}


