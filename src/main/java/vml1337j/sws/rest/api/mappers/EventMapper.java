package vml1337j.sws.rest.api.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vml1337j.sws.rest.api.dto.EventDto;
import vml1337j.sws.rest.api.dto.EventResultDto;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(source = "url", target = "eventPageUrl")
    EventDto toEventDto(EventEntity entity);

    List<EventDto> toEventDtoList(List<EventEntity> entities);

    List<EventResultDto> toEventResultDtoList(List<ResultEntity> entity);

    @Mapping(source = "racer.id", target = "racerId")
    @Mapping(source = "racer.firstname", target = "firstname")
    @Mapping(source = "racer.lastname", target = "lastname")
    @Mapping(source = "racer.gender", target = "gender")
    @Mapping(source = "racer.country", target = "country")
    EventResultDto toEventResultDto(ResultEntity entity);
}
