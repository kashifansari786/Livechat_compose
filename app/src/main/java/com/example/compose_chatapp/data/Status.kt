package com.example.compose_chatapp.data

/**
 * Created by Mohammad Kashif Ansari on 18,April,2024
 */
data class Status(val user:ChatUser= ChatUser(), val imageUrl:String?="",val timeStamp:Long?=null)
