package vml1337j.sws.rest.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vml1337j.sws.rest.api.exceptions.RacerNotFoundException;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import vml1337j.sws.rest.store.entities.ResultEntity;
import vml1337j.sws.rest.store.repositories.EventRepository;
import vml1337j.sws.rest.store.repositories.RacerRepository;
import vml1337j.sws.rest.utils.SwsUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RacerService {

    private final RacerRepository racerRepository;
    private final EventRepository eventRepository;
    private final SwsUtils swsUtils;

    public RacerEntity getRacerById(Long racerId) {
        RacerEntity racer = racerRepository.findById(racerId)
                .orElseThrow(() -> new RacerNotFoundException(
                        String.format("Racer with id: %s not found", racerId)
                ));

        if (!racer.isFilled()) {
            RacerEntity filledRacer = swsUtils.fillRacer(racer);
            racerRepository.saveAndFlush(filledRacer);
        }

        return racer;
    }

    public Page<RacerEntity> getRacers(Pageable pageable) {
        Page<RacerEntity> racers = racerRepository.findAll(pageable);

        if (racers.isEmpty()) {
            throw new RacerNotFoundException("Racers not found. Db probably empty");
        }

        List<RacerEntity> filledRacers = racers.toList().parallelStream()
                .filter(racer -> !racer.isFilled())
                .map(swsUtils::fillRacer)
                .collect(Collectors.toList());

        racerRepository.saveAllAndFlush(filledRacers);

        return racers;
    }

    public List<ResultEntity> getRacerResults(Long racerId) {
        RacerEntity racer = racerRepository.findById(racerId)
                .orElseThrow(() -> new RacerNotFoundException(
                        String.format("Racer with id: %s not found", racerId)
                ));

        if (!racer.isFilled() || racer.getResults().isEmpty()) {
            RacerEntity filledRacer = swsUtils.fillRacer(racer);
            racerRepository.saveAndFlush(filledRacer);
        }

        List<EventEntity> filledEvents = racer.getEvents().parallelStream()
                .filter(event -> !event.isFilled())
                .map(swsUtils::fillEvent)
                .collect(Collectors.toList());

        eventRepository.saveAllAndFlush(filledEvents);

        return racer.getResults();
    }
}
