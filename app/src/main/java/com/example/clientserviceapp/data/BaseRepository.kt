package com.example.clientserviceapp.data

import com.example.clientserviceapp.data.cache.CacheDataSource
import com.example.clientserviceapp.data.cache.JokeCacheCallback
import com.example.clientserviceapp.data.cloud.CloudDataSource
import com.example.clientserviceapp.data.cloud.JokeCloud
import com.example.clientserviceapp.data.cloud.JokeCloudCallback
import com.example.clientserviceapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
) : Repository<JokeUi, JokeError> {

    private var getJokeFromCache = false
    private var callback: ResultCallback<JokeUi, JokeError>? = null

    private var jokeCloudCached: JokeCloud? = null

    override fun fetch() {
        if (getJokeFromCache) {
            cacheDataSource.fetch(object : JokeCacheCallback {
                override fun provideJoke(joke: JokeCloud) {
                    jokeCloudCached = joke
                    callback?.provideSuccess(joke.toFavoriteUi())
                }

                override fun provideError(error: JokeError) {
                    callback?.provideError(error)
                }
            })

        } else
            cloudDataSource.fetch(object : JokeCloudCallback {
                override fun provideJokeCloud(jokeCloud: JokeCloud) {
                    jokeCloudCached = jokeCloud
                    callback?.provideSuccess(jokeCloud.toUi())
                }

                override fun provideError(error: JokeError) {
                    jokeCloudCached = null
                    callback?.provideError(error)
                }
            })

    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<JokeUi, JokeError>) {
        callback = resultCallback
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, JokeError>) {
        jokeCloudCached?.let {
            resultCallback.provideSuccess(it.change(cacheDataSource))
        }

    }


    override fun chooseFavorites(favorites: Boolean) {
        getJokeFromCache = favorites
    }
}