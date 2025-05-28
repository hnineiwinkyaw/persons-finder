package com.persons.finder.presentation
import com.fasterxml.jackson.databind.ObjectMapper
import com.persons.finder.data.Person
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.repository.PersonRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class PersonControllerTest {

    @SpringBootTest
    @AutoConfigureMockMvc
    class PersonControllerIntegrationTest {

        @Autowired
        private lateinit var mockMvc: MockMvc

        @Autowired
        private lateinit var objectMapper: ObjectMapper

        @Autowired
        lateinit var personRepository: PersonRepository

        @BeforeEach
        fun setup() {
            personRepository.deleteAll()

            val person1 = Person(id = 1L, name = "Hnin")
            val person2 = Person(id = 2L, name = "Win")

            personRepository.saveAll(listOf(person1, person2))
        }

        @Test
        fun `POST create person returns created person and status 201`() {
            val createRequest = CreatePersonRequest(name = "New Person")

            mockMvc.post("/api/v1/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }
                .andExpect {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.data.name") { value("New Person") }
                    jsonPath("$.data.id") { exists() }
                    jsonPath("$.transactionId") { exists() }
                }
        }

        @Test
        fun `POST create person with blank name returns 400 and validation error`() {
            val createRequest = CreatePersonRequest(name = "")

            mockMvc.post("/api/v1/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }
                .andExpect {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.error.code") { value("VALIDATION_ERROR") }
                    jsonPath("$.error.message") { value("Name must not be blank") }
                    jsonPath("$.transactionId") { exists() }
                }
        }

        @Test
        fun `POST create person with invalid characters returns 400 and validation error`() {
            val createRequest = CreatePersonRequest(name = "'; DROP TABLE person; --")

            mockMvc.post("/api/v1/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }
                .andExpect {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.error.code") { value("VALIDATION_ERROR") }
                    jsonPath("$.error.message") { value("Name contains invalid characters") }
                    jsonPath("$.transactionId") { exists() }
                }
        }

        @Test
        fun `POST create person with name too long returns 400 and validation error`() {
            val longName = "A".repeat(101)
            val createRequest = CreatePersonRequest(name = longName)

            mockMvc.post("/api/v1/persons") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(createRequest)
            }
                .andExpect {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.error.code") { value("VALIDATION_ERROR") }
                    jsonPath("$.error.message") { value("Name must be at most 100 characters") }
                    jsonPath("$.transactionId") { exists() }
                }
        }

        @Test
        fun `should return 404 when person not found`() {
            mockMvc.get("/api/v1/persons") {
                param("id", "1000")
                accept(MediaType.APPLICATION_JSON)
            }
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.error.code") { value("ENTITY_NOT_FOUND") }
                    jsonPath("$.error.message") { value("No persons found for given IDs") }
                }
        }

        @Test
        fun `should return 400 when user enter invalid input`() {
            mockMvc.get("/api/v1/persons") {
                param("id", "Hnin")
                accept(MediaType.APPLICATION_JSON)
            }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.error.code") { value("INVALID_INPUT") }
                    jsonPath("$.error.message") { value("Invalid value for parameter 'id'") }
                }
        }

        @Test
        fun `should return 200 and person list when valid ids provided`() {
            val id1 = 1L
            val id2 = 2L

            mockMvc.get("/api/v1/persons") {
                param("id", id1.toString())
                param("id", id2.toString())
                accept(MediaType.APPLICATION_JSON)
            }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.length()") { value(2) }
                    jsonPath("$.data[0].id") { value(id1) }
                    jsonPath("$.data[0].name") { value("Hnin") }
                    jsonPath("$.data[1].id") { value(id2) }
                    jsonPath("$.data[1].name") { value("Win") }
                }
        }
    }
}
