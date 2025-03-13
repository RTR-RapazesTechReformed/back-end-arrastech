package techForAll.techPoints.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Recurso não encontrado",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Argumento inválido",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Erro Interno",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStateException(ex: IllegalStateException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorDetails = ErrorResponse(
            timestamp = LocalDateTime.now(),
            message = ex.message ?: "Estado ilegal detectado",
            details = request.getDescription(false)
        )
        return ResponseEntity(errorDetails, HttpStatus.CONFLICT)
    }

    data class ErrorResponse(
        val timestamp: LocalDateTime,
        val message: String,
        val details: String
    )
}