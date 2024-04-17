package com.example.compose_chatapp.data

/**
 * Created by Mohammad Kashif Ansari on 17,April,2024
 */
data class ChatData(val chatId:String?="", val user1:ChatUser= ChatUser(), val user2:ChatUser=ChatUser())
