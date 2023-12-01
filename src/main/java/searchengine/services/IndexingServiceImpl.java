package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.repositories.Repositories;
import searchengine.config.ConnectionConfig;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {
    private final SitesList sites;
    private final Repositories repositories;
    private final ConnectionConfig connectionConfig;

    private static final String RESULT_OK = "{\n" +
            "\t'result': true\n" +
            "}\n";

    private static final String INDEX_ALREADY_STARTED = "{\n" +
            "\t'result': false,\n" +
            "\t'error': \"Индексация уже запущена\"\n" +
            "}\n";

    private static final String INDEX_NOT_STARTED = "{\n" +
            "\t'result': false,\n" +
            "\t'error': \"Индексация не запущена\"\n" +
            "}\n";

    @Override
    public StatisticsResponse startIndexing() throws InterruptedException {
        TotalStatistics total = new TotalStatistics();
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<searchengine.config.Site> sitesList = sites.getSites();

        HashMap<String, Thread> threadMap = new HashMap<>();
        for (searchengine.config.Site site : sitesList) {
            SiteIndexer siteIndexer = new SiteIndexer(site, repositories, connectionConfig);
            threadMap.put(site.getUrl(), siteIndexer);
            siteIndexer.start();
        }

        threadMap.forEach((url, thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                //!!!
            }
        });

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();

        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }

    @Override
    public StatisticsResponse stopIndexing() {
        ForkJoinPool.commonPool().shutdownNow();

        List<Site> sites =  repositories.getSiteRepository().getAllSites();
        for (Site site : sites) {
            if (site.getStatus() == Status.INDEXING) {
                site.setStatus(Status.FAILED);
                site.setLastError("Индексация остановлена пользователем");
                repositories.getSiteRepository().save(site);
            }
        }

        return null;
    }



}
