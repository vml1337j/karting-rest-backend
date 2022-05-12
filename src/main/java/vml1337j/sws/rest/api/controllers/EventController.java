package vml1337j.sws.rest.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vml1337j.sws.rest.api.dto.EventDto;
import vml1337j.sws.rest.api.dto.EventResultDto;
import vml1337j.sws.rest.api.mappers.EventMapper;
import vml1337j.sws.rest.api.services.EventService;
import vml1337j.sws.rest.store.entities.EventEntity;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EventController {

    public static final String FETCH_EVENTS = "/api/v1/events";
    public static final String GET_EVENT = "/api/v1/events/{event_id}";
    public static final String FETCH_EVENT_RESULTS = "/api/v1/events/{event_id}/results";

    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping(FETCH_EVENTS)
    public List<EventDto> fetchEvents() {
        return eventMapper.toEventDtoList(
                eventService.getEvents()
        );
    }

    @GetMapping(GET_EVENT)
    public EventDto getEventById(@PathVariable("event_id") Long eventId) {
        return eventMapper.toEventDto(
                eventService.getEventById(eventId)
        );
    }

    @GetMapping(FETCH_EVENT_RESULTS)
    public List<EventResultDto> fetchEventResultsById(@PathVariable("event_id") Long eventId) {
        return eventMapper.toEventResultDtoList(
                eventService.getEventResultsById(eventId)
        );
    }
}
