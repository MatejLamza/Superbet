package matej.lamza.superbet.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import com.skydoves.bindables.BindingActivity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.MapMarkerState
import matej.lamza.superbet.R
import matej.lamza.superbet.databinding.ActivityMapsBinding
import matej.lamza.superbet.utils.PermissionsHandler
import matej.lamza.superbet.utils.extensions.infoSnackBar
import matej.lamza.superbet.utils.extensions.toLatLng
import matej.lamza.superbet.utils.location.LocationClient
import matej.lamza.superbet.utils.maps.ClusterManagerService
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


const val TAG = "MapsActivity"

class MapsActivity : BindingActivity<ActivityMapsBinding>(R.layout.activity_maps) {

    companion object {
        const val REQUEST_CHECK_SETTINGS = 90
        val MUNICH_LOCATION = LatLng(48.137154, 11.576124)
    }

    private lateinit var clusterManager: ClusterManager<Betshop>
    private lateinit var map: GoogleMap
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationClient by inject<LocationClient> { parametersOf(this, fusedLocationClient) }
    private val mapViewModel by viewModel<MapViewModel>()
    private val betshopClusterManager by inject<ClusterManagerService<Betshop>> {
        parametersOf(
            this, map, lifecycleScope
        )
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    handleCurrentLocation()
                }

                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    handleCurrentLocation()
                }

                else -> {
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(MUNICH_LOCATION, 16f))
                    infoSnackBar(binding.root, getString(R.string.location_not_granted_message))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_Superbet)
        binding {
            vm = mapViewModel
            bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

            //This will make sure our map is initialized before observers can be triggered
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    val mapFragment: SupportMapFragment =
                        supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                    map = mapFragment.awaitMap()

                    clusterManager = betshopClusterManager.setupClusterManager()

                    PermissionsHandler.requestPermission(this@MapsActivity, binding.root, requestPermissionLauncher)

                    setupListeners()
                    setObservers()
                }
            }
        }
    }

    private fun setupListeners() {
        map.setOnCameraIdleListener { mapViewModel.processCameraMovement(map.projection.visibleRegion) }

        map.setOnMapClickListener {
            //remove marker when user clicks somewhere else on the map
            //todo maybe call manager to restart marker state
            betshopClusterManager.updateMarkerState(null)
        }

        clusterManager.markerCollection.setOnMarkerClickListener { marker ->
            betshopClusterManager.updateCurrentlySelectedBetshop(clusterManager, marker)
            betshopClusterManager.updateMarkerState(marker)
            true
        }
    }

    private fun setObservers() {
        with(mapViewModel) {
            visibleBetshops.distinctUntilChanged().observe(this@MapsActivity) {
                betshopClusterManager.createCluster(clusterManager, it)
            }
        }

        lifecycleScope.launch {
            betshopClusterManager.selectedBetshop.collect { selectedBetshop ->
                mapViewModel.updateSelectedBetshop(selectedBetshop)
            }
        }

        lifecycleScope.launch {
            betshopClusterManager.markerStateFlow.collect {
                handleBottomSheetInfoVisibility(it)
            }
        }
    }

    private fun handleBottomSheetInfoVisibility(markerState: MapMarkerState) {
        binding.bottomSheet.visibility = View.VISIBLE
        bottomSheetBehavior.state = when (markerState) {
            is MapMarkerState.Active -> BottomSheetBehavior.STATE_EXPANDED
            is MapMarkerState.Inactive -> BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showGPSPromptDialog(throwable: Throwable) {
        if (throwable is ResolvableApiException) runCatching {
            throwable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
        }
    }

    private fun handleCurrentLocation() {
        lifecycleScope.launch {
            locationClient.getCurrentLocation().catch { showGPSPromptDialog(it) }.collectLatest {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.toLatLng, 16f))
                Log.d(TAG, "Received current location: $it ")
            }
        }
    }
}
