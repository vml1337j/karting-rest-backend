package vml1337j.sws.rest.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vml1337j.sws.rest.api.exceptions.EventNotFoundException;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.store.repositories.RacerRepository;
import vml1337j.sws.rest.utils.SwsUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RacerRepository racerRepository;
    private final SwsUtils swsUtils;

    public EventEntity getEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with id: %s not found", eventId)
                ));

        if (!event.isFilled()) {
            EventEntity filledEvent = swsUtils.fillEvent(event);
            eventRepository.saveAndFlush(filledEvent);
        }

        return event;
    }

    public List<EventEntity> getEvents() {
        List<EventEntity> events = eventRepository.findAll();
        
        if (events.isEmpty()) {
            throw new EventNotFoundException(
                    String.format("Events not found. Db probably empty")
            );
        }

        List<EventEntity> filledEvents = events.parallelStream()
                .filter(event -> !event.isFilled())
                .map(swsUtils::fillEvent)
                .collect(Collectors.toList());

        eventRepository.saveAllAndFlush(filledEvents);

        return events;
    }

    public List<ResultEntity> getEventResultsById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with id: %s not found", eventId)
                ));

        if (!event.isFilled() || event.getResults().isEmpty()) {
            EventEntity filledEvent = swsUtils.fillEvent(event);
            eventRepository.saveAndFlush(filledEvent);
        }

        List<RacerEntity> filledRacers = event.getRacers().parallelStream()
                .filter(racer -> !racer.isFilled())
                .map(swsUtils::fillRacer)
                .collect(Collectors.toList());

        racerRepository.saveAllAndFlush(filledRacers);

        return event.getResults().stream()
                .sorted(Comparator.comparingInt(ResultEntity::getPosition))
                .collect(Collectors.toList());
    }
}
