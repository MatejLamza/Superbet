package matej.lamza.superbet.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import matej.lamza.core_model.Betshop

/**
 * This is a base activity that can be inherited
 */
open class BaseMapActivity : AppCompatActivity() {
    internal lateinit var clusterManager: ClusterManager<Betshop>
    internal lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CHECK_SETTINGS = 90
    }

    suspend fun initMap(mapResourceId: Int) {
        val mapFragment = supportFragmentManager.findFragmentById(mapResourceId) as SupportMapFragment
        map = mapFragment.awaitMap()
        map.awaitMapLoad()
    }

    fun showGPSPromptDialog(throwable: Throwable) {
        if (throwable is ResolvableApiException)
            kotlin.runCatching { throwable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) }
    }
}
