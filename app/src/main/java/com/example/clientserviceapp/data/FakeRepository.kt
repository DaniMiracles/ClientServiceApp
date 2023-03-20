package com.example.clientserviceapp.data

import com.example.clientserviceapp.presentation.JokeUi
import com.example.clientserviceapp.presentation.ManageResources

class FakeRepository(private val manageResources: ManageResources) : Repository<JokeUi, JokeError> {

    private val serviceError by lazy { JokeError.ServiceUnavailable(manageResources) }

    private var callback: ResultCallback<JokeUi, JokeError>? = null
    private var count = 0

    override fun fetch() {
        when (++count % 3) {
            0 -> callback?.provideSuccess(JokeUi.Base("joke base $count", ""))
            1 -> callback?.provideSuccess(JokeUi.Favorite("joke favorite $count", ""))
            2 -> callback?.provideError(serviceError)
        }
    }

    override fun clear() {
        callback = null
    }

    override fun init(resultCallback: ResultCallback<JokeUi, JokeError>) {
        callback = resultCallback
    }

    override fun changeJokeStatus(resultCallback: ResultCallback<JokeUi, JokeError>) {
        //todo
    }

    override fun chooseFavorites(favorites: Boolean) {
       //todo
    }
}