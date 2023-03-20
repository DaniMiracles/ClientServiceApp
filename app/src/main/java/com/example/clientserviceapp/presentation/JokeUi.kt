package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import com.example.clientserviceapp.R

abstract class JokeUi(
    private val text: String,
    private val punchline: String,
    @DrawableRes private val iconResId: Int
) {

    fun show(jokeUiCallback: JokeUiCallback) {
        jokeUiCallback.provideText("$text\n$punchline")
        jokeUiCallback.provideIconResId(iconResId)
    }


    class Base(text: String, punchline: String) :
        JokeUi(text, punchline, R.drawable.ic_favorite_empty)

    class Favorite(text: String, punchline: String) :
        JokeUi(text, punchline, R.drawable.ic_favorite_filled)

    class Failed(text: String) : JokeUi(text, "", 0)


}