package vml1337j.sws.rest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventResultDto {
    private int position;
    private long racerId;
    private String firstname;
    private String lastname;
    private String gender;
    private String country;
}
