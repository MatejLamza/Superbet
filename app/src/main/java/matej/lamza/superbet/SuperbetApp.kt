package matej.lamza.superbet

import android.app.Application
import matej.lamza.superbet.di.SuperbetDI

class SuperbetApp : Application() {

    private val superbetDI: SuperbetDI by lazy { SuperbetDI(this) }
    override fun onCreate() {
        super.onCreate()
        superbetDI.init()
    }
}
