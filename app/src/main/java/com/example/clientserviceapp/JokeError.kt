package com.example.clientserviceapp

import androidx.annotation.StringRes

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

    class NoConnection( manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_no_connection)

    class ServiceUnavailable( manageResources: ManageResources) :
        Abstract(manageResources, R.string.service_unavailable)
}