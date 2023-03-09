package com.example.clientserviceapp

import com.google.gson.Gson
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownServiceException

interface JokeService {

    fun joke(callback: ServiceCallback)

    class Base(private val gson:Gson) : JokeService {


        override fun joke(callback: ServiceCallback) {
            Thread {
                var connection: HttpURLConnection? = null
                try {
                    var url = URL(URL)
                    var connection = url.openConnection() as HttpURLConnection
                    InputStreamReader(BufferedInputStream(connection.inputStream)).use {
                        val text = it.readText()
                        callback.returnSuccess(gson.fromJson(text,JokeCloud::class.java))
                    }
                } catch (e: Exception) {
                    if (e is UnknownServiceException || e is ConnectException) {
                        callback.returnError(ErrorType.NO_CONNECTION)
                    } else {
                        callback.returnError(ErrorType.OTHER)
                    }
                } finally {
                    connection?.disconnect()
                }
            }.start()
        }

    }

    companion object {
        private const val URL = "https://official-joke-api.appspot.com/random_joke"
    }
}

interface ServiceCallback {

    fun returnSuccess(data: JokeCloud)

    fun returnError(error: ErrorType)


}

enum class ErrorType {

    NO_CONNECTION,
    OTHER
}