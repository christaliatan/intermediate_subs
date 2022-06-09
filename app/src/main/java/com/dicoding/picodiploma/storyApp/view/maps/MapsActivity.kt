package com.dicoding.picodiploma.storyApp.view.maps

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.storyApp.R
import com.dicoding.picodiploma.storyApp.api.ApiConfig
import com.dicoding.picodiploma.storyApp.api.GetAllStoriesResponse
import com.dicoding.picodiploma.storyApp.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.storyApp.model.UserPreference
import com.dicoding.picodiploma.storyApp.view.ViewModelFactory
import com.dicoding.picodiploma.storyApp.view.login.LoginViewModel
import com.dicoding.picodiploma.storyApp.view.story.ListStory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var loginViewModel: LoginViewModel
    private var listStory: List<ListStory>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[LoginViewModel::class.java]
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        loginViewModel.getToken().observe(this) { token ->
            ApiConfig.instances.getAllStories("Bearer $token").enqueue(object :
                Callback<GetAllStoriesResponse> {
                override fun onResponse(
                    call: Call<GetAllStoriesResponse>,
                    response: Response<GetAllStoriesResponse>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            listStory = result.listStory
                            if (listStory != null) {
                                listStory?.forEach {
                                    val lat = it.lat
                                    val lon = it.lon
                                    if (lat != null && lon != null) {
                                        val position = LatLng(lat, lon)
                                        mMap.addMarker(
                                            MarkerOptions().position(position).title(it.name)
                                        )
                                    }
                                }
                            } else {
                                Toast.makeText(this@MapsActivity, "No story", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(
                                this@MapsActivity,
                                response.message(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllStoriesResponse>, t: Throwable) {
                    Toast.makeText(this@MapsActivity, t.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
