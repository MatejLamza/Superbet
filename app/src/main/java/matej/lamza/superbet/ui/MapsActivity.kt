package matej.lamza.superbet.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.superbet.R
import matej.lamza.superbet.base.BaseMapActivity
import matej.lamza.superbet.databinding.ActivityMapsBinding
import matej.lamza.superbet.utils.PermissionsHandler
import matej.lamza.superbet.utils.location.LocationClient
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

const val TAG = "MapsActivity"

class MapsActivity : BaseMapActivity() {
    private lateinit var mapBinding: ActivityMapsBinding

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationClient by inject<LocationClient> { parametersOf(this, fusedLocationClient) }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    lifecycleScope.launch {
                        locationClient
                            .getCurrentLocation()
                            .catch { showGPSPromptDialog(it) }
                            .collectLatest { Log.d(TAG, "Received current location: $it ") }
                    }
                }
                else -> {
                    Log.d(TAG, "Permissions are not granted ")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_Superbet)

        mapBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)

        initMap(R.id.map)
        setupListeners()
    }

    private fun setupListeners() {
        mapBinding.currentLocation.setOnClickListener {
            PermissionsHandler.requestPermission(this, this, requestPermissionLauncher)
        }
    }
}
