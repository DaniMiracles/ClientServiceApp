package com.example.clientserviceapp

class BaseModel(
    private val jokeService: JokeService,
    private val manageResources: ManageResources
) : Model<Joke, JokeError> {

    private var callback: ResultCallback<Joke, JokeError>? = null
    private val noConnection by lazy { JokeError.NoConnection(manageResources) }
    private val serviceError by lazy { JokeError.ServiceUnavailable(manageResources) }

    override fun fetch() {
        jokeService.joke(object : ServiceCallback {
            override fun returnSuccess(data: JokeCloud) {
                callback?.provideSuccess(data.toJoke())
            }

            override fun returnError(error: ErrorType) {
                when (error) {
                    ErrorType.NO_CONNECTION -> callback?.provideError(noConnection)
                    ErrorType.OTHER -> callback?.provideError(serviceError)
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