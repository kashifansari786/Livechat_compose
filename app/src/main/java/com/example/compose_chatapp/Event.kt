package com.example.compose_chatapp

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */
open class Event<out T>(val content:T) {
    var hasBeenHandled=false
    fun getContentOrNull():T?{
        return if(hasBeenHandled) null else {
            hasBeenHandled=true
            content
        }
    }
}