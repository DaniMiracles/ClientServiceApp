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
                    callback?.provideSuccess(response.body()!!.toJoke())
                } else {
                    callback?.provideError(serviceError)
                }
            }

            override fun onFailure(call: Call<JokeCloud>, t: Throwable) {
                if (t is UnknownServiceException || t is ConnectException){
                    callback?.provideError(noConnection)
                }else{
                    callback?.provideError(serviceError)
                }
            }
        })

//        jokeService.joke(object : ServiceCallback {
//            override fun returnSuccess(data: JokeCloud) {
//                callback?.provideSuccess(data.toJoke())
//            }
//
//            override fun returnError(error: ErrorType) {
//                when (error) {
//                    ErrorType.NO_CONNECTION -> callback?.provideError(noConnection)
//                    ErrorType.OTHER -> callback?.provideError(serviceError)
//                }
//            }
//        })
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, JokeError>) {
        callback = resultCallback
    }
}