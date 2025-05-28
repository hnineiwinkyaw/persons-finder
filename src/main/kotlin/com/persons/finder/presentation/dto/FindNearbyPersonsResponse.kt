package com.persons.finder.presentation.dto

import com.persons.finder.common.dto.TransactionResponse
import com.persons.finder.data.Location

class FindNearbyPersonsResponse(data: List<LocationWithDistance>) : TransactionResponse<List<LocationWithDistance>>(data)

data class LocationWithDistance(
    val location: Location,
    val distanceKm: Double,
)
