package vml1337j.sws.rest.utils;

import vml1337j.sws.rest.store.entities.EventEntity;
import vml1337j.sws.rest.store.entities.RacerEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventScraperTest {

    @Test
    void shouldReturnEventFromEventPage() {
        Document eventPage = mock(Document.class);

        EventEntity expectedEvent = EventEntity.builder()
                .id(123L)
                .title("Event title")
                .date(LocalDate.parse("1970-01-01"))
                .category("Event category")
                .numberOfRacers(1)
                .url("https://www.test.com/test-path/test-123.html")
                .isFilled(true)
                .build();

        expectedEvent.addRacers(
                List.of(RacerEntity.builder()
                        .id(1234L)
                        .profileUrl("https://www.test.com/lang/drivers/1234.html")
                        .gender(RacerEntity.Gender.MALE)
                        .build()
                ));

        when(eventPage.location()).thenReturn("https://www.test.com/test-path/test-123.html");

        when(eventPage.select("h1#tmpl-detail-header-title"))
                .thenReturn(new Elements(
                        setTitle("Event title")
                ));

        when(eventPage.select("div#page-header-entity-info strong"))
                .thenReturn(new Elements(
                        setDate("1970-01-01")
                ));

        when(eventPage.select("span.category-badge"))
                .thenReturn(new Elements(
                        setCategory("Event category")
                ));

        when(eventPage.select("div#race-detail-results tbody.rowlink tr"))
                .thenReturn(new Elements(
                        setRacerTable(
                                "https://www.test.com/lang/drivers/1234.html",
                                "fa-mars",
                                1
                        )));

        EventEntity actualEvent = EventScraper.getEvent(eventPage);
        assertEquals(expectedEvent, actualEvent);
    }

    private Element setTitle(String title) {
        return new Element("h1").id("tmpl-detail-header-title")
                .text(String.format("%s", title));
    }

    private Element setCategory(String category) {
        return new Element("div")
                .attr("class", "race-detail-info-bloc")
                .append(String.format(
                        "<span class=\"category-badge\">%s</span>", category
                ));
    }

    private Element setDate(String date) {
        return new Element("div").id("page-header-entity-info")
                .append(String.format("<span>%s</span>", date));
    }

    private Element setRacerTable(String racerUrl, String attrGender, int tableSize) {
        String rawRacer = String.format(
                " <tr>\n" +
                        "  <td> " +
                        "   <a href=\"%s\">...</a> " +
                        "   <i class=\"fa %s\"></i>" +
                        "  </td>\n" +
                        " </tr>\n",
                racerUrl, attrGender
        );

        Element table = new Element("tbody").attr("class", "rowlink");
        for (int i = 0; i < tableSize; i++) {
            table.append(rawRacer);
        }

        return new Element("div").id("race-detail-results").appendChild(table);
    }

    @Test
    void shouldReturnListWithThreeFinishedEventsFromTrackPage() {
        Document trackPage = mock(Document.class);

        when(trackPage.select("table#track-map-table tbody.rowlink"))
                .thenReturn(new Elements(
                        setTrackTable(
                                "https://www.test.com/test-path/test-123.html",
                                "1970-01-01",
                                3
                        )));

        List<EventEntity> finishedEvents = EventScraper.getFinishedEventsFromTrack(trackPage);
        assertEquals(3, finishedEvents.size());
    }

    @Test
    void shouldReturnListWithThreeFinishedEventsFromTrackPageByYear() {
        Document trackPage = mock(Document.class);

        when(trackPage.select("table#track-map-table tbody.rowlink"))
                .thenReturn(new Elements(
                        setTrackTable(
                                "https://www.test.com/test-path/test-123.html",
                                "1970-01-01",
                                3
                        )));

        List<EventEntity> finishedEvents = EventScraper.getFinishedEventsByYear(trackPage, 1970);
        assertEquals(3, finishedEvents.size());
    }

    private Element setTrackTable(String url, String date, int size) {
        String event = String.format(
                "<tbody class=\"rowlink\"> \n" +
                        " <tr data-rowlink=\"%s\"> \n" +
                        "  <td></td> \n" +
                        "  <td>%s</td> \n" +
                        "  <td></td> \n" +
                        "  <td></td> \n" +
                        "  <td></td> \n" +
                        " </tr>\n" +
                        "</tbody>",
                url, date);

        Element table = new Element("table").id("track-map-table");
        for (int i = 0; i < size; i++) {
            table.append(event);
        }

        return table;
    }
}
