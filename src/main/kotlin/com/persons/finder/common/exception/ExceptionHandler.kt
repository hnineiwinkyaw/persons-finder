package com.persons.finder.common.exception

import com.persons.finder.common.dto.ErrorBody
import com.persons.finder.common.dto.ErrorResponse
import com.persons.finder.common.enum.ErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler
    fun handleValidationExceptions(
        ex: java.lang.Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val (errorCode, message) = when (ex) {
            is MethodArgumentNotValidException ->
                ErrorCode.VALIDATION_ERROR to (ex.bindingResult.allErrors.firstOrNull()?.defaultMessage ?: "Validation error")
            is MissingServletRequestParameterException ->
                ErrorCode.INVALID_INPUT to "Missing required parameter: ${ex.parameterName}"
            is PersonNotFoundException ->
                ErrorCode.ENTITY_NOT_FOUND to (ex.message ?: "No persons found")
            is MethodArgumentTypeMismatchException ->
                ErrorCode.INVALID_INPUT to "Invalid value for parameter '${ex.name}'"
            is HttpMessageNotReadableException ->
                ErrorCode.INVALID_INPUT to "Invalid Request Body"
            else ->
                ErrorCode.VALIDATION_ERROR to "Invalid request body or parameters"
        }

        val errorResponse = ErrorResponse(
            error = ErrorBody(
                code = errorCode.code,
                message = message,
            ),
        )
        return ResponseEntity.status(errorCode.httpStatus).body(errorResponse)
    }
}
