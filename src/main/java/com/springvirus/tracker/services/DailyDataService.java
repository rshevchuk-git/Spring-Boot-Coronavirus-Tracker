package com.springvirus.tracker.services;

import com.springvirus.tracker.models.LocationStatistics;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class DailyDataService {

    private static final String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static final String DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";

    private List<LocationStatistics> stats = new ArrayList<>();

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void fetchNewData() throws IOException, InterruptedException {

        //NEW STATISTICS LIST NOT TO DISTURB THE MAIN ONE WHILE FETCHING
        List<LocationStatistics> newStats = new ArrayList<>();

        //SET UP HTTP REQUEST
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DATA_URL))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // PARSE TOTAL CASES DATA
        StringReader in = new StringReader(response.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        for (CSVRecord rec : records){

            LocationStatistics stat = new LocationStatistics();

            stat.setCountryName(rec.get("Country/Region").equals("US") ? "United States" : rec.get("Country/Region"));
            stat.setLatestCases(Integer.parseInt(rec.get(rec.size() - 1)));

            int currentCases = Integer.parseInt(rec.get(rec.size() - 1));
            int prevCases = Integer.parseInt(rec.get(rec.size() - 2));
            stat.setDeltaOfDay(currentCases-prevCases);

            newStats.add(stat);

        }

        //SET UP HTTP REQUEST
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder()
                .uri(URI.create(DEATH_URL))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());


        // PARSE TOTAL DEATHS DATA
        in = new StringReader(response.body());
        records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);

        int index = 0;
        for(CSVRecord rec : records){

            newStats.get(index).setLatestDeaths(Integer.parseInt(rec.get(rec.size() - 1)));

            int currentDeaths = Integer.parseInt(rec.get(rec.size() - 1));
            int prevDeaths = Integer.parseInt(rec.get(rec.size() - 2));
            newStats.get(index).setDeltaOfDeaths(currentDeaths-prevDeaths);

            index++;
        }

        // REPLACE MAIN STATISTICS LIST
        this.stats = newStats;

    }

    public List<LocationStatistics> getAllCases(){

        return stats.stream()
                .collect(Collectors.groupingBy(LocationStatistics::getCountryName, Collectors.reducing(new LocationStatistics(), DailyDataService::combineStats)))
                .values().stream()
                .sorted(Comparator.comparing(LocationStatistics::getLatestCases).reversed())
                .collect(Collectors.toList());
    }

    public LocationStatistics getByCountry(String countryName){

        return stats.stream()
                .filter(locationStatistics -> locationStatistics.getCountryName().equals(countryName))
                .reduce(new LocationStatistics(), DailyDataService::combineStats);
    }

    private static LocationStatistics combineStats(LocationStatistics l1, LocationStatistics l2){

        return new LocationStatistics(l2.getCountryName(),
                (l1.getLatestCases() + l2.getLatestCases()),
                (l1.getDeltaOfDay() + l2.getDeltaOfDay()),
                (l1.getLatestDeaths() + l2.getLatestDeaths()),
                (l2.getDeltaOfDeaths() + l2.getDeltaOfDeaths()));
    }

    public int getTotalCases(){

        return stats.stream()
                .mapToInt(LocationStatistics::getLatestCases)
                .sum();
    }

    public int getNewCases(){

        return stats.stream()
                .mapToInt(LocationStatistics::getDeltaOfDay)
                .sum();
    }

    public Set<String> getCountryNames(){

        return stats.stream()
                .collect(Collectors.groupingBy(LocationStatistics::getCountryName))
                .keySet();
    }

}
