package searchengine.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.Lemma;

@Repository
public interface LemmaRepository extends CrudRepository<Lemma, Integer> {
    @Query(value = "SELECT Count(`id`) FROM `lemma` WHERE `site_id` = :siteId", nativeQuery = true)
    int siteLemmasCount(int siteId);

    @Query (value="SELECT * FROM `lemma` WHERE `lemma` = :lemma AND `site_id` = :siteId", nativeQuery = true)
    Lemma lemmaByLemmaAndSite(String lemma, int siteId);


}
