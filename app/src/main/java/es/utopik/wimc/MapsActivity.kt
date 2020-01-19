package es.utopik.wimc

/**
 * @created 18.01.2020
 */

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import es.utopik.jgsutils.alertMsg
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMapClickListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val LOG_TAG = "MapsActivity"
        private const val PLACE_PICKER_REQUEST = 3
        private const val ZOOM_INICIAL = 16f

        // EXTRAS
        const val EXTRA_LATITUD = "LATITUD"
        const val EXTRA_LONGITUD = "LONGITUD"

        // resultats tornats
        const val RESULT_ADDRESS = "ADDRESS"
        const val RESULT_LATITUD = "LATITUD"
        const val RESULT_LONGITUD = "LONGITUD"

    }

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var clickMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        //-----
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // → onMapReady
        //
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //---------------
        initUI()





//        -------------- init clickMarker
//        val markerOptions = MarkerOptions()
//        markerOptions.icon(
//            BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(
//                    resources,
//                    R.mipmap.ic_user_location
//                )
//            )
//        )
//
////        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        clickMarker = Marker(markerOptions)
//        map.addMarker()

        try {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        } catch (e: Exception) {
            alertMsg(e.message.toString(), "ERROR fusedLocationClient")
        }
    }

    fun init_processarExtras() {
        var bundle = intent.getExtras();
        if (bundle.containsKey(EXTRA_LONGITUD)) {
            var latitud = bundle.getFloat(MapsActivity.EXTRA_LATITUD)
            var longitud = bundle.getFloat(MapsActivity.EXTRA_LONGITUD)
        }
    }

    /**
     * 18.01.2020
     */
    fun initUI() {
        //mostram BACK button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    /**
     *
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MapsActivity.PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                var addressText = place.name.toString()
                addressText += "\n" + place.address.toString()

                placeMarkerOnMap(place.latLng)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        this.map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)
        map.setOnMapClickListener(this)

        setUpMap()


