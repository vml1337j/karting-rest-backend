package vml1337j.sws.rest.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RacerNotFoundException extends RuntimeException {
    public RacerNotFoundException(String message) {
        super(message);
    }
}
