package matej.lamza.core_model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import matej.lamza.core_model.utils.DateUtils


data class Betshop(
    val name: String,
    val location: Location,
    val county: String,
    val city: String,
    val address: String,
) : ClusterItem {
    val schedule: String =
        if (DateUtils.isCurrentlyOpened()) "Open now until ${DateUtils.END_TIME}"
        else "Opens tomorrow at ${DateUtils.START_TIME}"

    private val position: LatLng = LatLng(location.latitude, location.longitude)
    private val title: String = name
    private val snippet: String = address

    override fun getPosition(): LatLng = position
    override fun getTitle(): String? = title
    override fun getSnippet(): String? = snippet
}
