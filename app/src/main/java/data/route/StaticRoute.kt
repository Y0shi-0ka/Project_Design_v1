package com.example.project_design.data.route

import com.google.android.gms.maps.model.LatLng

data class StaticRoute(
    val id: String,
    val name: String,
    val points: List<LatLng>
)
