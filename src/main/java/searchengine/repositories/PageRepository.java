package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Page;

@Repository
public interface PageRepository extends CrudRepository<Page, Integer> {
    @Query(value = "SELECT Count(`id`) FROM `page` WHERE `site_id` = :siteId", nativeQuery = true)
    int sitePagesCount(int siteId);

    @Query (value="SELECT `id` FROM `page` WHERE `path` = :path AND `site_id` = :siteId", nativeQuery = true)
    Integer pageIdByUrlAndSite(String path, int siteId);
}
