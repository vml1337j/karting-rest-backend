package vml1337j.sws.rest.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vml1337j.sws.rest.api.dto.RacerDto;
import vml1337j.sws.rest.api.dto.RacerResultDto;
import vml1337j.sws.rest.store.entities.RacerEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RacerMapper {

    RacerDto toRacerDto(RacerEntity entity);

    List<RacerDto> toRacerDtoList(List<RacerEntity> entities);

    List<RacerResultDto> toRacerResultDtoList(List<ResultEntity> entities);

    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "event.title", target = "title")
    @Mapping(source = "event.date", target = "date")
    @Mapping(source = "event.category", target = "category")
    RacerResultDto toRacerResultDto(ResultEntity entity);

}
