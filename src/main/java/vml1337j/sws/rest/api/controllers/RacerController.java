package vml1337j.sws.rest.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vml1337j.sws.rest.api.dto.RacerDto;
import vml1337j.sws.rest.api.dto.RacerEventDto;

import java.util.List;

@RestController
public class RacerController {

    public static final String FETCH_RACERS = "/api/v1/racers";
    public static final String GET_RACER = "/api/v1/racers/{racer_id}";
    public static final String FETCH_RACER_EVENTS = "/api/v1/racers/{racer_id}/events";

    @GetMapping(FETCH_RACERS)
    public List<RacerDto> fetchRacers() {
        return List.of();
    }

    @GetMapping(GET_RACER)
    public RacerDto getRacerById(@PathVariable("racer_id") Long racerId) {
        return RacerDto.builder().build();
    }

    @GetMapping(FETCH_RACER_EVENTS)
    public List<RacerEventDto> fetchRacerEventsById(@PathVariable("racer_id") Long racerId) {
        return List.of();
    }
}
