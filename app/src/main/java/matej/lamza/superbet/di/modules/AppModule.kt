package matej.lamza.superbet.di.modules

import android.app.Activity
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import kotlinx.coroutines.CoroutineScope
import matej.lamza.superbet.utils.location.DefaultLocationClient
import matej.lamza.superbet.utils.location.LocationClient
import matej.lamza.superbet.utils.maps.BetshopClusterManager
import matej.lamza.superbet.utils.maps.ClusterManager
import org.koin.dsl.module

val appModule = module {
    single<LocationClient> { (context: Context, locationClient: FusedLocationProviderClient) ->
        DefaultLocationClient(context, locationClient)
    }

    single<ClusterManager<*>> { (activity: Activity, googleMaps: GoogleMap, scope: CoroutineScope) ->
        BetshopClusterManager(activity, googleMaps, scope)
    }
}
