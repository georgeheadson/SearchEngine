package searchengine.services;

import searchengine.dto.statistics.StatisticsResponse;

public interface IndexingService {
    StatisticsResponse startIndexing() throws InterruptedException;
    StatisticsResponse stopIndexing();
}
