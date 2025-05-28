package com.persons.finder.domain.services

import com.persons.finder.common.exception.PersonNotFoundException
import com.persons.finder.data.Location
import com.persons.finder.presentation.dto.LocationWithDistance
import com.persons.finder.repository.LocationRepository
import com.persons.finder.repository.PersonRepository
import org.springframework.stereotype.Service
import java.lang.Math.atan2
import java.lang.Math.cos
import java.lang.Math.sin
import java.lang.Math.sqrt
import kotlin.math.pow

@Service
class LocationsServiceImpl(private val locationRepository: LocationRepository, private val personRepository: PersonRepository) : LocationsService {

    override fun addLocation(location: Location): Location {
        val person = personRepository.findById(location.personId)
        if (person.isEmpty) throw PersonNotFoundException("No persons found for given ID")
        return locationRepository.save(location)
    }

    override fun removeLocation(locationReferenceId: Long) {
        TODO("Not yet implemented")
    }

    override fun findAround(lat: Double, lon: Double, radiusKm: Double): List<LocationWithDistance> {
        val (minLat, maxLat) = latRange(lat, radiusKm)
        val (minLon, maxLon) = lonRange(lat, lon, radiusKm)

        val locations = locationRepository.findWithinBox(minLat, maxLat, minLon, maxLon)

        return locations.map {
            val distance = haversine(lat, lon, it.latitude, it.longitude)
            LocationWithDistance(it, distance)
        }.filter { it.distanceKm <= radiusKm }
            .sortedBy { it.distanceKm }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }

    private fun latRange(lat: Double, radiusKm: Double): Pair<Double, Double> {
        val delta = radiusKm / 111.0 // ~111 km per degree latitude
        return lat - delta to lat + delta
    }

    private fun lonRange(lat: Double, lon: Double, radiusKm: Double): Pair<Double, Double> {
        val delta = radiusKm / (111.320 * cos(Math.toRadians(lat)))
        return lon - delta to lon + delta
    }
}
