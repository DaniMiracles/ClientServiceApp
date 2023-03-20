package com.example.clientserviceapp.data

import androidx.annotation.StringRes
import com.example.clientserviceapp.presentation.ManageResources
import com.example.clientserviceapp.R

interface JokeError {
    fun message(): String


    abstract class Abstract(
        private val manageResources: ManageResources,
        @StringRes private val messageId: Int
    ) : JokeError {
        override fun message(): String {
            return manageResources.string(messageId)
        }
    }

    class NoConnection(manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_no_connection)

    class ServiceUnavailable(manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_unavailable)

    class NoFavoriteJoke(manageResources: ManageResources) :
        Abstract(manageResources, R.string.no_favorite_joke)

    class CacheEmpty(manageResources: ManageResources) :
        Abstract(manageResources, R.string.empty_cache)
}