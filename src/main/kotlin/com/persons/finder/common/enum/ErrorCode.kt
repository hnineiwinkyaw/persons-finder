package com.persons.finder.common.enum

import org.springframework.http.HttpStatus

enum class ErrorCode(val code: String, val httpStatus: HttpStatus) {
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", HttpStatus.NOT_FOUND),
    INVALID_INPUT("INVALID_INPUT", HttpStatus.BAD_REQUEST),
}
