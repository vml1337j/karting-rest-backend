package vml1337j.sws.rest.api.assembler;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import vml1337j.sws.rest.api.controller.EventController;
import vml1337j.sws.rest.api.dto.EventDto;
import vml1337j.sws.rest.api.mapper.EventMapper;
import vml1337j.sws.rest.store.entities.EventEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequiredArgsConstructor
@Component
public class EventAssembler implements RepresentationModelAssembler<EventEntity, EntityModel<EventDto>> {

    private final EventMapper eventMapper;

    @Override
    public EntityModel<EventDto> toModel(EventEntity entity) {
        EntityModel<EventDto> model = EntityModel.of(eventMapper.toEventDto(entity));
        return model.add(
                linkTo(methodOn(EventController.class).getEventById(entity.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).getEventResultsById(entity.getId())).withRel("results")
        );
    }
}
