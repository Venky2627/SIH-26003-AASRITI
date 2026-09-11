package com.sih26003.aasriti.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Listens for system network connectivity state transitions on Android.
 *
 * Automatically triggers background sync via SyncManager & WorkManager
 * when internet connectivity becomes available.
 */
class NetworkConnectivityObserver(
    private val context: Context,
    private val syncManager: SyncManager
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val scope = CoroutineScope(Dispatchers.IO)

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("NetworkConnectivityObserver", "Network available. Triggering background sync batch...")
            triggerSync()
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            super.onCapabilitiesChanged(network, capabilities)
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            if (hasInternet) {
                Log.d("NetworkConnectivityObserver", "Validated internet connection active.")
                triggerSync()
            }
        }
    }

    fun register() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
            Log.d("NetworkConnectivityObserver", "Registered network connectivity callback.")
        } catch (e: Exception) {
            Log.e("NetworkConnectivityObserver", "Failed to register network callback: ${e.message}", e)
        }
    }

    fun unregister() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e("NetworkConnectivityObserver", "Error unregistering network callback: ${e.message}")
        }
    }

    private fun triggerSync() {
        scope.launch {
            try {
                val processed = syncManager.processPendingBatch()
                Log.d("NetworkConnectivityObserver", "Opportunistic network sync completed: $processed items processed.")
            } catch (e: Exception) {
                Log.e("NetworkConnectivityObserver", "Network sync failed: ${e.message}", e)
            }
        }
        SyncWorker.enqueue(context)
    }
}
