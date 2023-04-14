package com.example.clientserviceapp.presentation

import androidx.annotation.DrawableRes
import com.example.clientserviceapp.R

interface JokeUi {
    fun show(jokeUiCallback: JokeUiCallback)

    abstract class Abstract(
        private val text: String,
        private val punchline: String,
        @DrawableRes private val iconResId: Int
    ) : JokeUi {

        override fun show(jokeUiCallback: JokeUiCallback) {
            jokeUiCallback.provideText("$text\n$punchline")
            jokeUiCallback.provideIconResId(iconResId)
        }
    }

        class Base(text: String, punchline: String) :
            Abstract(text, punchline, R.drawable.ic_favorite_empty)

        class Favorite(text: String, punchline: String) :
           Abstract(text, punchline, R.drawable.ic_favorite_filled)

        class Failed(text: String) : Abstract(text, "", 0)
    }
