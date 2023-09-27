package matej.lamza.superbet.di.modules

import android.app.Activity
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import matej.lamza.superbet.utils.LocationHandler
import matej.lamza.superbet.utils.location.DefaultLocationClient
import matej.lamza.superbet.utils.location.LocationClient
import org.koin.dsl.module

val appModule = module {
    single<LocationHandler> { (activity: Activity) -> LocationHandler(activity = activity) }

    single<LocationClient> { (context: Context, locationClient: FusedLocationProviderClient) ->
        DefaultLocationClient(context, locationClient)
    }

}
