package vml1337j.sws.rest.utils;

import vml1337j.sws.rest.store.entities.RacerEntity;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RacerScraperTest {

    @Test
    void shouldReturnRacerFromPage() {
        Document mockDoc = mock(Document.class);

        RacerEntity expectedRacer = RacerEntity.builder()
                .id(1234L)
                .profileUrl("https://www.test.com/lang/drivers/1234.html")
                .lastname("Last")
                .firstname("First")
                .swsId("TEST-CODE-1234")
                .age(18)
                .country("Country")
                .points(1234L)
                .build();

        when(mockDoc.location()).thenReturn("https://www.test.com/lang/drivers/full-name-racer-1234.html");

        when(mockDoc.select("div#tmpl-detail-header-content-information strong"))
                .thenReturn(new Elements(
                        setRacerInfo(
                                "Last",
                                "First",
                                "TEST-CODE-1234",
                                18
                        )));

        when(mockDoc.select("div#tmpl-detail-header-content-information span.country-flag"))
                .thenReturn(new Elements(
                        setCountry("Country")
                ));

        when(mockDoc.select("div.tmpl-bloc-stats div"))
                .thenReturn(new Elements(
                        setPoints(1234L)
                ));

        RacerEntity actualRacer = RacerScraper.getRacer(mockDoc);
        assertEquals(expectedRacer, actualRacer);
    }

    private Elements setRacerInfo(String lastname, String firstname, String swsId, int age) {
        return new Element("div").id("tmpl-detail-header-content-information")
                .html(String.format(
                        " <strong>Фамилия :</strong> %s\n" +
                                " <strong>Имя :</strong> %s\n" +
                                " <strong>ID</strong> : %s\n" +
                                " <strong>Возраст</strong> : %d лет\n",
                        lastname, firstname, swsId, age)
                ).children();
    }

    private Element setCountry(String country) {
        return new Element("span")
                .attr("class", "country-flag")
                .attr("title", country);
    }

    private Element setPoints(long points) {
        return new Element("div").attr("class", "tmpl-bloc-stats-content")
                .html(String.valueOf(points));
    }
}
