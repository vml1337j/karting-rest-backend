package vml1337j.sws.rest.api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import vml1337j.sws.rest.api.exception.RacerNotFoundException;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.store.repositories.RacerRepository;
import vml1337j.sws.rest.utils.SwsUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RacerServiceTest {

    @Mock
    RacerRepository racerRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    SwsUtils swsUtils;

    @InjectMocks
    RacerService racerService;

    @Test
    void shouldReturnRacerWhenExistAndFilled() {
        RacerEntity racer = RacerEntity.builder().id(123L).isFilled(true).build();

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        RacerEntity actualRacer = racerService.getRacerById(racer.getId());

        assertEquals(racer.getId(), actualRacer.getId());
        assertEquals(racer.isFilled(), actualRacer.isFilled());

        verify(swsUtils, never()).fillRacer(any());
        verify(racerRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowRacerNotFoundExceptionWhenDidntFindRacerById() {
        long racerId = 123L;
        String expectedExceptionMsg = String.format("Racer with id: %s not found", racerId);

        when(racerRepository.findById(racerId)).thenReturn(Optional.empty());

        RacerNotFoundException actualException = assertThrows(
                RacerNotFoundException.class,
                () -> racerService.getRacerById(racerId)
        );

        assertEquals(expectedExceptionMsg, actualException.getMessage());
    }

    @Test
    void shouldCall_fillEvent_And_saveAndFlush_WhenRacerIsNotFilled() {
        RacerEntity racer = RacerEntity.builder().id(123L).isFilled(false).build();

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        RacerEntity actualRacer = racerService.getRacerById(racer.getId());

        verify(swsUtils, times(1)).fillRacer(any());
        verify(racerRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldReturnListOfRacerWhenAllExistsAndFilled() {
        List<RacerEntity> racers = List.of(
                RacerEntity.builder().id(123L).isFilled(true).build(),
                RacerEntity.builder().id(123L).isFilled(true).build(),
                RacerEntity.builder().id(123L).isFilled(true).build()
        );

        when(racerRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(racers));

        List<RacerEntity> actualRacers = racerService.getRacers(Pageable.unpaged()).getContent();

        assertNotNull(actualRacers);

        verify(swsUtils, never()).fillRacer(any());
        verify(racerRepository, times(1)).saveAllAndFlush(Collections.emptyList());
    }

    @Test
    void shouldCall_fillRacer_And_saveAllAndFlush_WhenSomeRacersAreNotFilled() {
        List<RacerEntity> racers = List.of(
                RacerEntity.builder().id(123L).isFilled(true).build(),
                RacerEntity.builder().id(123L).isFilled(false).build(),
                RacerEntity.builder().id(123L).isFilled(false).build()
        );

        when(racerRepository.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(racers));

        List<RacerEntity> actualRacers = racerService.getRacers(Pageable.unpaged()).getContent();

        verify(swsUtils, times(2)).fillRacer(any());
        verify(racerRepository, times(1)).saveAllAndFlush(any());
    }

    @Test
    void shouldThrowRacerNotFoundWhenDbEmpty() {
        String expectedExceptionMsg = "Racers not found. Db probably empty";

        when(racerRepository.findAll(Pageable.unpaged())).thenReturn(Page.empty());

        RacerNotFoundException actualException = assertThrows(
                RacerNotFoundException.class,
                () -> racerService.getRacers(Pageable.unpaged())
        );

        assertEquals(expectedExceptionMsg, actualException.getMessage());
    }

    @Test
    void shouldReturnRacerResultsWhenExistsAndFilled() {
        RacerEntity racer = getRacerEntityWithEventsAndResults();

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        List<ResultEntity> actualRacerResults = racerService.getRacerResults(racer.getId());

        assertNotNull(actualRacerResults);

        verify(swsUtils, never()).fillRacer(any());
        verify(racerRepository, never()).saveAndFlush(any());

        verify(swsUtils, never()).fillEvent(any());
        verify(eventRepository, times(1)).saveAllAndFlush(Collections.emptyList());
    }

    @Test
    void shouldCall_fillRacer_And_saveAndFlush_WhenRacerIsNotFilled() {
        RacerEntity racer = getRacerEntityWithEventsAndResults();
        racer.setFilled(false);

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        List<ResultEntity> actualRacerResults = racerService.getRacerResults(racer.getId());

        verify(swsUtils, times(1)).fillRacer(any());
        verify(racerRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldCall_fillRacer_And_saveAndFlush_WhenRacerResultsAreEmpty() {
        RacerEntity racer = getRacerEntityWithEventsAndResults();
        racer.setResults(Collections.emptyList());

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        List<ResultEntity> actualRacerResults = racerService.getRacerResults(racer.getId());

        verify(swsUtils, times(1)).fillRacer(any());
        verify(racerRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void shouldCall_fillRacer_And_saveAllAndFlush_WhenSomeEventsAreNotFilled() {
        RacerEntity racer = getRacerEntityWithEventsAndResults();

        racer.getEvents().get(0).setFilled(false);
        racer.getEvents().get(1).setFilled(false);

        when(racerRepository.findById(racer.getId())).thenReturn(Optional.of(racer));

        List<ResultEntity> actualRacerResults = racerService.getRacerResults(racer.getId());

        verify(swsUtils, times(2)).fillEvent(any());
        verify(eventRepository, times(1)).saveAllAndFlush(any());
    }

    private RacerEntity getRacerEntityWithEventsAndResults() {
        RacerEntity racer = RacerEntity.builder().id(123L).isFilled(true).build();

        EventEntity event1 = EventEntity.builder().id(1L).isFilled(true).build();
        EventEntity event2 = EventEntity.builder().id(1L).isFilled(true).build();
        EventEntity event3 = EventEntity.builder().id(1L).isFilled(true).build();

        List<ResultEntity> results = List.of(
                new ResultEntity(racer, event1, 1),
                new ResultEntity(racer, event2, 2),
                new ResultEntity(racer, event3, 3)
        );
        racer.setResults(results);

        return racer;
    }
}