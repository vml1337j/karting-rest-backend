package vml1337j.sws.rest.utils;

import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EventScraper {

    public static EventEntity getEvent(Document eventPage) {
        String url = getUrl(eventPage);
        EventEntity eventEntity = EventEntity.builder()
                .id(getId(url))
                .url(url)
                .title(getTitle(eventPage))
                .category(getCategory(eventPage))
                .date(getDate(eventPage))
                .numberOfRacers(getNumberOfRacers(eventPage))
                .isFilled(true)
                .build();

        if (!eventEntity.getCategory().equals("Endurance CUP")) {
            eventEntity.addRacers(getRacers(eventPage));
        }

        return eventEntity;
    }

    public static List<EventEntity> getFinishedEventsFromTrack(Document trackPage) {
        Element finished = getEventTables(trackPage).last();
        return getEventFromTable(finished);
    }

    public static List<EventEntity> getFinishedEventsByYear(Document trackPage, int year) {
        Element finished = getEventTables(trackPage).last();
        return filterEventByYear(finished, year);
    }

    public static List<RacerEntity> getRacers(Document eventPage) {
        return getRacerTable(eventPage)
                .parallelStream()
                .map(EventScraper::getRawRacer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private static long getId(String url) {
        Matcher matcher = Pattern.compile("\\d+(?=\\.)").matcher(url);
        if (!matcher.find()) {
            throw new RuntimeException("Illegal url");
        }

        return Long.parseLong(matcher.group());
    }

    private static String getTitle(Document eventPage) {
        return eventPage.select("h1#tmpl-detail-header-title").text();
    }

    private static LocalDate getDate(Document eventPage) {
        return LocalDate.parse(
                eventPage.select("div#page-header-entity-info strong").text()
        );
    }

    private static String getCategory(Document eventPage) {
        return eventPage.select("span.category-badge").text();
    }

    private static int getNumberOfRacers(Document eventPage) {
        return eventPage.select("div#race-detail-results tbody.rowlink tr").size();
    }

    private static String getUrl(Document eventPage) {
        return eventPage.location();
    }

    private static Optional<RacerEntity> getRawRacer(Element row) {
        try {
            String shortUrl = getRacerProfileUrl(row);
            return Optional.of(
                    RacerEntity.builder()
                            .profileUrl(shortUrl)
                            .id(getId(shortUrl))
                            .gender(getRacerGender(row))
                            .isFilled(false)
                            .build()
            );
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private static RacerEntity.Gender getRacerGender(Element it) {
        String attrGender = it.select("i").attr("class");
        String gender = attrGender.replaceAll(".+(?<=-)", "");
        if (gender.equals("mars"))
            return RacerEntity.Gender.MALE;

        return RacerEntity.Gender.FEMALE;
    }

    private static String getRacerProfileUrl(Element it) {
        return it.select("a").attr("href")
                .replaceAll("(?<=drivers/).+(?<=-)", "");
    }

    private static Elements getRacerTable(Document eventPage) {
        return eventPage.select("div#race-detail-results tbody.rowlink tr");
    }

    private static List<EventEntity> getEventFromTable(Element table) {
        return table.select("tr")
                .stream()
                .map(EventScraper::getRawEvent)
                .collect(Collectors.toList());
    }

    private static List<EventEntity> filterEventByYear(Element table, int year) {
        return table.select("tr")
                .stream()
                .filter(tr -> filterByYear(tr, year))
                .map(EventScraper::getRawEvent)
                .collect(Collectors.toList());
    }

    private static EventEntity getRawEvent(Element tr) {
        String url = tr.attr("data-rowlink");
        LocalDate date = LocalDate.parse(tr.select("td").get(1).text());
        return EventEntity.builder()
                .id(getId(url))
                .url(url)
                .date(date)
                .isFilled(false)
                .build();
    }

    private static boolean filterByYear(Element tr, int year) {
        LocalDate date = LocalDate.parse(tr.select("td").get(1).text());
        return date.getYear() == year;
    }

    private static Elements getEventTables(Document trackPage) {
        return trackPage.select("table#track-map-table tbody.rowlink");
    }
}
