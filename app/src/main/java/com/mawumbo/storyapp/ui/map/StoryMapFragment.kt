package com.mawumbo.storyapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.mawumbo.storyapp.R
import com.mawumbo.storyapp.databinding.FragmentStoryMapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryMapFragment : Fragment() {

    private var _binding: FragmentStoryMapBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StoryMapViewModel by viewModels()

    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.story_map) as SupportMapFragment

        viewModel.storiesWithLocation.observe(viewLifecycleOwner) { storiesWithLocation ->
            val callback = OnMapReadyCallback { googleMap ->
                storiesWithLocation.forEach { story ->
                    val latLng = LatLng(story.lat, story.lon)
                    googleMap.addMarker(MarkerOptions().position(latLng).title(story.name))
                    boundsBuilder.include(latLng)
                }

                val bounds: LatLngBounds = boundsBuilder.build()
                googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        100
                    )
                )
            }

            mapFragment.getMapAsync(callback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}