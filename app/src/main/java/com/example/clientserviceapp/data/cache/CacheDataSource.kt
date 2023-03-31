package com.example.clientserviceapp.data.cache

import com.example.clientserviceapp.JokeCache
import com.example.clientserviceapp.data.*
import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource : DataSource {

    fun addOrRemove(id: Int, joke: Joke): JokeUi


    class Base(
        private val realm: ProvideRealm,
        manageResources: ManageResources,
        private val jokeError: JokeError = JokeError.NoFavoriteJoke(manageResources),
        private val mapper: Joke.Mapper<JokeCache> = ToCache(),
        private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
        private val toUi: Joke.Mapper<JokeUi> = ToUi(),
    ) : CacheDataSource {

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {

            realm.provideRealm().let {
                val jokeCached = it.where(JokeCache::class.java).equalTo("id", id).findFirst()
                if (jokeCached == null) {

                    it.executeTransaction { realm ->
                        val jokeCache = joke.map(mapper)
                        realm.insert(jokeCache)
                    }
                    return joke.map(toFavoriteUi)
                } else {
                    it.executeTransaction { realm ->
                        jokeCached.deleteFromRealm()
                    }
                    return joke.map(toUi)
                }
            }
        }

        override fun fetch(jokeCallback: JokeCallback) {
            realm.provideRealm().let {
                val jokes = it.where(JokeCache::class.java).findAll()
                if (jokes.isEmpty()) {
                    jokeCallback.provideError(jokeError)
                } else {
                    val jokeCached = jokes.random()
                    jokeCallback.provideJoke(it.copyFromRealm(jokeCached))
                }
            }
        }

    }

    class Fake(manageResources: ManageResources) : CacheDataSource {

        private val jokeError = JokeError.NoFavoriteJoke(manageResources)
        private val map = mutableMapOf<Int, Joke>()

        override fun addOrRemove(id: Int, joke: Joke): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }


        private var count = 0
        override fun fetch(jokeCallback: JokeCallback) {
            if (map.isEmpty()) {
                jokeCallback.provideError(jokeError)
            } else {
                if (++count == map.size) count = 0
                jokeCallback.provideJoke(map.toList()[count].second)
            }
        }


    }

}

interface DataSource {
    fun fetch(jokeCallback: JokeCallback)
}

interface JokeCallback : ProvideError {
    fun provideJoke(joke: Joke)
}

interface ProvideError {
    fun provideError(error: JokeError)
}

interface ProvideRealm {
    fun provideRealm(): Realm
}