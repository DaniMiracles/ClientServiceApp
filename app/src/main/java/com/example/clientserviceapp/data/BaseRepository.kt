package com.example.clientserviceapp.data

import com.example.clientserviceapp.data.cache.CacheDataSource
import com.example.clientserviceapp.data.cache.JokeCallback
import com.example.clientserviceapp.data.cloud.CloudDataSource
import com.example.clientserviceapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val toUi: Joke.Mapper<JokeUi> = ToUi(),
    private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
    private val change: Joke.Mapper<JokeUi> = Change(cacheDataSource)
) : Repository<JokeUi, JokeError> {

    private var getJokeFromCache = false
    private var callback: ResultCallback<JokeUi, JokeError>? = null
    private var jokeTemporary: Joke? = null

    private val jokeCacheCallback = BaseJokeCallback(toFavoriteUi)
    private val jokeCloudCallback = BaseJokeCallback(toUi)

    override fun fetch() {
        if (getJokeFromCache) {
            cacheDataSource.fetch(jokeCacheCallback)
        } else
            cloudDataSource.fetch(jokeCloudCallback)

    }

    private inner class BaseJokeCallback(private val mapper: Joke.Mapper<JokeUi>) : JokeCallback {

        override fun provideJoke(joke: Joke) {
            jokeTemporary = joke
            callback?.provideSuccess(joke.map(mapper))
        }

        override fun provideError(error: JokeError) {
            jokeTemporary = null
            callback?.provideError(error)
        }

    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<JokeUi, JokeError>) {
        callback = resultCallback
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, JokeError>) {
        jokeTemporary?.let {
            resultCallback.provideSuccess(it.map(change))
        }
    }

    override fun chooseFavorites(favorites: Boolean) {
        getJokeFromCache = favorites
    }
}