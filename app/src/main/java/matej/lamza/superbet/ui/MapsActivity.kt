package matej.lamza.superbet.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.superbet.R
import matej.lamza.superbet.databinding.ActivityMapsBinding
import matej.lamza.superbet.utils.PermissionsHandler
import matej.lamza.superbet.utils.location.LocationClient
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var binding: ActivityMapsBinding? = null
    private lateinit var map: GoogleMap
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private val locationClient by inject<LocationClient> { parametersOf(this, fusedLocationClient) }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    lifecycleScope.launch {
                        locationClient.getCurrentLocation()
                            .catch {
                                Log.d(TAG, "Caught exception: ")
                                showGPSDialog(it)
                            }
                            .collectLatest { Log.d(TAG, "Dobio sam: $it ") }
                    }
                }

                else -> {
                    Log.d(TAG, "0 bodova baki moj ")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_Superbet)
        setContentView(binding?.root)

        initMap()
        setupListeners()
    }

    private fun setupListeners() {
        binding?.currentLocation?.setOnClickListener {
            Log.d(TAG, "Click")
            PermissionsHandler.requestPermission(this, this, requestPermissionLauncher)
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun showGPSDialog(exception: Throwable) {
        if (exception is ResolvableApiException) {
            runCatching { exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    companion object {
        const val REQUEST_CHECK_SETTINGS = 90
    }

}
