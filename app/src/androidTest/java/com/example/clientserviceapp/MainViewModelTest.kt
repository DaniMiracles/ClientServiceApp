package com.example.clientserviceapp

import org.junit.Assert.assertEquals
import org.junit.Test


class MainViewModelTest {

    @Test
    fun test_success() {
        val model = FakeModelTest()
        model.returnSuccess = true
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback {
            override fun provideText(text: String) {
                assertEquals("Joke" + "\n" + "PunchLine", text)
            }
        })
        viewModel.getJoke()
    }

    @Test
    fun test_error() {
         val model = FakeModelTest()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : TextCallback{
            override fun provideText(text: String) {
                assertEquals("fake error",text)
            }
        })
        viewModel.getJoke()
    }

}

private class FakeModelTest : Model<Joke, JokeError> {

    var returnSuccess = true
    private var callback: ResultCallback<Joke, JokeError>? = null

    override fun fetch() {
        if (returnSuccess) {
            callback?.provideSuccess(Joke("Joke", "PunchLine"))
        } else {
            callback?.provideError(FakeJokeError())
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<Joke, JokeError>) {
        this.callback = resultCallback
    }

}

private class FakeJokeError : JokeError {
    override fun message(): String {
        return "fake error"
    }

}