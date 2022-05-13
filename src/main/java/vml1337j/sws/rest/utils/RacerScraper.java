package vml1337j.sws.rest.utils;

import vml1337j.sws.rest.store.entities.RacerEntity;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RacerScraper {

    public static RacerEntity getRacer(Document racerPage) {
        String shortProfileUrl = getShortProfileUrl(racerPage);
        return RacerEntity.builder()
                .id(getId(shortProfileUrl))
                .firstname(getFirstname(racerPage))
                .lastname(getLastname(racerPage))
                .swsId(getSwsId(racerPage))
                .age(getAge(racerPage))
                .country(getCountry(racerPage))
                .points(getPoints(racerPage))
                .profileUrl(shortProfileUrl)
                .build();
    }

    private static String getShortProfileUrl(Document racerPage) {
        return racerPage.location().replaceAll("(?<=drivers/).+(?<=-)", "");
    }

    private static long getId(String url) {
        Matcher matcher = Pattern.compile("\\d+(?=\\.)").matcher(url);
        if (!matcher.find()) {
            throw new RuntimeException("Illegal url");
        }

        return Long.parseLong(matcher.group());
    }

    private static String getFirstname(Document racerPage) {
        return capitalize(getRacerInfo(racerPage, 1));
    }

    private static String getLastname(Document racerPage) {
        return capitalize(getRacerInfo(racerPage, 0));
    }

    private static String capitalize(String name) {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }

    private static String getSwsId(Document racerPage) {
        return getRacerInfo(racerPage, 2).substring(2);
    }

    private static int getAge(Document racerPage) {
        return Integer.parseInt(
                getRacerInfo(racerPage, 3).replaceAll("\\D", "")
        );
    }

    private static String getRacerInfo(Document racerPage, int indexElement) {
        return racerPage.select("div#tmpl-detail-header-content-information strong")
                .get(indexElement)
                .nextSibling()
                .toString()
                .trim();
    }

    private static String getCountry(Document racerPage) {
        return racerPage.select("div#tmpl-detail-header-content-information span.country-flag")
                .attr("title");
    }

    private static long getPoints(Document racerPage) {
        String points = racerPage.select("div.tmpl-bloc-stats div")
                .get(0).text();

        if (points.equals("-"))
            return 0;

        return Long.parseLong(points);
    }
}
