package matej.lamza.superbet.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.MapMarkerState
import matej.lamza.superbet.R
import matej.lamza.superbet.base.BaseMapActivity
import matej.lamza.superbet.databinding.ActivityMapsBinding
import matej.lamza.superbet.utils.PermissionsHandler
import matej.lamza.superbet.utils.location.LocationClient
import matej.lamza.superbet.utils.maps.ClusterManager
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


const val TAG = "MapsActivity"

class MapsActivity : BaseMapActivity() {

    private lateinit var mapBinding: ActivityMapsBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationClient by inject<LocationClient> { parametersOf(this, fusedLocationClient) }
    private val mapViewModel by viewModel<MapViewModel>()
    private val betshopClusterManager by inject<ClusterManager<Betshop>> { parametersOf(this, map, lifecycleScope) }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    lifecycleScope.launch {
                        locationClient.getCurrentLocation().catch { showGPSPromptDialog(it) }
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

        bottomSheetBehavior = BottomSheetBehavior.from(mapBinding.bottomSheet.bottomSheet)

        //This will make sure our map is initialized before observers can be triggered
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                initMap(R.id.map)
                clusterManager = betshopClusterManager.setupClusterManager()
                map.setOnCameraIdleListener(clusterManager)
                map.setOnMarkerClickListener(clusterManager)

                setupListeners()
                setObservers()
            }
        }
    }


    private fun setupListeners() {
        map.setOnCameraIdleListener {
            mapViewModel.processCameraMovement(map.projection.visibleRegion)
        }

        mapBinding.currentLocation.setOnClickListener {
            PermissionsHandler.requestPermission(this, this, requestPermissionLauncher)
        }

        clusterManager.markerCollection.setOnMarkerClickListener {
            betshopClusterManager.updateMarkerState(it)
            false
        }

        map.setOnMapClickListener {
            //remove marker when user clicks somewhere else on the map
            //todo maybe call manager to restart marker state
            betshopClusterManager.updateMarkerState(null)
        }
    }

    private suspend fun setObservers() {
        with(mapViewModel) {
            visibleBetshops.observe(this@MapsActivity) {
                if (clusterManager.algorithm.items.isNotEmpty()) clusterManager.clearItems()
                clusterManager.addItems(it)
                clusterManager.cluster()
            }
        }

        betshopClusterManager.markerStateFlow.collect {
            if (it is MapMarkerState.Active) Toast.makeText(this, "Displaying info...", Toast.LENGTH_SHORT).show()
            else if (it is MapMarkerState.Inactive) Toast.makeText(this, "Hiding ...", Toast.LENGTH_SHORT).show()
        }
    }
}
