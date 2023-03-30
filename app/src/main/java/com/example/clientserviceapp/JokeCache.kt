package com.example.clientserviceapp

import com.example.clientserviceapp.data.Joke
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class JokeCache : RealmObject(), Joke {

    @PrimaryKey
    var id: Int = -1
    var mainText: String = ""
    var punchline: String = ""
    var type: String = ""


    override fun <T> map(mapper: Joke.Mapper<T>): T = mapper.map(type, mainText, punchline, id)

}