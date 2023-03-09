package com.example.clientserviceapp

import kotlin.Error

class MainViewModel(private val model: Model<Joke, JokeError>) {

    private var textCallback: TextCallback = TextCallback.Empty()

    fun getJoke() {
        model.fetch()
    }

    fun init(textCallback: TextCallback) {
        this.textCallback = textCallback

        model.init(object : ResultCallback<Joke, JokeError> {
            override fun provideSuccess(data: Joke) {
                textCallback.provideText(data.getJokeUi())
            }

            override fun provideError(error: JokeError) {
                textCallback.provideText(error.message())
            }
        })
    }

    fun clear() {
        textCallback = TextCallback.Empty()
        model.clear()
    }
}

interface TextCallback {

    fun provideText(text: String)

    class Empty : TextCallback {
        override fun provideText(text: String) = Unit

    }
}