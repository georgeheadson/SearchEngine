package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Site;

import java.util.List;

@Repository
public interface SiteRepository extends CrudRepository<Site, Integer> {
    @Query (value = "SELECT * FROM `site`", nativeQuery = true)
    List<Site> getAllSites();

    @Query(value = "SELECT * FROM `site` WHERE `url` = :url", nativeQuery = true)
    Site siteByUrl(String url);
}
