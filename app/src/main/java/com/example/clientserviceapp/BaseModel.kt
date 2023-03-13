package com.example.clientserviceapp

import retrofit2.Call
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownServiceException

class BaseModel(
    private val jokeService: JokeService,
    private val manageResources: ManageResources
) : Model<Joke, JokeError> {

    private var callback: ResultCallback<Joke, JokeError>? = null
    private val noConnection by lazy { JokeError.NoConnection(manageResources) }
    private val serviceError by lazy { JokeError.ServiceUnavailable(manageResources) }

    override fun fetch() {
        jokeService.joke().enqueue(object : retrofit2.Callback<JokeCloud> {
            override fun onResponse(call: Call<JokeCloud>, response: Response<JokeCloud>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body == null)
                        callback?.provideError(serviceError)
                    else
                        callback?.provideSuccess(body.toJoke())
                } else {
                    callback?.provideError(serviceError)
                }
            }

            override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                if (t is UnknownServiceException || t is ConnectException) {
                    callback?.provideError(noConnection)
                } else {
                    callback?.provideError(serviceError)
                }
            }
        })
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, JokeError>) {
        callback = resultCallback
    }
}