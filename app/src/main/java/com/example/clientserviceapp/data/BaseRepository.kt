package com.example.clientserviceapp.data

import com.example.clientserviceapp.data.cache.CacheDataSource
import com.example.clientserviceapp.data.cache.JokeResult
import com.example.clientserviceapp.data.cloud.CloudDataSource
import com.example.clientserviceapp.presentation.JokeUi

class BaseRepository(
    private val cloudDataSource: CloudDataSource,
    private val cacheDataSource: CacheDataSource,
    private val change: Joke.Mapper<JokeUi> = Change(cacheDataSource)
) : Repository<JokeUi, JokeError> {

    private var getJokeFromCache = false
    private var jokeTemporary: Joke? = null



    override fun fetch(): JokeResult {
        val jokeResult = if (getJokeFromCache)
            cacheDataSource.fetch()
         else
            cloudDataSource.fetch()
        jokeTemporary = if (jokeResult.isSuccessful()) {
            jokeResult.map(ToDomain())
        }else{
            null
        }
        return jokeResult
    }




    override fun changeJokeStatus(): JokeUi {
      return  jokeTemporary!!.map(change)
    }

    override fun chooseFavorites(favorites: Boolean) {
        getJokeFromCache = favorites
    }
}