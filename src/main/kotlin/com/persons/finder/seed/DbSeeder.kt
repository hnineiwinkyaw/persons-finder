package com.persons.finder.seed

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class DbSeeder(private val jdbcTemplate: JdbcTemplate) {

    fun getMaxPersonId(): Long {
        return jdbcTemplate.queryForObject("SELECT COALESCE(MAX(id), 0) FROM person", Long::class.java) ?: 0L
    }

    fun batchInsertPersonsWithId(startId: Long, count: Int) {
        val sql = "INSERT INTO person (id, name) VALUES (?, ?)"
        val batchArgs = (startId until startId + count).map { arrayOf(it, "Person $it") }
        jdbcTemplate.batchUpdate(sql, batchArgs)
    }

    fun batchInsertLocations(startPersonId: Long, count: Int) {
        val sql = "INSERT INTO location (person_id, latitude, longitude) VALUES (?, ?, ?)"
        val batchArgs = (startPersonId until startPersonId + count).map {
            arrayOf(it, Random.nextDouble(-90.0, 90.0), Random.nextDouble(-180.0, 180.0))
        }
        jdbcTemplate.batchUpdate(sql, batchArgs)
    }

    fun seedData(total: Long, batchSize: Int = 10_000) {
        var currentId = getMaxPersonId() + 1
        var remaining = total

        while (remaining > 0) {
            val count = if (remaining < batchSize) remaining.toInt() else batchSize

            batchInsertPersonsWithId(currentId, count)
            batchInsertLocations(currentId, count)

            currentId += count
            remaining -= count
        }
    }
}
