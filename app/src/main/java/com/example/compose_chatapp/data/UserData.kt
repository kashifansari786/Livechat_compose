package com.example.compose_chatapp.data

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */
data class UserData(
    var userId:String?=null,
    var name:String?=null,
    var number:String?=null,
    var imageUrl:String?=null,
    var email:String?=null,
){
    fun toMap()= mapOf(
        "userId" to userId,
        "name" to name,
        "number" to number,
        "imageUrl" to imageUrl,
        "email" to email,

    )
}
