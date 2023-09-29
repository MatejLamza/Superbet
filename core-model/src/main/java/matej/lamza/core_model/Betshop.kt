package matej.lamza.core_model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem


data class Betshop(
    val name: String,
    val location: Location,
    val county: String,
    val city: String,
    val address: String,
) : ClusterItem {

    private val position: LatLng = LatLng(location.latitude, location.longitude)
    private val title: String = name
    private val snippet: String = address

    override fun getPosition(): LatLng = position
    override fun getTitle(): String? = title
    override fun getSnippet(): String? = snippet
}
