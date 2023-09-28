package matej.lamza.superbet.base

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * This is a base activity that can be inherited
 */
open class BaseMapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CHECK_SETTINGS = 90
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Sydney")
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    fun initMap(mapResourceId: Int) {
        val mapFragment = supportFragmentManager.findFragmentById(mapResourceId) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun showGPSPromptDialog(throwable: Throwable) {
        if (throwable is ResolvableApiException)
            kotlin.runCatching { throwable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) }
    }
}
