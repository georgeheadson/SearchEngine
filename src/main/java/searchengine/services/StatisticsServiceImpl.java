package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.repositories.Repositories;
import searchengine.model.Status;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final SitesList sites;
    private final Repositories repositories;

    @Override
    public StatisticsResponse getStatistics() {

        TotalStatistics total = new TotalStatistics();
        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        List<Site> sitesList = sites.getSites();

        boolean indexing = false;
        for(int i = 0; i < sitesList.size(); i++) {
            Site site = sitesList.get(i);
            DetailedStatisticsItem item = new DetailedStatisticsItem();
            item.setName(site.getName());
            item.setUrl(site.getUrl());
            int pages = 0;
            int lemmas = 0;
            Status status = Status.FAILED;
            long statusTime = 0;
            String errors = "";
            searchengine.model.Site siteInDB = repositories.getSiteRepository().siteByUrl(site.getUrl());
            if (siteInDB != null) {
                pages = repositories.getPageRepository().sitePagesCount(siteInDB.getId());
                lemmas = repositories.getLemmaRepository().siteLemmasCount(siteInDB.getId());
                status = siteInDB.getStatus();
                ZonedDateTime zdt = ZonedDateTime.of(siteInDB.getStatusTime(), ZoneId.systemDefault());
                statusTime = zdt.toInstant().toEpochMilli();
                errors = siteInDB.getLastError();
                indexing = status.name() == Status.INDEXING.name() ? true : false;
            }

            item.setPages(pages);
            item.setLemmas(lemmas);
            item.setStatus(status.name());
            item.setError(errors);
            item.setStatusTime(statusTime);

            total.setSites(total.getSites() + 1);
            total.setPages(total.getPages() + pages);
            total.setLemmas(total.getLemmas() + lemmas);
            detailed.add(item);
        }

        total.setIndexing(indexing);

        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }
}
