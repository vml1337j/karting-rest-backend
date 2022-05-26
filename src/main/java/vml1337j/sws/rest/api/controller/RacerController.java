package vml1337j.sws.rest.api.controller;

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
import vml1337j.sws.rest.api.assembler.RacerAssembler;
import vml1337j.sws.rest.api.dto.RacerDto;
import vml1337j.sws.rest.api.dto.RacerResultDto;
import vml1337j.sws.rest.api.mapper.RacerMapper;
import vml1337j.sws.rest.api.service.RacerService;
import vml1337j.sws.rest.store.entities.RacerEntity;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
public class RacerController {

    public static final String GET_RACERS = "/api/v1/racers";
    public static final String GET_RACER = "/api/v1/racers/{racer_id}";
    public static final String GET_RACER_EVENTS = "/api/v1/racers/{racer_id}/results";

    private final RacerService racerService;
    private final RacerAssembler racerAssembler;
    private final PagedResourcesAssembler<RacerEntity> pagedResourcesAssembler;
    private final RacerMapper racerMapper;

    @GetMapping(GET_RACERS)
    public PagedModel<EntityModel<RacerDto>> getAllRacers(@PageableDefault Pageable pageable) {
        return pagedResourcesAssembler.toModel(racerService.getRacers(pageable), racerAssembler);
    }

    @GetMapping(GET_RACER)
    public EntityModel<RacerDto> getRacerById(@PathVariable("racer_id") Long racerId) {
        return racerAssembler.toModel(racerService.getRacerById(racerId))
                .add(linkTo(methodOn(RacerController.class).getAllRacers(Pageable.unpaged())).withRel("racers"));
    }

    @GetMapping(GET_RACER_EVENTS)
    public CollectionModel<EntityModel<RacerResultDto>> getRacerResultsById(@PathVariable("racer_id") Long racerId) {
        List<EntityModel<RacerResultDto>> results = racerMapper.toRacerResultDtoList(racerService.getRacerResults(racerId))
                .stream()
                .map(result -> EntityModel.of(result,
                        linkTo(methodOn(EventController.class).getEventById(result.getEventId())).withRel("event")
                ))
                .collect(Collectors.toList());

        return CollectionModel.of(results,
                linkTo(methodOn(RacerController.class).getRacerResultsById(racerId)).withSelfRel(),
                linkTo(methodOn(RacerController.class).getRacerById(racerId)).withRel("racer"),
                linkTo(methodOn(RacerController.class).getAllRacers(Pageable.unpaged())).withRel("racers")
        );
    }
}
