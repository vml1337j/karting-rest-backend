package vml1337j.sws.rest.api.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class ApiErrorDto {
    private String msg;
    private Instant timestamp;
}