//        // Add a marker in Sydney and move the camera
////        val sydney = LatLng(-34.0, 151.0)
//        val inca = LatLng(39.721293, 2.909901)
//
//        var myLocation = inca
//
//        try {
//            map.addMarker(MarkerOptions().position(myLocation).title("Marker in INCA"))
//            map.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
//        } catch (e: Exception) {
//            alertMsg(e.message.toString(),"ERROR onMapReady")
//        }
//
//        map.getUiSettings().setZoomControlsEnabled(true)
////        map.setMinZoomPreference(4f)
//        val ZOOM = 12.0f
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,  ZOOM))
//
//        map.setOnMarkerClickListener(this)
//
//        //comprovam PERMISOS
//        this.setUpMap_checkPermissions()
    }


    /**
     *
     */
    override fun onMapClick(pos: LatLng?) {

        if (pos == null) return

        //borram marker previ
        clickMarker?.remove()

        //el tornam a crear
        create_clickMarker(pos)

//        //cream marker a sa nova posició
//        this.clickMarker = map.addMarker(MarkerOptions()
//            .position(pos)
//            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//        )
    }

    /**
     * @date 18.01.2020
     */
    override fun onMarkerClick(p0: Marker?): Boolean {
        this.showInfoPosition(p0?.position)
        return true
    }


    fun showInfoPosition(p0: LatLng?, address: String? = null) {

        if (p0 != null) {
            var s = ""

            var address2 = address ?: getAddress(p0)


            p0.let {
                //                s = "LAT: ${it.latitude} / "
//                s += "LONG: ${it.longitude}\n"
                s += "$address2\n"
            }

//            s = "LAT: ${pos.latitude}\n"
//            s += "LONG: ${pos.longitude}\n"
//            s += "getAddress: ${getAddress(pos)}\n"
            txtAddress.text = s
//            alertMsg(s, "showInfoPosition")
        }

    }

    /**
     * @return OK
     */
    private fun checkPermisos(): Boolean {
        var permis = android.Manifest.permission.ACCESS_FINE_LOCATION

        //miram si tenim PERMIS!
        if (ActivityCompat.checkSelfPermission(this, permis) != PackageManager.PERMISSION_GRANTED) {
            //sol·icitam permís
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permis),
                MapsActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return false
        } else {
            return true
        }

    }

    /**
     * 18.01.2020
     */
    private fun setUpMap() {

        if (!checkPermisos()) return

        // si hem arribat aquí, es que tenim permisos! JAJAJA

        //mostra icona de "myLocation", + marker de sa posició actual
        map.isMyLocationEnabled = true


        //Tipus de Mapa
        //        GoogleMap.MAP_TYPE_HYBRID
        //        GoogleMap.MAP_TYPE_NORMAL
        //        GoogleMap.MAP_TYPE_SATELLITE
        map.mapType = GoogleMap.MAP_TYPE_TERRAIN


        //miram si ens ha passat EXTRAS
        var bundle = intent.getExtras();
        if (bundle!=null && bundle.containsKey(EXTRA_LONGITUD)) {
            var latitud = bundle.getFloat(MapsActivity.EXTRA_LATITUD)
            var longitud = bundle.getFloat(MapsActivity.EXTRA_LONGITUD)

            var pos = LatLng(latitud.toDouble(), longitud.toDouble())
            placeMarkerOnMap(pos)
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, ZOOM_INICIAL))
        }
        else {
            //demanam sa darrera posició coneguda el dispositiu
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {

                    this.lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)

                    //col·locam un MARKER
                    placeMarkerOnMap(currentLatLng)

                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            currentLatLng,
                            ZOOM_INICIAL
                        )
                    )
                }
            }
        }
    }

    /**
     * 18.01.2020
     * This takes the coordinates of a location and returns a readable address and vice versa.
     */
    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]

                for (i in 0 until address.maxAddressLineIndex + 1) {
//                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                    var s = address.thoroughfare +", nº " + address.featureName + ", " + address.locality

                    addressText += if (i == 0) s  else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e(LOG_TAG, e.localizedMessage)
        }

        return addressText
    }

    /**
     * 18.01.2020
     */
    private fun placeMarkerOnMap(location: LatLng) {
        this.create_clickMarker(location)
//        // 1
//        val markerOptions = MarkerOptions().position(location)
//
//        val titleStr = getAddress(location)
//        markerOptions.title(titleStr)
//
//
//        //custom MARKER!
////        markerOptions.icon(
////            BitmapDescriptorFactory.fromBitmap(
////                BitmapFactory.decodeResource(
////                    resources,
////                    R.mipmap.ic_user_location
////                )
////            )
////        )
////
//        //canviam color del default MARKER
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//
//        //afegim Marker.
//        // i el guardam per despres borrar-ho en tornar a fer click!
//        this.clickMarker =  map.addMarker(markerOptions)
    }


    fun create_clickMarker(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)

        val address = getAddress(location)
        markerOptions.title(address)


        //custom MARKER!
//        markerOptions.icon(
//            BitmapDescriptorFactory.fromBitmap(
//                BitmapFactory.decodeResource(
//                    resources,
//                    R.mipmap.ic_user_location
//                )
//            )
//        )
//
        //canviam color del default MARKER
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        //afegim Marker.
        // i el guardam per despres borrar-ho en tornar a fer click!
        this.clickMarker = map.addMarker(markerOptions)

        //mostram info location
        this.showInfoPosition(location, address)

    }


    /**
     * 18.01.2020
     */
    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        try {
            startActivityForResult(builder.build(this@MapsActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            alertMsg(e.message.toString(), "loadPlacePicker")
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            alertMsg(e.message.toString(), "loadPlacePicker")
            e.printStackTrace()
        }
    }

    fun btnSearchOnClick(v: View) {
        loadPlacePicker()
    }

    /**
     * retornam valors!
     */
    fun btnOK_OnClick(view: View) {
        var intent = Intent()

        if (clickMarker == null) {
            alertMsg("Pulsa en el mapa para seleccionar la posición")
            return
        }

        if (clickMarker != null) {

            var pos = clickMarker?.position

            if (pos!=null){
                intent.putExtra(RESULT_ADDRESS, txtAddress.text)
                intent.putExtra(RESULT_LATITUD, pos.latitude.toDouble())
                intent.putExtra(RESULT_LONGITUD, pos.longitude.toDouble() )
            }

        }

/*

        clickMarker?.let {
            intent.putExtra(RESULT_ADDRESS, txtAddress.text)
            intent.putExtra(RESULT_LATITUD, it.position.latitude)
            intent.putExtra(RESULT_LONGITUD, it.position.longitude)
        }
*/

        //tornam valor
        setResult(Activity.RESULT_OK, intent)

        //tancam
        finish()
    }
}
