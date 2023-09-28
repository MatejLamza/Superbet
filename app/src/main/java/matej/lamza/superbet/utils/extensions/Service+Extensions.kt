package matej.lamza.superbet.utils.extensions

import android.location.LocationManager


val LocationManager.isGPSEnabled
    get() = isProviderEnabled(LocationManager.GPS_PROVIDER) || isProviderEnabled(LocationManager.NETWORK_PROVIDER)
