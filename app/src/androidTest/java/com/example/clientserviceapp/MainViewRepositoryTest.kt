package com.example.clientserviceapp

import com.example.clientserviceapp.data.JokeError
import com.example.clientserviceapp.data.Repository
import com.example.clientserviceapp.data.ResultCallback
import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.MainViewModel
import com.example.clientserviceapp.presentation.JokeUiCallback
import org.junit.Assert.assertEquals
import org.junit.Test


class MainViewRepositoryTest {

    @Test
    fun test_success() {
        val model = FakeModelTest()
        model.returnSuccess = true
        val viewModel = MainViewModel(model)
        viewModel.init(object : JokeUiCallback {
            override fun provideText(text: String) {
                assertEquals("Joke" + "\n" + "PunchLine", text)
            }

            override fun provideIconResId(iconResId: Int) {
                TODO("Not yet implemented")
            }
        })
        viewModel.getJoke()
    }

    @Test
    fun test_error() {
         val model = FakeModelTest()
        model.returnSuccess = false
        val viewModel = MainViewModel(model)
        viewModel.init(object : JokeUiCallback {
            override fun provideText(text: String) {
                assertEquals("fake error",text)
            }

            override fun provideIconResId(iconResId: Int) {
                TODO("Not yet implemented")
            }
        })
        viewModel.getJoke()
    }

}

private class FakeModelTest : Repository<JokeUi, JokeError> {

    var returnSuccess = true
    private var callback: ResultCallback<JokeUi, JokeError>? = null

    override fun fetch() {
        if (returnSuccess) {
            callback?.provideSuccess(JokeUi.Base("Joke", "PunchLine"))
        } else {
            callback?.provideError(FakeJokeError())
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<JokeUi, JokeError>) {
        this.callback = resultCallback
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, JokeError>) {
        TODO("Not yet implemented")
    }

    override fun chooseFavorites(favorites: Boolean) {
        TODO("Not yet implemented")
    }

}

private class FakeJokeError : JokeError {
    override fun message(): String {
        return "fake error"
    }

}