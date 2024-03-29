package com.example.clientserviceapp.data.cache

import com.example.clientserviceapp.JokeCache
import com.example.clientserviceapp.data.*
import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.ManageResources
import io.realm.Realm

interface CacheDataSource : DataSource {

    suspend fun addOrRemove(id: Int, joke: Joke): JokeUi


    class Base(
        private val realm: ProvideRealm,
        manageResources: ManageResources,
        private val jokeError: JokeError = JokeError.NoFavoriteJoke(manageResources),
        private val mapper: Joke.Mapper<JokeCache> = ToCache(),
        private val toFavoriteUi: Joke.Mapper<JokeUi> = ToFavoriteUi(),
        private val toUi: Joke.Mapper<JokeUi> = ToUi(),
    ) : CacheDataSource {

        override suspend fun addOrRemove(id: Int, joke: Joke): JokeUi {
            realm.provideRealm().use { realm ->
                val jokeCached = realm.where(JokeCache::class.java).equalTo("id", id).findFirst()
                return if (jokeCached == null) {
                    val jokeCache = joke.map(mapper)
                    realm.executeTransaction { realm ->
                        realm.insert(jokeCache)
                    }
                    joke.map(toFavoriteUi)
                } else {
                    realm.executeTransaction { realm ->
                        jokeCached.deleteFromRealm()
                    }
                    joke.map(toUi)
                }
            }
        }

        override suspend fun fetch(): JokeResult {
            realm.provideRealm().use { realm ->
                val jokes = realm.where(JokeCache::class.java).findAll()
                return if (jokes.isEmpty()) {
                    JokeResult.Failure(jokeError)
                } else {
                    val jokeCached = jokes.random()
                    JokeResult.Success(realm.copyFromRealm(jokeCached), true)
                }
            }
        }

    }

    class Fake(manageResources: ManageResources) : CacheDataSource {

        private val jokeError = JokeError.NoFavoriteJoke(manageResources)
        private val map = mutableMapOf<Int, Joke>()

        override suspend fun addOrRemove(id: Int, joke: Joke): JokeUi {
            return if (map.containsKey(id)) {
                map.remove(id)
                joke.map(ToUi())
            } else {
                map[id] = joke
                joke.map(ToFavoriteUi())
            }
        }


        private var count = 0
        override suspend fun fetch(): JokeResult {
            return if (map.isEmpty()) {
                JokeResult.Failure(jokeError)
            } else {
                if (++count == map.size) count = 0
                JokeResult.Success(map.toList()[count].second, true)
            }
        }


    }

}

interface DataSource {
    suspend fun fetch(): JokeResult
}

interface JokeResult : Joke {
    fun isSuccessful(): Boolean
    fun errorMessage(): String
    fun toFavorite(): Boolean

    class Success(private val joke: Joke, private val toFavorite: Boolean) : JokeResult {
        override fun isSuccessful() = true
        override fun errorMessage() = ""
        override fun toFavorite(): Boolean = toFavorite

        override suspend fun <T> map(mapper: Joke.Mapper<T>) = joke.map(mapper)
    }

    class Failure(private val error: JokeError) : JokeResult {
        override fun isSuccessful() = false
        override fun errorMessage() = error.message()
        override fun toFavorite(): Boolean = false

        override suspend fun <T> map(mapper: Joke.Mapper<T>) = throw java.lang.IllegalStateException()
    }
}

interface ProvideRealm {
    fun provideRealm(): Realm
}