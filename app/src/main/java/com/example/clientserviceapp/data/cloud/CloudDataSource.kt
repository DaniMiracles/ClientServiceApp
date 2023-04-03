package com.example.clientserviceapp.data.cloud



import com.example.clientserviceapp.data.JokeError
import com.example.clientserviceapp.data.cache.DataSource
import com.example.clientserviceapp.data.cache.JokeResult
import com.example.clientserviceapp.presentation.ManageResources
import java.net.ConnectException
import java.net.UnknownServiceException

interface CloudDataSource : DataSource {


    class Base(
        private val jokeService: JokeService,
        private val manageResources: ManageResources,
        private val noConnection: JokeError = JokeError.NoConnection(manageResources),
        private val serviceError: JokeError = JokeError.ServiceUnavailable(manageResources)
    ) : CloudDataSource {

        override fun fetch(): JokeResult =
            try {
                val response = jokeService.joke().execute()
                JokeResult.Success(response.body()!!, false)
            } catch (e: Exception) {
                JokeResult.Failure(
                    if (e is UnknownServiceException || e is ConnectException)
                        noConnection
                    else
                        serviceError
                )
            }
    }

}
