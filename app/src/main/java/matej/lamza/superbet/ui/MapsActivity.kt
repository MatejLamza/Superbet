package matej.lamza.superbet.ui

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.MapMarkerState
import matej.lamza.superbet.R
import matej.lamza.superbet.databinding.ActivityMapsBinding
import matej.lamza.superbet.utils.DateUtils
import matej.lamza.superbet.utils.PermissionsHandler
import matej.lamza.superbet.utils.location.LocationClient
import matej.lamza.superbet.utils.maps.ClusterManagerService
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


const val TAG = "MapsActivity"

class MapsActivity : AppCompatActivity() {
    lateinit var clusterManager: ClusterManager<Betshop>
    lateinit var map: GoogleMap

    companion object {
        const val REQUEST_CHECK_SETTINGS = 90
    }


    private lateinit var mapBinding: ActivityMapsBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationClient by inject<LocationClient> { parametersOf(this, fusedLocationClient) }
    private val mapViewModel by viewModel<MapViewModel>()
    private val betshopClusterManager by inject<ClusterManagerService<Betshop>> {
        parametersOf(
            this,
            map,
            lifecycleScope
        )
    }

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

    @SuppressLint("PotentialBehaviorOverride")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Base_Theme_Superbet)

        mapBinding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(mapBinding.root)

        bottomSheetBehavior = BottomSheetBehavior.from(mapBinding.bottomSheet.bottomSheet)

        //This will make sure our map is initialized before observers can be triggered
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                val mapFragment: SupportMapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

                map = mapFragment.awaitMap()

                clusterManager = betshopClusterManager.setupClusterManager()

                setupListeners()
                setObservers()
            }
        }
    }

    private fun setupListeners() {
        map.setOnCameraIdleListener {
            Log.d("bbb", "setupListeners: kamera stala")
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
            if (it is MapMarkerState.Active) setBottomSheetVisibility(true)
            else if (it is MapMarkerState.Inactive) setBottomSheetVisibility(false)
        }
    }

    private fun bindBetshopInfoToView(betshop: Betshop) {
        with(mapBinding.bottomSheet) {
            location.text = betshop.title
            phone.text = betshop.snippet
            schedule.text =
                if (DateUtils.isCurrentlyOpened()) getString(R.string.betshop_open_now, DateUtils.END_TIME.toString())
                else getString(R.string.betshop_closed, DateUtils.START_TIME.toString())
        }
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        if (isVisible) mapBinding.bottomSheet.bottomSheet.visibility = View.VISIBLE
        val updatedState = if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    private fun showGPSPromptDialog(throwable: Throwable) {
        if (throwable is ResolvableApiException)
            kotlin.runCatching { throwable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS) }
    }
}
