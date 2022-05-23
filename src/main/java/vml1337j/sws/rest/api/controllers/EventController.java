package vml1337j.sws.rest.api.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vml1337j.sws.rest.api.assembler.EventAssembler;
import vml1337j.sws.rest.api.dto.EventDto;
import vml1337j.sws.rest.api.dto.EventResultDto;
import vml1337j.sws.rest.api.mappers.EventMapper;
import vml1337j.sws.rest.api.services.EventService;
import vml1337j.sws.rest.store.entities.EventEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
public class EventController {

    public static final String FETCH_EVENTS = "/api/v1/events";
    public static final String GET_EVENT = "/api/v1/events/{event_id}";
    public static final String FETCH_EVENT_RESULTS = "/api/v1/events/{event_id}/results";

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final PagedResourcesAssembler<EventEntity> pagedResourcesAssembler;
    private final EventAssembler eventAssembler;

    @GetMapping(FETCH_EVENTS)
    public PagedModel<EntityModel<EventDto>> fetchEvents(@PageableDefault Pageable pageable) {
        return pagedResourcesAssembler.toModel(eventService.getEvents(pageable), eventAssembler);
    }

    @GetMapping(GET_EVENT)
    public EntityModel<EventDto> getEventById(@PathVariable("event_id") Long eventId) {
        return eventAssembler.toModel(eventService.getEventById(eventId))
                .add(linkTo(methodOn(EventController.class).fetchEvents(Pageable.unpaged())).withRel("events"));
    }

    @GetMapping(FETCH_EVENT_RESULTS)
    public CollectionModel<EntityModel<EventResultDto>> fetchEventResultsById(@PathVariable("event_id") Long eventId) {
        List<EntityModel<EventResultDto>> results = eventMapper.toEventResultDtoList(
                        eventService.getEventResultsById(eventId)
                ).stream()
                .map(result -> EntityModel.of(result,
                        linkTo(methodOn(RacerController.class).getRacerById(result.getRacerId())).withRel("racer")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(results,
                linkTo(methodOn(EventController.class).fetchEventResultsById(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventById(eventId)).withRel("event"),
                linkTo(methodOn(EventController.class).fetchEvents(Pageable.unpaged())).withRel("events")
        );
    }
}
