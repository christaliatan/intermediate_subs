package com.dicoding.picodiploma.storyApp.view.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.storyApp.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {
    fun getSession(): LiveData<Boolean> {
        return pref.getSession().asLiveData()
    }

    fun getToken(): LiveData<String> {
        return pref.getToken().asLiveData()
    }

    fun setLoginData(name: String, token: String, isLogin: Boolean){
        viewModelScope.launch {
            pref.saveUser(name, token, isLogin)
        }
    }
}