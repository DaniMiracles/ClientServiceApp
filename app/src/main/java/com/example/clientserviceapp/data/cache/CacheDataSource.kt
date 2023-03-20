package com.example.clientserviceapp.data.cache

import com.example.clientserviceapp.data.JokeError
import com.example.clientserviceapp.data.cloud.JokeCloud
import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.ManageResources

interface CacheDataSource {

    fun addOrRemove(id: Int, joke: JokeCloud): JokeUi
    fun fetch(jokeCacheCallback: JokeCacheCallback)


    class Fake(private val manageResources: ManageResources) : CacheDataSource {

        private val jokeErrorNoFav = JokeError.NoFavoriteJoke(manageResources)
        private val map = mutableMapOf<Int, JokeCloud>()

        override fun addOrRemove(id: Int, joke: JokeCloud): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.toUi()
            } else {
                map[id] = joke
                joke.toFavoriteUi()
            }
        }


        private var count = 0
        override fun fetch(jokeCacheCallback: JokeCacheCallback) {
            if (map.isEmpty()) {
                jokeCacheCallback.provideError(jokeErrorNoFav)
            } else{
                if (++count == map.size) count = 0
                jokeCacheCallback.provideJoke(map.toList()[count].second)}
        }


    }
}

interface JokeCacheCallback : ProvideError {
    fun provideJoke(joke: JokeCloud)

}

interface ProvideError {
    fun provideError(error: JokeError)
}