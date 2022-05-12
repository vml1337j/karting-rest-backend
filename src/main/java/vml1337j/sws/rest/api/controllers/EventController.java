package vml1337j.sws.rest.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vml1337j.sws.rest.api.dto.EventDto;
import vml1337j.sws.rest.api.dto.EventResultDto;

import java.util.List;

@RestController
public class EventController {

    public static final String FETCH_EVENTS = "/api/v1/events";
    public static final String GET_EVENT = "/api/v1/events/{event_id}";
    public static final String FETCH_EVENT_RESULTS = "/api/v1/events/{event_id}/results";

    @GetMapping(FETCH_EVENTS)
    public List<EventDto> fetchEvents() {
        return List.of();
    }

    @GetMapping(GET_EVENT)
    public EventDto getEventById(@PathVariable("event_id") Long eventId) {
        return EventDto.builder().build();
    }

    @GetMapping(FETCH_EVENT_RESULTS)
    public List<EventResultDto> fetchEventResultsById(@PathVariable("event_id") Long eventId) {
        return List.of();
    }
}
