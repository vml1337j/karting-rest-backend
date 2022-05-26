package vml1337j.sws.rest.api.assembler;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import vml1337j.sws.rest.api.controller.RacerController;
import vml1337j.sws.rest.api.dto.RacerDto;
import vml1337j.sws.rest.api.mapper.RacerMapper;
import vml1337j.sws.rest.store.entities.RacerEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class RacerAssembler implements RepresentationModelAssembler<RacerEntity, EntityModel<RacerDto>> {

    private final RacerMapper mapper;

    @Override
    public EntityModel<RacerDto> toModel(RacerEntity entity) {
        return EntityModel.of(mapper.toRacerDto(entity),
                linkTo(methodOn(RacerController.class).getRacerById(entity.getId())).withSelfRel(),
                linkTo(methodOn(RacerController.class).getRacerResultsById(entity.getId())).withRel("results")
        );
    }
}
