package matej.lamza.superbet.utils.extensions

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import matej.lamza.core_model.MapMarkerState


fun Marker.setStateIcon(iconResId: Int) {
    setIcon(BitmapDescriptorFactory.fromResource(iconResId))
}

fun MapMarkerState.setStateIcon() {
    marker?.setStateIcon(iconResourceId)
}
