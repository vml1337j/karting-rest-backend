package vml1337j.sws.rest.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vml1337j.sws.rest.api.dto.RacerDto;
import vml1337j.sws.rest.api.dto.RacerResultDto;
import vml1337j.sws.rest.api.mappers.RacerMapper;
import vml1337j.sws.rest.api.services.RacerService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class RacerController {

    public static final String FETCH_RACERS = "/api/v1/racers";
    public static final String GET_RACER = "/api/v1/racers/{racer_id}";
    public static final String FETCH_RACER_EVENTS = "/api/v1/racers/{racer_id}/results";

    private final RacerService racerService;
    private final RacerMapper racerMapper;

    @GetMapping(FETCH_RACERS)
    public List<RacerDto> fetchRacers() {
        return racerMapper.toRacerDtoList(
                racerService.getRacers()
        );
    }

    @GetMapping(GET_RACER)
    public RacerDto getRacerById(@PathVariable("racer_id") Long racerId) {
        return racerMapper.toRacerDto(
                racerService.getRacerById(racerId)
        );
    }

    @GetMapping(FETCH_RACER_EVENTS)
    public List<RacerResultDto> fetchRacerEventsById(@PathVariable("racer_id") Long racerId) {
        return racerMapper.toRacerResultDtoList(
                racerService.getRacerResults(racerId)
        );
    }
}
