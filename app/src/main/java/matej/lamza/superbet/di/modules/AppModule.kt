package matej.lamza.superbet.di.modules

import android.app.Activity
import matej.lamza.superbet.utils.LocationHandler
import org.koin.dsl.module

val appModule = module {
    single<LocationHandler> { (activity: Activity) -> LocationHandler(activity = activity) }

}
