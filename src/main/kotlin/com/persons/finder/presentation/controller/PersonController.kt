package com.persons.finder.presentation.controller

import com.persons.finder.common.exception.PersonNotFoundException
import com.persons.finder.data.Location
import com.persons.finder.data.Person
import com.persons.finder.domain.services.LocationsService
import com.persons.finder.domain.services.PersonsService
import com.persons.finder.presentation.api.PersonApi
import com.persons.finder.presentation.dto.AddLocationRequest
import com.persons.finder.presentation.dto.AddLocationResponse
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.presentation.dto.CreatePersonResponse
import com.persons.finder.presentation.dto.FindNearbyPersonsResponse
import com.persons.finder.presentation.dto.GetPersonResponse
import com.persons.finder.seed.DbSeeder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PersonController(private val personService: PersonsService, private val locationService: LocationsService, private val seeder: DbSeeder) : PersonApi {

    override fun createPerson(
        @RequestBody
        request: CreatePersonRequest,
    ): ResponseEntity<CreatePersonResponse> {
        val person = Person(name = request.name)
        val savedPerson = personService.save(person)
        val response = CreatePersonResponse(data = savedPerson)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    override fun getPersonsById(ids: List<Long>): ResponseEntity<GetPersonResponse> {
        val persons = personService.getByIds(ids)
        if (persons.isEmpty()) throw PersonNotFoundException("No persons found for given IDs")
        return ResponseEntity.ok(GetPersonResponse(data = persons))
    }

    override fun addLocation(id: Long, request: AddLocationRequest): ResponseEntity<AddLocationResponse> {
        val location = locationService.addLocation(Location(latitude = request.latitude!!, longitude = request.longitude!!, personId = id))
        return ResponseEntity.ok(AddLocationResponse(data = location))
    }

    override fun findNearbyPersons(lat: Double, lon: Double, radiusKm: Double): ResponseEntity<FindNearbyPersonsResponse> {
        val nearby = locationService.findAround(lat, lon, radiusKm)
        return ResponseEntity.ok(FindNearbyPersonsResponse(data = nearby))
    }

    override fun seedData(size: Long): ResponseEntity<Map<String, String>> {
        seeder.seedData(size)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "Seeded with $size records."))
    }
}
