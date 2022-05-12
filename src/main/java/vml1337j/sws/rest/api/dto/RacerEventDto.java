package vml1337j.sws.rest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RacerEventDto {
    private int position;
    private long eventId;
    private String title;
    private LocalDate date;
}
