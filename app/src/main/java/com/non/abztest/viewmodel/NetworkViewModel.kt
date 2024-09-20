package com.non.abztest.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class NetworkViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> = _isNetworkAvailable

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.postValue(true)
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.postValue(false)
        }
    }

    init {
        checkNetworkAvailability()
        registerNetworkCallback()
    }

    private fun checkNetworkAvailability() {
        val network = connectivityManager.activeNetwork
        if (network == null) {
            _isNetworkAvailable.value = false
            return
        }

        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        val hasInternetCapability =
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        _isNetworkAvailable.value = hasInternetCapability == true
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onCleared() {
        super.onCleared()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
