package vml1337j.sws.rest.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;

import java.io.IOException;

@Component
public class SwsUtils {

    private final EventScraper eventScraper = new EventScraper();

    private final RacerScraper racerScraper = new RacerScraper();

    public EventEntity fillEvent(EventEntity event) {
        Document eventPage = getPage(event.getUrl());

        EventEntity filledEvent = eventScraper.getEvent(eventPage);
        filledEvent.setFilled(true);

        return filledEvent;
    }

    public RacerEntity fillRacer(RacerEntity racer) {
        Document racerPage = getPage(racer.getProfileUrl());

        RacerEntity filledRacer = racerScraper.getRacer(racerPage);
        filledRacer.setFilled(true);
        filledRacer.setGender(racer.getGender());

        return filledRacer;
    }

    public Document getPage(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new ScraperException(String.format("Invalid url: %s", url));
        }
    }
}
