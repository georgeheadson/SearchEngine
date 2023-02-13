package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexingServiceImpl implements IndexingService {

    @Override
    public String startIndexing() {
        return null;
    }

    @Override
    public String stopIndexing() {
        return null;
    }
}
