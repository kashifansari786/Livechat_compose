package com.example.compose_chatapp

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.compose_chatapp.constants.USER_NODE
import com.example.compose_chatapp.data.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by Mohammad Kashif Ansari on 15,April,2024
 */
@HiltViewModel
class LiveChatViewModel @Inject constructor(val auth:FirebaseAuth,val dataBase:FirebaseFirestore): ViewModel() {



    var inProgress= mutableStateOf(false)
    var eventMutableState= mutableStateOf<Event<String>?>(null)
    var signIn= mutableStateOf(false)
    var userData= mutableStateOf<UserData?>(null)


    init {
        val currentUser=auth.currentUser
        signIn.value=currentUser!=null
        currentUser?.uid?.let {
            getUserData(it)
        }
    }

    fun signUp(name:String,number:String,email:String,password:String){
        inProgress.value=true
        if(name.isEmpty() or number.isEmpty() or email.isEmpty() or password.isEmpty()){
            handleException(customException = "please fill all fields")
            return
        }
        inProgress.value=true
        dataBase.collection(USER_NODE).whereEqualTo("number",number).get().addOnSuccessListener {
            if(it.isEmpty){
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                    if(it.isSuccessful){
                        signIn.value=true
                        createOrUpdateProfile(name,number)
                        //navigateTo(navController = )
                    } else handleException(it.exception,"Signup failed")
                }
            }else{
                handleException(customException = "number already exist")
                inProgress.value=false
            }
        }

    }

    fun Login(email:String,password:String){
        if(email.isEmpty() or password.isEmpty()){
            handleException(customException = "Please fill all the fields")
            return
        }else{
            inProgress.value=true
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener() {
                if(it.isSuccessful){
                    signIn.value=true
                    inProgress.value=false
                    auth.currentUser?.uid?.let {
                        getUserData(it)
                    }
                }
            }
        }

    }

    private fun createOrUpdateProfile(name: String?=null, number: String?=null,imageUrl:String?=null) {
        var uId=auth.currentUser?.uid
        val userData=UserData(
            uId,name?:userData.value?.name,number?:userData.value?.number,imageUrl?:userData.value?.imageUrl
        )
        uId?.let {
            inProgress.value=true
            dataBase.collection(USER_NODE).document(uId).get().addOnSuccessListener {
                if(it.exists()){
                    //update user data
                }else
                {
                    dataBase.collection(USER_NODE).document(uId).set(userData)
                    inProgress.value=false
                    getUserData(uId)
                }
            }.addOnFailureListener{
                handleException(it,"cannot retrieve user")
            }

        }
    }

    private fun getUserData(uId: String) {
        inProgress.value=true
        dataBase.collection(USER_NODE).document(uId).addSnapshotListener{
            value,error->
            if(error!=null){

            }
            if(value!=null){
                var user=value.toObject<UserData>()
                userData.value=user
                inProgress.value=false
            }
        }
    }

    fun handleException(exception: Exception?=null,customException:String=""){
        val errMessage=exception?.localizedMessage?:""
        val message=if(customException.isNullOrEmpty()) errMessage else customException

        eventMutableState.value= Event(message)
        inProgress.value=false
    }
}

