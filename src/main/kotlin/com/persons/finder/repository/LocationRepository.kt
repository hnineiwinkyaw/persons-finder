package com.persons.finder.repository

import com.persons.finder.data.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface LocationRepository : JpaRepository<Location, Long> {
    @Query(
        """
        SELECT l FROM Location l
        WHERE l.latitude BETWEEN :minLat AND :maxLat
          AND l.longitude BETWEEN :minLon AND :maxLon
        """,
    )
    fun findWithinBox(
        @Param("minLat") minLat: Double,
        @Param("maxLat") maxLat: Double,
        @Param("minLon") minLon: Double,
        @Param("maxLon") maxLon: Double,
    ): List<Location>
}
