package vml1337j.sws.rest.api.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import vml1337j.sws.rest.api.exceptions.EventNotFoundException;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.utils.SwsUtils;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventSchedulingService {

    // todo add trackUrl value event scheduling task
//    @Value()
    private final String trackUrl = "https://www.sodiwseries.com/ru-ru/tracks/mayak-71.html";

    private final EventRepository eventRepository;
    private final TaskScheduler taskScheduler;
    private final SwsUtils swsUtils;

    @PostConstruct
    public void getNextScheduledEvent() {
        List<EventEntity> nextScheduledEvents = swsUtils.getNextScheduledEvent(trackUrl);

        if (nextScheduledEvents.isEmpty()) {
            throw new EventNotFoundException("Scheduled events not found");
        }

        taskScheduler.schedule(() -> fillAndSaveEvents(nextScheduledEvents),
                Date.valueOf(nextScheduledEvents.get(0).getDate())
        );
    }

    private void fillAndSaveEvents(List<EventEntity> events) {
        List<EventEntity> filledEvents = events.stream()
                .map(swsUtils::fillEvent)
                .collect(Collectors.toList());

        eventRepository.saveAllAndFlush(filledEvents);
        getNextScheduledEvent();
    }
}
