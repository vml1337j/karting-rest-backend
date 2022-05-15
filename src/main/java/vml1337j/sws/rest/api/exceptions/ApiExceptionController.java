package vml1337j.sws.rest.api.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler({EventNotFoundException.class})
    public ResponseEntity<Object> handleEventNotFoundException(EventNotFoundException e) {
        log.error(e.getMessage());

        ApiErrorDto eventNotFound = new ApiErrorDto(
                e.getMessage(),
                Instant.now()
        );

        return new ResponseEntity<>(eventNotFound, HttpStatus.NOT_FOUND);
    }
}
