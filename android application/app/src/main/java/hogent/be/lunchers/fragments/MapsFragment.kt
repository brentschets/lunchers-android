package hogent.be.lunchers.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import hogent.be.lunchers.R
import hogent.be.lunchers.models.Lunch
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import hogent.be.lunchers.viewmodels.LunchViewModel
import java.io.InputStream


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    /**
     * [MeetingViewModel] met de data van alle meetings
     */
    //Globaal ter beschikking gesteld aangezien het mogeiljks later nog in andere functie dan onCreateView wenst te worden
    private lateinit var lunchViewModel: LunchViewModel

    // Lateinit variabelen zijn standaard null, normaal mag dit niet mag in Kotlin
    // Er wordt echter vanuit gegaan dat ze in OnStart of OnResume of ... geinitialiseerd worden
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_google_maps, container, false)

        // Een fragment voor de Google map
        val mapFragment = (childFragmentManager.findFragmentById(R.id.google_map)) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //viewmodel vullen
        lunchViewModel = ViewModelProviders.of(requireActivity()).get(LunchViewModel::class.java)
        lunchViewModel.resetFilteredLunches()
        // Een variabele voor het gebruiken van de locatie van de gebruiker
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        return rootView
    }

    // Functie die wordt opgeroepen nadat de map geladen is
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
    }

    override fun onMarkerClick(marker: Marker?) = false

    // Deze methode wordt opgeroepen nadat de app de gebruiker gevraagd heeft om de locatie permissie te geven
    // Indien de gebruiker permissie tot zijn/haar locatie heeft gegeven, wordt de methode setUpMap uitgevoerd
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    setUpMap()
                }
                return
            }
        }
    }

    // Deze methode plaatst markers op de map via de meegegeven latitude en longitude
    // Als je er op klikt verschijnt de naam en kan je ook via Google Maps navigatie starten
    private fun placeMarkerOnMap(lat: Double, lng: Double, naam: String, beschrijving: String, imgUrl: String) {

        map.addMarker(
            MarkerOptions().position(LatLng(lat, lng))
                .title(naam)
                .snippet(beschrijving)
        )
    }

    private fun getBitmapFromURL(src: String): Bitmap? {
        var bmp: Bitmap? = null
        try {
            val input: InputStream = java.net.URL(src).openStream()
            bmp = BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            Log.e("Error", e.message)
            e.printStackTrace()
        }
        return bmp
    }

    // Functie die de locatie van de gebruiker ophaalt en de camera naar deze locatie laat gaan
    private fun setUpMap() {
        // De gebruiker toestemming vragen voor zijn/haar locatie te gebruiken
        // Momenteel is het nog zo dat na het toestemming geven de app nog eens opnieuw opgestart moet worden
        if (checkSelfPermission(
                this.requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this.requireActivity()) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
            }
        }

        retrieveAllLunches()
    }

    // Deze methode haalt alle lunches op en plaatst van iedere lunch de handelaar op de kaart
    private fun retrieveAllLunches() {
        val list = lunchViewModel.getFilteredLunches()


        list.observe(this, Observer {
            putMarkersOnMap(list.value!!)
        })
    }

    private fun putMarkersOnMap(lunches: List<Lunch>) {
        if (lunches != null) {
            lunches.forEach {
                placeMarkerOnMap(
                    it.handelaar.locatie.latitude,
                    it.handelaar.locatie.longitude,
                    it.handelaar.handelsNaam,
                    it.beschrijving,
                    it.afbeeldingen[0].pad
                )
            }
        }
    }
    
    // Een companion object kan je zien als een statische variabele
    // In dit geval is het de request code die we proberen terug te krijgen
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}