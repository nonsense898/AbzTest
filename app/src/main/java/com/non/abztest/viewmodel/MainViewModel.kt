package com.non.abztest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.non.abztest.model.Position
import com.non.abztest.model.RegistrationResponse
import com.non.abztest.model.UserPost
import com.non.abztest.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    val users = repository.getUsers().cachedIn(viewModelScope)

    private val _positions = MutableLiveData<List<Position>>()
    val positions: LiveData<List<Position>> get() = _positions

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _reloadUsers = MutableLiveData<Boolean>()
    val reloadUsers: LiveData<Boolean> get() = _reloadUsers

    fun clearAndReloadUsers() {
        repository.clearUserCache()
        _reloadUsers.postValue(true)
    }

    fun finishReloadingUsers() {
        _reloadUsers.value = false
    }

    fun loadPositions() {
        viewModelScope.launch {
            try {
                val positionsResponse = repository.getPositions()
                _positions.postValue(positionsResponse.positions)
            } catch (e: Exception) {
                _error.postValue("Exception: ${e.message}")
            }
        }
    }

    fun registerUser(user: UserPost, photoFile: File, onResponse: (response: Response<RegistrationResponse>?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.registerUser(user, photoFile)
                onResponse(response)
                if (response.isSuccessful) {
                    clearAndReloadUsers()
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
                onResponse(null)
            }
        }
    }
}