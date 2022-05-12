package vml1337j.sws.rest.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.store.repositories.ResultRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ResultRepository resultRepository;

    public EventEntity getEventById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        return check(event);
    }

    public List<EventEntity> getEvents() {
        return eventRepository.findAll().stream()
                .map(this::check)
                .collect(Collectors.toList());
    }

    public List<ResultEntity> getEventResultsById(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(RuntimeException::new);
        return check(event).getResults();
    }

    // todo rename it
    private EventEntity check(EventEntity event) {
        if (!event.isFilled()) {
            // scrap event from site
            // save filled event
        }

        return event;
    }
}
