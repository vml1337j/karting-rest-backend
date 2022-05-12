package vml1337j.sws.rest.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RacerDto {
    private long id;
    private String swsId;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String country;
    private long points;
}
