package com.sih26003.smritisetu.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Contract for checking real-time network connectivity.
 * Enables offline-first guarantees and clean simulation of Airplane mode.
 */
interface NetworkConnectivityMonitor {
    fun isOnline(): Boolean
}

/**
 * Real Android system network monitor using Android ConnectivityManager.
 */
class AndroidNetworkConnectivityMonitor(
    private val context: Context
) : NetworkConnectivityMonitor {
    override fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                 capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                 capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }
}

/**
 * Testable monitor allowing programmatic toggling between Online and Offline (e.g. Airplane Mode).
 */
class TestableNetworkConnectivityMonitor(
    @Volatile private var onlineState: Boolean = true
) : NetworkConnectivityMonitor {
    override fun isOnline(): Boolean = onlineState

    fun setOnline(online: Boolean) {
        onlineState = online
    }
}
