package com.example.clientserviceapp.presentation

import com.example.clientserviceapp.data.Joke
import com.example.clientserviceapp.data.JokeError
import com.example.clientserviceapp.data.Repository
import com.example.clientserviceapp.data.cache.JokeResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MainViewModelTest() {
    private lateinit var repository: FakeRepository
    private lateinit var toFavoriteMapper: FakeMapper
    private lateinit var toBaseMapper: FakeMapper
    private lateinit var viewModel: MainViewModel
    private lateinit var fakeJokeUiCallback: FakeJokeUiCallback
    private lateinit var dispatcherList: FakeDispatcherList

    @Before
    fun set_up() {
        repository = FakeRepository()
        toFavoriteMapper = FakeMapper(true)
        toBaseMapper = FakeMapper(false)
        fakeJokeUiCallback = FakeJokeUiCallback()
        dispatcherList = FakeDispatcherList()


        viewModel = MainViewModel(
            repository,
            toFavoriteMapper,
            toBaseMapper,
            dispatcherList.io(),
            dispatcherList.ui()
        )

        viewModel.init(fakeJokeUiCallback)
    }


    @Test
    fun test_successful_not_favorite() {
        repository.returnFakeJokeResult = FakeJokeResult(
            FakeJoke(
                "testType",
                "testText", "testPunchline", 12
            ), true, false, ""
        )


        viewModel.getJoke()
        val expectedText = "testText_testPunchline"
        val expectedId = 12

        assertEquals(expectedText, fakeJokeUiCallback.provideTextList[0])
        assertEquals(expectedId, fakeJokeUiCallback.provideIconResIdList[0])

        assertEquals(1, fakeJokeUiCallback.provideTextList.size)
        assertEquals(1, fakeJokeUiCallback.provideIconResIdList.size)

    }

    @Test
    fun test_successful_favorite() {
        repository.returnFakeJokeResult = FakeJokeResult(
            FakeJoke(
                "testTypeFav",
                "testTextFav", "testPunchlineFav", 5
            ), true, true, ""
        )


        viewModel.getJoke()
        val expectedText = "testTextFav_testPunchlineFav"
        val expectedId = 6

        assertEquals(expectedText, fakeJokeUiCallback.provideTextList[0])
        assertEquals(expectedId, fakeJokeUiCallback.provideIconResIdList[0])

        assertEquals(1, fakeJokeUiCallback.provideTextList.size)
        assertEquals(1, fakeJokeUiCallback.provideIconResIdList.size)
    }

    @Test
    fun test_not_successful() {
        repository.returnFakeJokeResult = FakeJokeResult(
            FakeJoke(
                "testTypeFav",
                "testTextFav", "testPunchlineFav", 0
            ), false, true, "testErrorMessage"
        )


        viewModel.getJoke()
        val expectedText = "testErrorMessage\n"
        val expectedId = 0

        assertEquals(expectedText, fakeJokeUiCallback.provideTextList[0])
        assertEquals(expectedId, fakeJokeUiCallback.provideIconResIdList[0])

        assertEquals(1, fakeJokeUiCallback.provideTextList.size)
        assertEquals(1, fakeJokeUiCallback.provideIconResIdList.size)

    }

    @Test
    fun test_change_joke_status(){
        repository.returnChangeJokeStatus = FakeJokeUi("changeText","changePunchline",5,false)
        viewModel.changeJokeStatus()

        val expectedText = "changeText_changePunchline"
        val expectedId = 5

        assertEquals(expectedText, fakeJokeUiCallback.provideTextList[0])
        assertEquals(expectedId, fakeJokeUiCallback.provideIconResIdList[0])

        assertEquals(1, fakeJokeUiCallback.provideTextList.size)
        assertEquals(1, fakeJokeUiCallback.provideIconResIdList.size)
    }
}

private class FakeDispatcherList : DispatcherList {
    private val dispatcher = TestCoroutineDispatcher()

    override fun io(): CoroutineDispatcher = dispatcher

    override fun ui(): CoroutineDispatcher = dispatcher

}

private class FakeJokeUiCallback : JokeUiCallback {
    val provideTextList = mutableListOf<String>()
    override fun provideText(text: String) {
        provideTextList.add(text)
    }

    val provideIconResIdList = mutableListOf<Int>()
    override fun provideIconResId(iconResId: Int) {
        provideIconResIdList.add(iconResId)
    }

}

private class FakeMapper(private var toFavorite: Boolean) : Joke.Mapper<JokeUi> {

    override suspend fun map(type: String, mainText: String, punchline: String, id: Int): JokeUi {
        return FakeJokeUi(mainText, punchline, id, toFavorite)
    }
}

private data class FakeJokeUi(
    private val text: String,
    private val punchline: String,
    private val id: Int,
    private val toFavorite: Boolean
) : JokeUi{
    override fun show(jokeUiCallback: JokeUiCallback) {
        jokeUiCallback.provideText(text + "_" + punchline)
        jokeUiCallback.provideIconResId(if (toFavorite) id + 1 else id)
    }
}

private data class FakeJoke(
    private val type: String,
    private val mainText: String,
    private val punchline: String,
    private val id: Int
) : Joke {
    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return mapper.map(type, mainText, punchline, id)
    }
}

private data class FakeJokeResult(
    private val joke: Joke,
    private val isSuccessful: Boolean,
    private val isFavorite: Boolean,
    private val errorMessage: String
) : JokeResult {
    override fun isSuccessful(): Boolean = isSuccessful

    override fun errorMessage(): String = errorMessage

    override fun toFavorite(): Boolean = isFavorite

    override suspend fun <T> map(mapper: Joke.Mapper<T>): T {
        return joke.map(mapper)
    }

}

private class FakeRepository : Repository<JokeUi, JokeError> {

    var returnFakeJokeResult: JokeResult? = null
    override suspend fun fetch(): JokeResult {
        return returnFakeJokeResult!!
    }

    var returnChangeJokeStatus : JokeUi? = null
    override suspend fun changeJokeStatus(): JokeUi {
        return returnChangeJokeStatus!!
    }

    override fun chooseFavorites(favorites: Boolean) {
        TODO("Not yet implemented")
    }

}