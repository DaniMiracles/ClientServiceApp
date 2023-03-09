package com.example.clientserviceapp

import java.util.Timer
import java.util.TimerTask

class FakeModel(manageResources: ManageResources) : Model<Joke, JokeError> {

    private var callback: ResultCallback<Joke, JokeError>? = null
    private val noConnection = JokeError.NoConnection(manageResources)
    private val serviceUnavailable = JokeError.ServiceUnavailable(manageResources)

    private var count = 0
    override fun fetch() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                if (count % 2 == 1) {
                    callback?.provideSuccess(Joke("fake success $count ", "punchline"))
                } else if (count % 3 == 0) {
                    callback?.provideError(noConnection)
                } else {
                    callback?.provideError(serviceUnavailable)
                }
                count++
            }
        }, 2000)


    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, JokeError>) {
        callback = resultCallback
    }
}