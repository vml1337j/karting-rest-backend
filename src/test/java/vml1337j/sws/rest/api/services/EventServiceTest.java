package vml1337j.sws.rest.api.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import vml1337j.sws.rest.api.exceptions.EventNotFoundException;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.store.repositories.RacerRepository;
import vml1337j.sws.rest.utils.SwsUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    RacerRepository racerRepository;

    @Mock
    SwsUtils swsUtils;

    @InjectMocks
    EventService eventService;

    @Test
    void shouldReturnEventWhenExistAndFilled() {
        long eventId = 123L;
        EventEntity expectedEvent = EventEntity.builder().id(eventId).isFilled(true).build();

        when(eventRepository.findById(eventId)).thenReturn(
                Optional.of(EventEntity.builder()
                        .id(eventId)
                        .isFilled(true)
                        .build()
                ));

        EventEntity actualEvent = eventService.getEventById(123L);

        Assertions.assertEquals(expectedEvent.getId(), actualEvent.getId());
        Assertions.assertEquals(expectedEvent.isFilled(), actualEvent.isFilled());

        verify(swsUtils, never()).fillEvent(any());
        verify(eventRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenDidntFindEventById() {
        long eventId = 123L;
        String expectedExceptionMsg = String.format("Event with id: %s not found", eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        EventNotFoundException actualException = Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventService.getEventById(eventId)
        );

        Assertions.assertEquals(expectedExceptionMsg, actualException.getMessage());
    }

    @Test
    void shouldCall_fillEvent_And_saveAndFlush_WhenEventField_isFilled_EqualsFalse() {
        long eventId = 123L;

        when(eventRepository.findById(eventId)).thenReturn(
                Optional.of(EventEntity.builder()
                        .id(eventId)
                        .isFilled(false)
                        .build()
                ));

        EventEntity actualEvent = eventService.getEventById(eventId);

        verify(swsUtils, times(1)).fillEvent(any());
        verify(eventRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldReturnListOfEventsWhenAllExistAndFilled() {
        List<EventEntity> expectedEvents = List.of(
                EventEntity.builder().id(1L).isFilled(true).build()
        );

        when(eventRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(expectedEvents));

        List<EventEntity> actualEvents = eventService.getEvents(Pageable.unpaged()).getContent();

        Assertions.assertNotNull(actualEvents);

        verify(swsUtils, never()).fillEvent(any());
        verify(eventRepository).saveAllAndFlush(eq(Collections.emptyList()));
    }

    @Test
    void shouldCall_fillEvent_And_saveAllAndFlush_WhenSomeEventsAreNotFilled() {
        List<EventEntity> expectedEvents = List.of(
                EventEntity.builder().id(1L).isFilled(true).build(),
                EventEntity.builder().id(2L).isFilled(false).build(),
                EventEntity.builder().id(3L).isFilled(true).build()
        );

        when(eventRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(expectedEvents));

        List<EventEntity> actualEvents = eventService.getEvents(Pageable.unpaged()).getContent();

        Assertions.assertNotNull(actualEvents);

        verify(swsUtils, times(1)).fillEvent(any());
        verify(eventRepository, times(1)).saveAllAndFlush(any());
    }

    @Test
    void shouldThrowEventNotFoundExceptionWhenDbEmpty() {
        String expectedExceptionMsg = "Events not found. Db probably empty";
        when(eventRepository.findAll(Pageable.unpaged())).thenReturn(Page.empty());

        EventNotFoundException actualException = Assertions.assertThrows(
                EventNotFoundException.class,
                () -> eventService.getEvents(Pageable.unpaged())
        );

        Assertions.assertEquals(expectedExceptionMsg, actualException.getMessage());
    }

    @Test
    void shouldReturnListOfResultsInOrderAscPosition() {
        EventEntity event = getEventEntityWithRacersAndResults();

        List<ResultEntity> mixedPositionsResult = List.of(
                new ResultEntity(event.getRacers().get(1), event, 2),
                new ResultEntity(event.getRacers().get(2), event, 3),
                new ResultEntity(event.getRacers().get(0), event, 1)
        );
        event.setResults(mixedPositionsResult);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        List<ResultEntity> actualEventResults = eventService.getEventResultsById(event.getId());

        Assertions.assertEquals(1, actualEventResults.get(0).getPosition());
        Assertions.assertEquals(2, actualEventResults.get(1).getPosition());
        Assertions.assertEquals(3, actualEventResults.get(2).getPosition());

        verify(swsUtils, never()).fillEvent(any());
        verify(eventRepository, never()).saveAllAndFlush(any());

        verify(swsUtils, never()).fillRacer(any());
        verify(racerRepository, times(1)).saveAllAndFlush(Collections.emptyList());
    }

    @Test
    void shouldCall_fillEvent_And_saveAndFlush_WhenEventIsNotFilled() {
        EventEntity event = getEventEntityWithRacersAndResults();

        event.setFilled(false);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        List<ResultEntity> actualEventResults = eventService.getEventResultsById(event.getId());

        verify(swsUtils, times(1)).fillEvent(any());
        verify(eventRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldCall_fillEvent_And_saveAndFlush_WhenResultsAreEmpty() {
        EventEntity event = getEventEntityWithRacersAndResults();

        event.setResults(Collections.emptyList());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        List<ResultEntity> actualEventResults = eventService.getEventResultsById(event.getId());

        verify(swsUtils, times(1)).fillEvent(any());
        verify(eventRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldCall_fillRacer_And_saveAllAndFlush_WhenSomeRacersAreNotFilled() {
        EventEntity event = getEventEntityWithRacersAndResults();

        event.getRacers().get(0).setFilled(false);
        event.getRacers().get(1).setFilled(false);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        List<ResultEntity> actualEventResult = eventService.getEventResultsById(event.getId());

        verify(swsUtils, times(2)).fillRacer(any());
        verify(racerRepository, times(1)).saveAllAndFlush(any());
    }

    private EventEntity getEventEntityWithRacersAndResults() {
        long eventId = 123L;
        EventEntity event = EventEntity.builder().id(eventId).isFilled(true).build();
        RacerEntity racer1 = RacerEntity.builder().id(123L).isFilled(true).build();
        RacerEntity racer2 = RacerEntity.builder().id(123L).isFilled(true).build();
        RacerEntity racer3 = RacerEntity.builder().id(123L).isFilled(true).build();

        List<ResultEntity> expectedEventResults = List.of(
                new ResultEntity(racer1, event, 1),
                new ResultEntity(racer2, event, 2),
                new ResultEntity(racer3, event, 3)
        );
        event.setResults(expectedEventResults);

        return event;
    }
}
