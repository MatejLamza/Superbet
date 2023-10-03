package matej.lamza.core_model

import com.google.android.gms.maps.model.Marker

sealed class MapMarkerState(val iconResourceId: Int, open val marker: Marker? = null) {

    data class Active(override val marker: Marker? = null) :
        MapMarkerState(iconResourceId = R.mipmap.ic_pin_active, marker)

    data class Inactive(override val marker: Marker? = null) :
        MapMarkerState(iconResourceId = R.mipmap.ic_pin_normal, marker)
}
