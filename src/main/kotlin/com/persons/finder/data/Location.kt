package com.persons.finder.data

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

@Entity
@Table(
    name = "location",
    indexes = [
        Index(name = "idx_location_lat_lon", columnList = "latitude, longitude"),
    ],
)
data class Location(
    @Id
    val personId: Long = 0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)
